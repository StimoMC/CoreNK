package net.core.commands.admin;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.core.Options;
import net.core.corePlayer.CorePlayer;
import net.core.database.Database;
import net.core.manager.coinManager.Coins;
import net.core.manager.groupManager.Groups;
import net.core.utils.Utils;

import java.util.Objects;

public class RankCommand extends Command {
    public RankCommand(String s) {
        super(s);

        this.getCommandParameters().put("default",
                new CommandParameter[]{
                        new CommandParameter("help", CommandParamType.TEXT, false),
                        new CommandParameter("setrank <player> <time in days|PERMANENT>", CommandParamType.TEXT, true)
                });

        this.setPermission("core.command.rank.admin");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (strings.length == 0) {
                String message = Options.prefix + "\n";
                message += Options.prefix + "§fCurrent rank: §r" + Groups.getColoredPlayerGroup(Groups.getPlayerGroup(commandSender.getName()), commandSender.getName()) + "§r\n";
                message += Options.prefix + "§fRank expires in: §r" + Groups.getEnd(Groups.getRankDuration(commandSender.getName())) + "§r\n";
                message += Options.prefix + "\n";
                commandSender.sendMessage(message);
                return false;
            }
        }

        if (this.testPermission(commandSender)) {

            if (strings.length == 1){
                commandSender.sendMessage(Options.prefix + "§cUse /rank help");
                return false;
            }

            //Setrank: /rank setrank(1) <player>(2) <rank>(3) <time in days>(4)
            if (strings.length >= 1) {
                if (strings[0].equals("setrank")) {
                    if (strings.length < 4) {
                        commandSender.sendMessage(Options.prefix + "§cPlease use §f/rank setrank <player> <rank> <time in days|PERMANENT>");
                        return false;
                    }

                    String playerName = strings[1];
                    if (!Database.exists("users", "name", playerName.toLowerCase())) {
                        commandSender.sendMessage(Options.prefix + "§cThis player don't exists.");
                        return false;
                    }
                    String rankName = strings[2];
                    if (!Groups.existsRank(rankName)) {
                        commandSender.sendMessage(Options.prefix + "§cThis rank don't exists.");
                        return false;
                    }
                    String time = strings[3];
                    if (!Utils.isNumeric(time) && !time.equals("PERMANENT")) {
                        commandSender.sendMessage(Options.prefix + "§cPlease enter a number or 'PERMANENT' as the time.");
                        return false;
                    }
                    if (!Objects.equals(time, "PERMANENT")) {
                        if (Integer.parseInt(time) > 365) {
                            commandSender.sendMessage(Options.prefix + "§cPlease select a period between 1 and 365 days.");
                            return false;
                        }
                        Groups.setRank(playerName, commandSender.getName(), rankName, time, "d");
                    } else {
                        Groups.setRank(playerName, commandSender.getName(), rankName, "PERMANENT", "d");
                    }
                    commandSender.sendMessage(Options.prefix + "§aYou have the player §e" + playerName + " §agiven the rank " + Groups.getGroupColor(rankName) + rankName + "§r§7!");
                }
            }
        }
        return false;
    }
}