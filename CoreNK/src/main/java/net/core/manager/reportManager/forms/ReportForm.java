package net.core.manager.reportManager.forms;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import eu.pixelstudios.cloudbridge.api.CloudAPI;
import eu.pixelstudios.cloudbridge.api.server.CloudServer;
import eu.pixelstudios.cloudbridge.api.server.status.ServerStatus;
import net.core.Options;
import net.core.manager.reportManager.Report;
import ru.nukkitx.forms.elements.SimpleForm;

public class ReportForm {

    public static void manageReport(Player player, String report){

        String online = (CloudAPI.getInstance().getPlayerByName(report) != null ? "§aOnline" : "§4Offline");

        SimpleForm form;
        form = new SimpleForm("§eReport §7- §e" + report);
        form.setContent("§eReported§7: §b" + report + "§r\n" + "§eReported by§7: §b" + Report.getReportedBy(report) + "§r\n" + "§eReason§7: §b" + Report.getReportReason(report) + "§r\n" + "§eDate§7: §b" + Report.getReportDate(report) + "§r\n" + "§eClaimed by§7: §b" + Report.getClaimedBy(report) + "§r\n");

        form.addButton("§eJump to player\n" + "§8(" + online + "§8)");
        form.addButton("§2Accept report");
        form.addButton("§cDeny report");
        form.addButton("§4Close");

        form.send(player, (targetPlayer, targetForm, data) -> {
            if(data == -1) return;
            String realData = TextFormat.clean(targetForm.getResponse().getClickedButton().getText());
            String[] splits = realData.split("\n");
            String button = splits[0];

            if (button.equals("Close")){
                return;
            }

            if (button.equals("Accept report")){
                Report.acceptReport(player, report, Report.getReportReason(report));
                return;
            }

            if (button.equals("Deny report")){
                Report.denyReport(player, report);
                return;
            }

            if (button.equals("Jump to player")){
                if (CloudAPI.getInstance().getPlayerByName(report) != null){
                    CloudServer server = CloudAPI.getInstance().getPlayerByName(report).getCurrentServer();
                    if (server != null){
                        if (server.getServerStatus() == ServerStatus.FULL && server.getServerStatus() != ServerStatus.IN_GAME){
                            player.sendMessage(Options.prefix + "§cYou can't jump to this server, because this server is full.");
                            return;
                        }

                        if (CloudAPI.getInstance().transferPlayerForSpectate(player, server)){
                            player.sendMessage(Options.prefix + "§aTransferring to §e" + server.getName() + "§7...");
                        } else {
                            player.sendMessage(Options.prefix + "§cYou can't be transferred to §e" + server.getName() + "§7.");
                        }
                    } else {
                        player.sendMessage(Options.prefix + "§cThis server don't exists.");
                    }
                } else {
                    player.sendMessage(Options.prefix + "§cThis player isn't online.");
                }
            }
        });
    }
}
