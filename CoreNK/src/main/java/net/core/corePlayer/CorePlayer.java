package net.core.corePlayer;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.UpdateAbilitiesPacket;
import net.core.Core;
import net.core.Options;
import net.core.database.Database;
import net.core.manager.groupManager.Groups;
import net.core.manager.groupManager.Nicks;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CorePlayer extends Player {

    protected boolean joinSequenceFinished = false;

    public CorePlayer(SourceInterface sourceInterface, Long aLong, InetSocketAddress inetSocketAddress) {
        super(sourceInterface, aLong, inetSocketAddress);
        this.setGamemode(this.getGamemode());
    }

    public void register(String table, String where, String search) {
        if (!playerExists(table, where, search)) {
            Database.update("INSERT INTO " + table + " (name, xuid, coins, rank, rankbefore, rankduration, nickname, permissions) VALUES ('" + this.getName().toLowerCase() + "', '" + this.getUniqueId().toString() + "', 0, '" + Groups.getDefaultGroup() + "', 'null', '0', 'null', '');");
        }
    }

    public boolean playerExists(String table, String where, String search) {
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + table + " WHERE " + where + " = '" + search + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("name") != null;
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return false;
        }
        return false;
    }

    public String getJoinedAddress(){
        String address;
        if (this.getLoginChainData().getRawData().get("ServerAddress").getAsString() != null){
            address = this.getLoginChainData().getRawData().get("ServerAddress").getAsString();
        } else {
            address = "StimoMC.net:19132";
        }
        return address;
    }

    @Override
    public String getName() {
        if (this.username != null) {
            return this.username.replace(" ", "_");
        } else {
            return super.getName();
        }
    }

    public void setJoinSequenceFinished(boolean joinSequenceFinished) {
        this.joinSequenceFinished = joinSequenceFinished;
        if (joinSequenceFinished){
            Server.getInstance().getLogger().info("Player: " + this.getName() + " joined successfully.");
        }
    }

    public boolean getJoinSequenceFinished() {
        return this.joinSequenceFinished;
    }

    public String getOriginalAddress() {
        return this.getLoginChainData().getRawData().get("Waterdog_IP").getAsString();
    }

    public String getXUID(){
        return this.getLoginChainData().getRawData().get("Waterdog_XUID").getAsString();
    }

    @Override
    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        double force = 0.4;
        base = 0.4;

        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }
        f = 1 / f;

        double motionX = this.motionX / 2;
        double motionY = this.motionY / 2;
        double motionZ = this.motionZ / 2;
        motionX += x * f * force;
        motionY += force;
        motionZ += z * f * force;

        if (motionY > force) {
            motionY = force;
        }

        if (Options.reduceKnockback) {
            if (this.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                if (attacker instanceof CorePlayer) {
                    if (((CorePlayer) attacker).getInventory().getItemInHand().hasEnchantment(Enchantment.ID_KNOCKBACK)) {
                        this.customKnockBack(attacker, damage, x, z, base);
                        return;
                    }
                }
            }
        }
        this.setMotion(new Vector3(motionX, motionY, motionZ));
    }

    public void customKnockBack(Entity attacker, double damage, double x, double z, double base) {
        double force = 0.4;
        base = 0.4;

        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        f = 1 / f;

        double motionX = this.motionX / 2;
        double motionY = this.motionY / 2;
        double motionZ = this.motionZ / 2;
        motionX += x * f * force;
        motionY += force;
        motionZ += z * f * force;

        if (motionY > force) {
            motionY = force;
        }

        if (Options.reduceKnockback){
            if (attacker instanceof CorePlayer){
                double knockbackLevel = 0.4;
                if (attacker.isSprinting()){
                    knockbackLevel = knockbackLevel + 0.15;
                }

                if (this.isSprinting()){
                    knockbackLevel = knockbackLevel - 0.3;
                }

                Enchantment enchKnock = ((CorePlayer) attacker).getInventory().getItemInHand().getEnchantment(Enchantment.ID_KNOCKBACK);
                if (enchKnock != null){
                    int add = enchKnock.getLevel() / 14;
                    knockbackLevel = knockbackLevel + add;
                }

                Vector3 targetVelo = new Vector3(motionX, motionY, motionZ).divide(2);
                Vector3 v = targetVelo.add(
                        -Math.sin(attacker.getLocation().getYaw() * Math.PI / 180.0) * knockbackLevel,
                        0.268,
                        Math.cos(attacker.getLocation().getYaw() * Math.PI / 180.0) * knockbackLevel
                );
                this.resetFallDistance();
                this.setMotion(v);
            }
        }
    }

    public void resetHitbox(){
        this.setScale(this.getScale());
        this.recalculateBoundingBox();
    }

    public String getNickName(){
        return Nicks.getName(this);
    }
}
