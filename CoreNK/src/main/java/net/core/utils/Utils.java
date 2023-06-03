package net.core.utils;

import cn.nukkit.Player;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.core.Options;
import net.core.api.StatsAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

public class Utils {

    public static HashMap<String, String> lastMessage = new HashMap<>();
    public static HashMap<String, Integer> chatCooldown = new HashMap<>();
    public static HashMap<String, Integer> cooldown = new HashMap<>();
    public static HashMap<String, Skin> skins = new HashMap<>();
    public static HashMap<String, Integer> reportReasons = new HashMap<>();

    public static boolean isObjectInteger(Object object) {
        return object instanceof Integer;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static int mt_rand(int min, int max) {
        return (int) (Math.random()*(max-min))+min;
    }

    public static void showStats(Player player) {
        FormWindowSimple gui = new FormWindowSimple("§eStats",
                    "§eKills §8-> §f" + StatsAPI.getKills(player.getName(), Options.statsTable) + "§r\n" +
                        "§eDeaths §8-> §f" + StatsAPI.getDeaths(player.getName(), Options.statsTable) + "§r\n" +
                        "§eWins §8-> §f" + StatsAPI.getWins(player.getName(), Options.statsTable) + "§r\n" +
                        "§eLoses §8-> §f" + StatsAPI.getLoses(player.getName(), Options.statsTable) + "§r\n" +
                        "§eBeds §8-> §f" + StatsAPI.getBeds(player.getName(), Options.statsTable) + "§r\n" +
                        "§eRounds §8-> §f" + StatsAPI.getRounds(player.getName(), Options.statsTable) + "§r\n\n" +
                        "§eElo §8-> §f" + StatsAPI.getElo(player.getName(), Options.statsTable) + "§r\n" +
                        "§eElo rank §8-> §f" + StatsAPI.getEloRank(player.getName(), Options.statsTable) + "§r\n" +
                        "§eNext elo rank §8-> §f" + StatsAPI.getNextEloRank(player.getName(), Options.statsTable) + "§r\n"
        );
        gui.addButton(new ElementButton("§4Close"));
        player.showFormWindow(gui);
    }

    public static void showEloRanks(Player player) {
        FormWindowSimple gui = new FormWindowSimple("§eElo ranking",
                "§8Noob§f: §7up to 1050 Elo" + "§r\n" +
                        "§aNoobv2§f: §71050 - 1100 Elo" + "§r\n" +
                        "§6Pro§f: §71100 - 1200 Elo" + "§r\n" +
                        "§cExpert§f: §71200 - 1500 Elo" + "§r\n" +
                        "§eChampion§f: §71500 - 1800 Elo" + "§r\n" +
                        "§4Elite§f: §71800 Elo and above"
        );
        gui.addButton(new ElementButton("§4Close"));
        player.showFormWindow(gui);
    }
}
