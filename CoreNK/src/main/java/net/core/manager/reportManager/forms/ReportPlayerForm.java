package net.core.manager.reportManager.forms;

import cn.nukkit.Player;
import cn.nukkit.Server;
import eu.pixelstudios.cloudbridge.api.CloudAPI;
import eu.pixelstudios.cloudbridge.api.player.CloudPlayer;
import net.core.Options;
import net.core.manager.reportManager.Report;
import net.core.utils.Utils;
import ru.nukkitx.forms.elements.CustomForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPlayerForm {

    public static void sendReportPlayerForm(Player player){

        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : Utils.reportReasons.entrySet()) {
            list.add(entry.getKey());
        }


        CustomForm customForm = new CustomForm("§l§cReport player")
                .addInput("§ePlayer name")
                .addDropDown("§cReason", list, Utils.reportReasons.size() - 1);
        customForm.send(player, (target, form, data) -> {
            if (data == null) return;

            String playerName = (String) data.get(0);
            String reason = (String) data.get(1);

            System.out.println(playerName);
            System.out.println(reason);

            if (playerName.isEmpty() || playerName.isBlank()){
                player.sendMessage(Options.prefix + "§cPlayer name can't be empty.");
                return;
            }

            if (reason.isEmpty() || reason.isBlank()){
                player.sendMessage(Options.prefix + "§cReason can't be empty.");
                return;
            }

            if (Server.getInstance().getPlayerExact(playerName) == null){
                player.sendMessage(Options.prefix + "§cThis player is not online.");
                return;
            }

            if (Report.getReport(playerName) != null) {
                player.sendMessage(Options.prefix + "§cThis player has already been reported§7.");
                return;
            }

            if (playerName.equalsIgnoreCase(player.getName())){
                player.sendMessage(Options.prefix + "§cYou can't report yourself§7.");
                return;
            }

            Report.createReport(player, reason, playerName);
        });
    }
}
