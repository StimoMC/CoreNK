package net.core;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import net.core.api.PasswordAPI;
import net.core.api.StatsAPI;
import net.core.api.WebhookAPI;
import net.core.commands.admin.OpCommand;
import net.core.commands.admin.RankCommand;
import net.core.commands.defaults.*;
import net.core.commands.team.*;
import net.core.database.Database;
import net.core.emotes.Emotes;
import net.core.listener.EventListener;
import net.core.api.API;
import net.core.manager.altManager.Alts;
import net.core.manager.banManager.Bans;
import net.core.manager.groupManager.Groups;
import net.core.manager.reportManager.Report;
import net.core.manager.verifyManager.Verify;
import net.core.tasks.ChangeWeatherTask;
import net.core.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class Core extends PluginBase {

    protected static ArrayList<API> apis = new ArrayList<>();
    protected static Core instance = null;
    protected static Verify verify = null;

    @Override
    public void onLoad(){
        instance = this;
        this.initDatabase();
    }

    @Override
    public void onEnable() {
        new File("/home/debian/mcpe/.data/core/").mkdirs();
        this.registerAPIs();
        initEmotes();

        Groups.load();
        Bans.create();
        Alts.create();
        Report.create();
        verify = new Verify();

        unregisterCommands();

        registerCommands();
        registerListener();
        initReportReasons();

        Server.getInstance().getScheduler().scheduleRepeatingTask(new ChangeWeatherTask(), 20, true);
    }

    public static void initReportReasons(){
        Utils.reportReasons.put("Cheating", 1);
        Utils.reportReasons.put("Teaming", 2);
        Utils.reportReasons.put("Insults", 3);
        Utils.reportReasons.put("Provocation", 4);
        Utils.reportReasons.put("Sexual content", 5);
        Utils.reportReasons.put("Bugusing", 8);
        Utils.reportReasons.put("Invalid Skin", 9);
        Utils.reportReasons.put("Invalid name", 10);
    }

    public static void initEmotes(){
        Emotes.addEmote("Abduction?", "18891e6c-bb3d-47f6-bc15-265605d86525");
        Emotes.addEmote("Acting Like a Dragon", "c2a47805-c792-4882-a56d-17c80b6c57a8");
    }

    public void registerAPIs(){
        apis.add(new StatsAPI());
        apis.add(new WebhookAPI());
        apis.add(new PasswordAPI());

        for (API api : apis){
            getServer().getLogger().info(Options.prefix + "§aLoaded API §e" + api.getName() + "§7:§e" + api.getVersion() + " §aby §e" + api.getAuthor() + "§7.");
        }
    }

    public void initDatabase(){
        if (!Database.isConnected()) {
            Database.connect();
        }
        Database.createDefaultTables();
    }

    public static Core getInstance() {
        return instance;
    }

    public void unregisterCommands(){
        getServer().getCommandMap().getCommands().remove("me");
        getServer().getCommandMap().getCommands().remove("say");
        getServer().getCommandMap().getCommands().remove("help");
        getServer().getCommandMap().getCommands().remove("version");
        getServer().getCommandMap().getCommands().remove("plugins");
        getServer().getCommandMap().getCommands().remove("ban");
        getServer().getCommandMap().getCommands().remove("ban-ip");
        getServer().getCommandMap().getCommands().remove("kick");
        getServer().getCommandMap().getCommands().remove("pardon");
        getServer().getCommandMap().getCommands().remove("pardon-ip");
        getServer().getCommandMap().getCommands().remove("op");
        getServer().getCommandMap().getCommands().remove("reload");
        getServer().getCommandMap().getCommands().remove("kill");
        getServer().getCommandMap().getCommands().remove("xp");
        getServer().getCommandMap().getCommands().remove("list");
        getServer().getCommandMap().getCommands().remove("gc");
        getServer().getCommandMap().getCommands().remove("debugpaste");
        getServer().getCommandMap().getCommands().remove("status");
        getServer().getCommandMap().getCommands().remove("time");
        getServer().getCommandMap().getCommands().remove("title");
        getServer().getCommandMap().getCommands().remove("banlist");
        getServer().getCommandMap().getCommands().remove("save-on");
        getServer().getCommandMap().getCommands().remove("save-off");
        getServer().getCommandMap().getCommands().remove("gamerule");
        getServer().getCommandMap().getCommands().remove("tell");
        getServer().getCommandMap().getCommands().remove("seed");
        getServer().getCommandMap().getCommands().remove("defaultgamemode");
        getServer().getCommandMap().getCommands().remove("spawnpoint");
        getServer().getCommandMap().getCommands().remove("effect");
        getServer().getCommandMap().getCommands().remove("enchant");
    }

    public void registerCommands(){
        getServer().getCommandMap().register("regplayers", new RegPlayersCommand("regplayers"));
        getServer().getCommandMap().register("glist", new GlobalListCommand("glist"));
        getServer().getCommandMap().register("whoami", new WhoAmICommand("whoami"));
        getServer().getCommandMap().register("coins", new CoinsCommand("coins"));
        getServer().getCommandMap().register("rank", new RankCommand("rank"));
        getServer().getCommandMap().register("stats", new StatsCommand("stats"));
        getServer().getCommandMap().register("elo", new EloCommand("elo"));
        getServer().getCommandMap().register("op", new OpCommand("op"));
        getServer().getCommandMap().register("banids", new BanIDsCommand("banids"));
        getServer().getCommandMap().register("ban", new BanCommand("ban"));
        getServer().getCommandMap().register("unban", new UnBanCommand("unban"));
        getServer().getCommandMap().register("unmute", new UnMuteCommand("unmute"));
        getServer().getCommandMap().register("verify", new VerifyCommand("verify"));
        getServer().getCommandMap().register("nick", new NickCommand("nick"));
        getServer().getCommandMap().register("report", new ReportCommand("report"));
        getServer().getCommandMap().register("reports", new ReportsCommand("reports"));
    }

    public void registerListener(){
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        if (Database.isConnected()) Database.close();
    }
}
