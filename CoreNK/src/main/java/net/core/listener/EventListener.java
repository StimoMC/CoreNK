package net.core.listener;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.*;
import cn.nukkit.scheduler.Task;
import eu.pixelstudios.cloudbridge.network.Network;
import eu.pixelstudios.cloudbridge.network.packet.impl.normal.PlayerKickPacket;
import net.core.Options;
import net.core.api.StatsAPI;
import net.core.api.WebhookAPI;
import net.core.corePlayer.CorePlayer;
import net.core.manager.altManager.Alts;
import net.core.manager.banManager.Bans;
import net.core.manager.groupManager.Groups;
import net.core.manager.groupManager.Nicks;
import net.core.manager.groupManager.PermissionManager;
import net.core.manager.reportManager.Report;
import net.core.manager.vpnManager.VPN;
import net.core.utils.Utils;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EventListener implements Listener {

    private static final SplittableRandom random = new SplittableRandom();
    private final HashMap<Player, List<Long>> cps = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreation(PlayerCreationEvent event){
        event.setPlayerClass(CorePlayer.class);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        if (Utils.skins.get(player.getName()) == null){
            Utils.skins.put(player.getName(), player.getSkin());
        }

        CompletableFuture.runAsync(() -> {
            ((CorePlayer) player).register("users", "name", player.getName().toLowerCase());
            Bans.registerBanPlayer(player);

            Alts.registerCID(player);
            Alts.registerUUID(player);
            Alts.registerXUID((CorePlayer) player);
            Alts.registerIP((CorePlayer) player);

            PermissionManager.calculatePlayerPermissions(player, false);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!(player instanceof CorePlayer)) return;

        player.getInventory().setHeldItemIndex(player.getInventory().getHeldItemIndex());
        player.getInventory().setHeldItemSlot(player.getInventory().getHeldItemSlot());

        if (Bans.isBanned(player.getName())){
            if (!Bans.checkExpired(player, false)){
                if (!player.hasPermission("core.ban.ignore")) {
                    Network.getInstance().sendPacket(new PlayerKickPacket(player.getName(), Bans.getBanMessage(player)));
                    return;
                } else {
                    Bans.unbanPlayer(player.getName(), "AUTOMATIC", true, true);
                }
            } else {
                Bans.unbanPlayer(player.getName(), "AUTOMATIC", false, true);
            }
        }

        for (String name : Alts.getPlayerAccounts(player)){
            if (Bans.isBanned(name)){
                if (!Bans.checkExpired(name, false)){
                    if (!player.hasPermission("core.ban.ignore")) {
                        Network.getInstance().sendPacket(new PlayerKickPacket(player.getName(), Bans.getBanMessage(name)));
                        return;
                    } else {
                        Bans.unbanPlayer(name, "AUTOMATIC", true, true);
                    }
                } else {
                    Bans.unbanPlayer(name, "AUTOMATIC", false, true);
                }
            }
        }

        double ipScore = VPN.requestIPScore(((CorePlayer) player).getOriginalAddress());
        if(ipScore > 0.98) {
            VPN.onVPN(player.getName(), ((CorePlayer) player).getOriginalAddress());
            Network.getInstance().sendPacket(new PlayerKickPacket(player.getName(), "§cYou are using VPN!.\nPlease, rejoin without it."));
            return;
        }

        Utils.chatCooldown.put(player.getName(), 0);
        Utils.lastMessage.put(player.getName(), "");

        CompletableFuture.runAsync(() -> {
            if (Groups.playerRankExpired(player)){
                String rankBefore = Groups.getPlayerGroupBefore(player.getName());
                Groups.setRank(player.getName(), "AUTOMATIC", rankBefore, "PERMANENT", "d");
                player.sendMessage(Options.stimoPrefix + "§cYour rank has expired.");

                PermissionManager.calculatePlayerPermissions(player, true);
                (new Nicks()).unnickPlayer(player);
            }

            if (!Nicks.isNicked(player.getName())){
                player.setNameTag(Groups.getNameTag(Objects.requireNonNull(Groups.getPlayerGroup(player.getName()))).replace("{name}", player.getName()));
                player.setDisplayName(Groups.getNameTag(Objects.requireNonNull(Groups.getPlayerGroup(player.getName()))).replace("{name}", player.getName()));
            } else {
                player.setNameTag(Groups.getNameTag(Objects.requireNonNull(Groups.getNickedGroup())).replace("{name}", ((CorePlayer) player).getNickName()));
                player.setDisplayName(Groups.getNameTag(Objects.requireNonNull(Groups.getNickedGroup())).replace("{name}", ((CorePlayer) player).getNickName()));
            }
        });

        if (Options.showEloRank){
            player.setScoreTag(StatsAPI.getEloRank(player.getName(), Options.statsTable));
        }

        if (!((CorePlayer) player).getJoinedAddress().equalsIgnoreCase("StimoMC.net:19132")){
            player.sendTitle("§cPlease join over", "§eStimoMC.net");
        }

        ((CorePlayer) player).setJoinSequenceFinished(true);
        event.setJoinMessage("");

        if (player.hasPermission("core.reports.manage")){
            if (Report.getOpenReportsCount() > 0){
                player.sendMessage(Options.prefix + "§aCurrently are §e" + Report.getOpenReportsCount() + " §areports opened§7.");
            } else {
                player.sendMessage(Options.prefix + "§cCurrently are no reports open§7.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        if (Bans.isMuted(player.getName())) {
            if (!Bans.checkExpired(player, true)) {
                if (!player.hasPermission("core.ban.ignore")) {
                    player.sendMessage(Bans.getMuteMessage(player));
                    event.setCancelled(true);
                    return;
                } else {
                    Bans.unmutePlayer(player.getName(), "AUTOMATIC", true, true);
                }
            } else {
                Bans.unmutePlayer(player.getName(), "AUTOMATIC", false, true);
            }
        }

        for (String name : Alts.getPlayerAccounts(player)) {
            if (Bans.isMuted(name)) {
                if (!Bans.checkExpired(name, false)) {
                    if (!player.hasPermission("core.ban.ignore")) {
                        player.sendMessage(Bans.getMuteMessage(player));
                        event.setCancelled(true);
                        return;
                    } else {
                        Bans.unmutePlayer(name, "AUTOMATIC", true, true);
                    }
                } else {
                    Bans.unmutePlayer(name, "AUTOMATIC", false, true);
                }
            }
        }

        if (Utils.chatCooldown.get(player.getName()) > 0) {
            player.sendMessage(Options.prefix + "§cYou can chat again in §e" + Utils.chatCooldown.get(player.getName()) + " §cseconds.");
            event.setCancelled(true);
            return;
        }

        Utils.chatCooldown.put(player.getName(), 3);
        Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                if (Utils.chatCooldown.get(player.getName()) == 3) {
                    Utils.chatCooldown.put(player.getName(), 2);
                } else if (Utils.chatCooldown.get(player.getName()) == 2) {
                    Utils.chatCooldown.put(player.getName(), 1);
                } else if (Utils.chatCooldown.get(player.getName()) == 1) {
                    Utils.chatCooldown.put(player.getName(), 0);
                    this.getHandler().cancel();
                }
            }
        }, 20, true);

        if (Utils.lastMessage.get(player.getName()).equals(event.getMessage())) {
            player.sendMessage(Options.stimoPrefix + "§cPlease don't repeat yourself.");
            event.setCancelled(true);
            return;
        } else {
            Utils.lastMessage.put(player.getName(), event.getMessage());
        }

        onChatLog(player.getName(), event.getMessage());

        if (!(player instanceof CorePlayer)) return;
        if (!Nicks.isNicked(player.getName())) {
            event.setFormat(Groups.getChatFormat(Objects.requireNonNull(Groups.getPlayerGroup(player.getName()))).replace("{name}", player.getName()).replace("{msg}", event.getMessage()));
        } else {
            event.setFormat(Groups.getChatFormat(Objects.requireNonNull(Groups.getNickedGroup())).replace("{name}", ((CorePlayer) player).getNickName()).replace("{msg}", event.getMessage()));
        }
    }

    @EventHandler
    public void onReceiveAchieve(PlayerAchievementAwardedEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onPacket(DataPacketReceiveEvent event) {
        if (event.getPacket() instanceof StartGamePacket){
            ((StartGamePacket) event.getPacket()).emoteChatMuted = true;
            event.getPlayer().getNetworkSession().sendPacket(event.getPacket());
        }

        if ((event.getPacket() instanceof LevelSoundEventPacket)) {

            LevelSoundEventPacket packet = (LevelSoundEventPacket) event.getPacket();

            if (packet.sound != LevelSoundEventPacket.SOUND_ATTACK && packet.sound != LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE &&
                    packet.sound != LevelSoundEventPacket.SOUND_ATTACK_STRONG) return;

            List<Long> cpsList = cps.get(event.getPlayer());

            if (cpsList == null) {
                cpsList = new ArrayList<>();
            }

            cpsList.add(System.currentTimeMillis());
            cps.remove(event.getPlayer());
            cps.put(event.getPlayer(), cpsList);
            event.getPlayer().sendActionBar("§eCPS§f: " + String.valueOf(gCPS(event.getPlayer())));
        }
    }

    public int gCPS(Player player) {
        List<Long> list = cps.get(player);
        if (list == null) return 0;
        list.removeIf(l -> l < System.currentTimeMillis() - 1000);
        return list.size();
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        String[] message = event.getMessage().split(" ");
        if (event.getMessage().startsWith("/")){
            String command = message[0].replace("/", "");
            if (Server.getInstance().getCommandMap().getCommand(command) == null){
                event.setCancelled(true);
                player.sendMessage(Options.stimoPrefix + "§cThe command §4/" + command + " §cwas not found.");
            }
        } else if (event.getMessage().startsWith(".") && event.getMessage().startsWith("/", 2)) {
            String command = message[0].replace(".", "").replace("/", "");
            if (Server.getInstance().getCommandMap().getCommand(command) == null){
                event.setCancelled(true);
                player.sendMessage(Options.stimoPrefix + "§cThe command §4/" + command + " §cwas not found.");
            }
        }
    }

    public static void onChatLog(String name, String message) {
        String url = WebhookAPI.getWebhookLink("chatlog_webhook");

        try (WebhookClient client = WebhookClient.withUrl(url)) {
            TemporalAccessor accessor = Instant.ofEpochMilli(System.currentTimeMillis());
            WebhookEmbed embed = new WebhookEmbedBuilder()
                    .setTitle(new WebhookEmbed.EmbedTitle(name + "'s chatlog", ""))
                    .setDescription("")
                    .addField(new WebhookEmbed.EmbedField(false, "Message: ", message))
                    .setTimestamp(accessor)
                    .build();
            client.send(embed).thenAccept(readonlyMessage -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if (player instanceof CorePlayer){
            ((CorePlayer) player).resetHitbox();
        }
    }
}