package net.core.manager.reportManager.forms;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import net.core.manager.reportManager.Report;
import ru.nukkitx.forms.elements.SimpleForm;

import java.util.ArrayList;
import java.util.List;

public class ReportListForm {

    public static void listReportsForm(Player player){

        List<String> reports = new ArrayList<>();
        for (Object[] report : Report.getOpenReports()) {
            String reported = (String) report[0];
            String claimedBy = (String) report[4];

            System.out.println(reported);
            System.out.println(claimedBy);
            if (reported != null){
                if (claimedBy == null || Report.getClaimedBy(reported).equals("null") || Report.getClaimedBy(reported).equalsIgnoreCase(player.getName())){
                    reports.add(reported);
                }
            }
        }

        SimpleForm form;
        form = new SimpleForm("§aChoose a report");
        form.setContent("Currently are §e" + Report.getOpenReportsCount() + " §rreports open.");
        if (reports.size() == 0){
            form.addButton("§cNo reports found");
        } else {
            for (String report : reports){
                if (report != null){
                    form.addButton("§e" + Report.getReported(report) + "\n§r§a" + Report.getReportReason(report) + " §8| §c" + Report.getReportDate(report) + "§r");
                }
            }
            form.addButton("§4Close");
        }

        form.send(player, (targetPlayer, targetForm, data) -> {
            if (data == -1) return;
            String realData = TextFormat.clean(targetForm.getResponse().getClickedButton().getText());
            String[] splits = realData.split("\n");
            String reportName = splits[0];

            if (reportName.equals("No reports found") || reportName.equals("Close")) {
                return;
            }

            Report.claimReport(player, reportName);
            ReportForm.manageReport(player, reportName);
        });
    }
}
