package net.core.manager.reportManager;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import cn.nukkit.Player;
import eu.pixelstudios.cloudbridge.network.Network;
import eu.pixelstudios.cloudbridge.network.packet.impl.normal.PlayerKickPacket;
import net.core.Core;
import net.core.Options;
import net.core.api.WebhookAPI;
import net.core.database.Database;
import net.core.manager.banManager.Bans;
import net.core.manager.groupManager.Groups;
import net.core.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Report {

    public static void create(){
        Database.createTables("CREATE TABLE IF NOT EXISTS reports(" +
                "reported VARCHAR(255)," +
                "reportedBy VARCHAR(255)," +
                "reason VARCHAR(255)," +
                "date VARCHAR(255)," +
                "claimedBy VARCHAR(255)," +
                "PRIMARY KEY (reported));");
    }

    public static int getOpenReportsCount() {
        try {
            final ResultSet rs = Database.getResult("SELECT COUNT(*) AS count FROM reports;");
            assert rs != null;
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
        }
        return 0;
    }

    public static ArrayList<Object[]> getOpenReports() {
        ArrayList<Object[]> openReports = new ArrayList<>();
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM reports;");
            assert rs != null;
            while (rs.next()) {
                Object[] report = new Object[5];
                report[0] = rs.getString("reported");
                report[1] = rs.getString("reportedBy");
                report[2] = rs.getString("reason");
                report[3] = rs.getString("date");
                report[4] = rs.getString("claimedBy");
                openReports.add(report);
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
        }
        return openReports;
    }

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

    public static String getReport(String reported){
        try {
            final ResultSet rs = Database.getResult("SELECT * FROM " + "reports" + " WHERE " + "reported" + " = '" + reported + "';");
            assert rs != null;
            if (rs.next()) {
                return rs.getString("reported");
            }
        } catch (SQLException exception) {
            Core.getInstance().getLogger().info(exception.getMessage());
            return null;
        }
        return null;
    }

    public static String getReported(final String name) {
        return String.valueOf(Report.get("reported", "reports", "reported", name));
    }

    public static String getReportedBy(final String name) {
        return String.valueOf(Report.get("reportedBy", "reports", "reported", name));
    }

    public static String getReportReason(final String name) {
        return String.valueOf(Report.get("reason", "reports", "reported", name));
    }

    public static String getReportDate(final String name) {
        return String.valueOf(Report.get("date", "reports", "reported", name));
    }

    public static String getClaimedBy(final String name) {
        return String.valueOf(Report.get("claimedBy", "reports", "reported", name));
    }

    public static void acceptReport(Player acceptedBy, String reported, String reason) {
        int id = getPunishIdByReportReason(reason);

        acceptedBy.sendMessage(Options.prefix + "§aYou have the report against §e" + reported + " §aaccepted and the player was punished§7.");
        deleteReport(reported);
        sendReportManageHook(reported, acceptedBy.getName(), true);

        if (Bans.existsBanId(id)) {
            if (Objects.equals(Bans.getBanTypeById(id), "ban")) {
                if (Bans.isBanned(reported)) {
                    acceptedBy.sendMessage(Options.prefix + "§cThis player is already banned.");
                    return;
                }
            } else if (Objects.equals(Bans.getBanTypeById(id), "mute")) {
                if (Bans.isMuted(reported)) {
                    acceptedBy.sendMessage(Options.prefix + "§cThis player is already muted.");
                    return;
                }
            }
            Bans.banPlayer(reported, acceptedBy.getName(), id);
        }
    }

    public static void denyReport(Player deniedBy, String reported){
        deniedBy.sendMessage(Options.prefix + "§cYou have the report against §e" + reported + " §cdenied and the player was not punished§7.");
        deleteReport(reported);
        sendReportManageHook(reported, deniedBy.getName(), false);
    }

    public static void deleteReport(String report){
        if (getReport(report) != null) {
            Database.update("DELETE FROM reports WHERE reported = '" + report + "';");
        }
    }

    public static void sendReportManageHook(String report, String by, boolean bool){
        String url = WebhookAPI.getWebhookLink("security_webhook");

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed;
            if (bool) {
                embed = new WebhookEmbedBuilder()
                        .setTitle(new WebhookEmbed.EmbedTitle("Report update", ""))
                        .setDescription("The report against " + report + " was accepted.")
                        .addField(new WebhookEmbed.EmbedField(false, "Accepted by:", by))
                        .setTimestamp(accessor)
                        .build();
            } else {
                embed = new WebhookEmbedBuilder()
                        .setTitle(new WebhookEmbed.EmbedTitle("Report update", ""))
                        .setDescription("The report against " + report + " was denied.")
                        .addField(new WebhookEmbed.EmbedField(false, "Denied by:", by))
                        .setTimestamp(accessor)
                        .build();
            }
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createReportMessageHook(String report, String by, String reason){
        String url = WebhookAPI.getWebhookLink("security_webhook");

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed;
            embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle("Report created", ""))
                    .setDescription("An new report was created.")
                    .addField(new WebhookEmbed.EmbedField(false, "Reported player:", report))
                    .addField(new WebhookEmbed.EmbedField(false, "Created by:", by))
                    .addField(new WebhookEmbed.EmbedField(false, "Reason:", reason))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void claimReport(Player claimedBy, String report){
        if (getClaimedBy(report).equals("null") || getClaimedBy(report) == null) {
            Database.update("UPDATE reports SET `claimedBy` = '" + claimedBy.getName() + "' WHERE `reported` = '" + report + "'");
            claimedBy.sendMessage(Options.prefix + "§aYou have claimed the report against §e" + report + "§7.");
        }
    }

    public static Integer getPunishIdByReportReason(String reason){
        return Utils.reportReasons.get(reason);
    }

    public static void createReport(Player createdBy, String reason, String reported){

        createReportMessageHook(reported, createdBy.getName(), reason);

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new java.util.Date());
        Database.update("INSERT INTO " + "reports" + " (reported, reportedBy, reason, date, claimedBy) VALUES ('" + reported + "', '" + createdBy.getName() + "', '" + reason + "', '" + timeStamp + "', 'null');");
        createdBy.sendMessage(Options.prefix + "§aYour report was created successfully§7.");
    }
}