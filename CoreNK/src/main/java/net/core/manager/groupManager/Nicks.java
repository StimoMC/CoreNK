package net.core.manager.groupManager;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.Config;
import eu.pixelstudios.cloudbridge.api.CloudAPI;
import net.core.Options;
import net.core.corePlayer.CorePlayer;
import net.core.database.Database;
import net.core.utils.Utils;
import okhttp3.internal.Util;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Nicks {

    private static final Random random = new Random();

    public static Object get(final String select, final String database, final String where, final String where_result) {
        final ResultSet rs = Database.getResult("SELECT " + select + " FROM " + database + " WHERE " + where + " = '" + where_result + "';");
        try {
            assert rs != null;
            if (rs.next()) {
                return rs.getObject(select);
            }
        }
        catch (SQLException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
        return null;
    }

    public static boolean existsNickFile(String name) {
        return new File("/home/debian/mcpe/.data/nicks/nicks.yml").exists();
    }

    public static List<String> getNicks() {
        return new Config("/home/debian/mcpe/.data/nicks/nicks.yml", Config.YAML).getStringList("Nicks");
    }

    public static String getRandomNickname() {
        try {
            return getNicks().get(random.nextInt(getNicks().size()));
        } catch (Exception ignored){
            return null;
        }
    }

    public void nickPlayer(Player player){
        String nickName = Nicks.getRandomNickname();
        if (nickName != null) {
            if (!(player instanceof CorePlayer)) return;

            Database.update("UPDATE users SET `nickname` = '" + nickName + "' WHERE `name` = '" + player.getName() + "'");

            player.sendMessage(Options.stimoPrefix + "§7You are now nicked.");
            player.setNameTag(Groups.getNameTag(Objects.requireNonNull(Groups.getNickedGroup())).replace("{name}", ((CorePlayer) player).getNickName()));
            player.setDisplayName(Groups.getNameTag(Objects.requireNonNull(Groups.getNickedGroup())).replace("{name}", ((CorePlayer) player).getNickName()));

            setRandomSkin(player);
        } else {
            player.sendMessage(Options.prefix + "§cError whilst nicking. Please report this to an Administrator!");
        }
    }

    public void unnickPlayer(Player player){
        Database.update("UPDATE users SET `nickname` = 'null' WHERE `name` = '" + player.getName() + "'");

        player.sendMessage(Options.stimoPrefix + "§cYou are no longer nicked.");
        player.setNameTag(Groups.getNameTag(Objects.requireNonNull(Groups.getPlayerGroup(player.getName()))).replace("{name}", player.getName()));
        player.setDisplayName(Groups.getNameTag(Objects.requireNonNull(Groups.getPlayerGroup(player.getName()))).replace("{name}", player.getName()));

        setDefaultSkin(player);
    }

    public static String getPlayerNickname(final String name) {
        return String.valueOf(get("nickname", "users", "name", name));
    }

    public static boolean isNicked(final String name){
        return !getPlayerNickname(name).equals("null") && get("nickname", "users", "name", name) != null;
    }

    public static String getName(Player player){
        return (isNicked(player.getName()) ? getPlayerNickname(player.getName()) : player.getName());
    }

    public static void setRandomSkin(Player player){
        List<Skin> skinList = new ArrayList<>(Utils.skins.values());
        Random random = new Random();
        int randomIndex = random.nextInt(skinList.size());
        Skin randomSkin = skinList.get(randomIndex);
        if (randomSkin != null){
            player.setSkin(randomSkin);
        }
    }

    public static void setDefaultSkin(Player player){
        if (Utils.skins.get(player.getName()) != null){
            player.setSkin(Utils.skins.get(player.getName()));
        }
    }
}
