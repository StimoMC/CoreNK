package net.core.commands.team;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import eu.pixelstudios.cloudbridge.network.Network;
import eu.pixelstudios.cloudbridge.network.packet.impl.normal.PlayerKickPacket;
import net.core.Options;
import net.core.corePlayer.CorePlayer;
import net.core.database.Database;
import net.core.manager.banManager.Bans;
import net.core.utils.Utils;

import java.util.Objects;

public class BanCommand extends Command {

    public BanCommand(String s) {
        super(s);
        this.setPermission("core.command.ban");
        this.setDescription("Use: /ban <player> <id>");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!this.testPermission(commandSender)) return false;
        String bannedBy = commandSender.getName();

        if (strings.length < 2){
            commandSender.sendMessage(this.getDescription());
            return false;
        }

        String playerName = strings[0];
        if (playerName.equalsIgnoreCase(bannedBy)){
            commandSender.sendMessage(Options.prefix + "§cYou can't ban yourself.");
            return false;
        }

        if (!Database.exists("bans", "name", playerName)){
            commandSender.sendMessage(Options.prefix + "§cThis user don't exists.");
            return false;
        }

        Player player = Server.getInstance().getPlayerExact(playerName);
        if (player != null){
            if (player.hasPermission("core.ban.ignore")){
                commandSender.sendMessage(Options.prefix + "§cYou can't ban this player.");
                return false;
            }
        }

        if (Utils.isNumeric(strings[1])) {
            int banId = Integer.parseInt(strings[1]);
            if (Bans.existsBanId(banId)) {
                if (Objects.equals(Bans.getBanTypeById(banId), "ban")){
                    if (Bans.isBanned(playerName)){
                        commandSender.sendMessage(Options.prefix + "§cThis player is already banned.");
                        return false;
                    }
                } else if (Objects.equals(Bans.getBanTypeById(banId), "mute")) {
                    if (Bans.isMuted(playerName)){
                        commandSender.sendMessage(Options.prefix + "§cThis player is already muted.");
                        return false;
                    }
                }

                Bans.banPlayer(playerName, bannedBy, banId);
                if (Objects.equals(Bans.getBanTypeById(banId), "ban")) {
                    Network.getInstance().sendPacket(new PlayerKickPacket(playerName, "§cYou were banned."));
                    commandSender.sendMessage(Options.prefix + "§aYou have banned the player §e" + playerName + " §abecause of §c" + Bans.getBanReasonById(banId) + "§7.");
                } else if (Objects.equals(Bans.getBanTypeById(banId), "mute")) {
                    commandSender.sendMessage(Options.prefix + "§aYou have muted the player §e" + playerName + " §abecause of §c" + Bans.getBanReasonById(banId) + "§7.");
                }
            } else {
                commandSender.sendMessage(Options.prefix + "§cThis ban id don't exists.");
            }
        }
        return false;
    }
}
