package net.core.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.Options;
import net.core.corePlayer.CorePlayer;
import net.core.database.Database;
import net.core.manager.coinManager.Coins;
import net.core.utils.Utils;

public class CoinsCommand extends Command {

    public CoinsCommand(String s) {
        super(s);
        this.setPermission("core.coins.command.admin");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length == 0){
            if (commandSender instanceof Player){
                commandSender.sendMessage(Options.prefix + "§7You have currently §e" + Coins.getPlayerCoins(commandSender.getName()) + "§7 coins.");
            }
            return false;
        } else {
            if (!this.testPermission(commandSender)) return false;

            if (strings.length == 1){
                commandSender.sendMessage(Options.prefix + "§cUse /coins help");
                return false;
            }

            if (strings[0].equalsIgnoreCase("set")) {
                if (strings.length < 3) {
                    commandSender.sendMessage(Options.prefix + "§cPlease use §f/coins set <player> <coins>");
                    return false;
                }
                String playerName = strings[1];
                if (!Database.exists("users", "name", playerName.toLowerCase())) {
                    commandSender.sendMessage(Options.prefix + "§cThis player don't exists.");
                    return false;
                }

                if (Utils.isNumeric(strings[2])) {
                    Integer coins = Integer.parseInt(strings[2]);
                    Coins.setCoins(playerName, coins);
                    commandSender.sendMessage(Options.prefix + "§aThe player §e" + playerName + " §ahas now §e" + coins + "coins§a.");
                }
            } else if (strings[0].equalsIgnoreCase("add")){
                if (strings.length < 3) {
                    commandSender.sendMessage(Options.prefix + "§cPlease use §f/coins add <player> <coins>");
                    return false;
                }
                String playerName = strings[1];
                if (!Database.exists("users", "name", playerName.toLowerCase())) {
                    commandSender.sendMessage(Options.prefix + "§cThis player don't exists.");
                    return false;
                }

                if (Utils.isNumeric(strings[2])) {
                    Integer coins = Integer.parseInt(strings[2]);
                    Coins.addCoins(playerName, coins);
                    commandSender.sendMessage(Options.prefix + "§aYou have added the player §e" + playerName + " §e" + coins + "coins§a.");
                }
            } else if (strings[0].equalsIgnoreCase("remove")){
                if (strings.length < 3) {
                    commandSender.sendMessage(Options.prefix + "§cPlease use §f/coins remove <player> <coins>");
                    return false;
                }
                String playerName = strings[1];
                if (!Database.exists("users", "name", playerName.toLowerCase())) {
                    commandSender.sendMessage(Options.prefix + "§cThis player don't exists.");
                    return false;
                }

                if (Utils.isNumeric(strings[2])) {
                    Integer coins = Integer.parseInt(strings[2]);
                    Coins.removeCoins(playerName, coins);
                    commandSender.sendMessage(Options.prefix + "§aYou have removed the player §e" + playerName + " §§e" + coins + "coins§a.");
                }
            }
        }
        return false;
    }
}
