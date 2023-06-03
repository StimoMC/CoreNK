package net.core.commands.team;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.Options;
import net.core.database.Database;
import net.core.manager.banManager.Bans;

public class UnBanCommand extends Command {

    public UnBanCommand(String s) {
        super(s);
        this.setPermission("core.command.unban");
        this.setDescription("Use: /unban <name>");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!this.testPermission(commandSender)) return false;
        if (strings.length >= 1) {
            String playerName = strings[0];
            if (!Database.exists("bans", "name", playerName)){
                commandSender.sendMessage(Options.prefix + "§cThis user don't exists.");
                return false;
            } else {
                if (!Bans.isBanned(playerName)){
                    commandSender.sendMessage(Options.prefix + "§cThis user isn't banned.");
                    return false;
                } else {
                    commandSender.sendMessage(Options.prefix + "§cYou have unbanned the player §e" + playerName + "§7.");
                    Bans.unbanPlayer(playerName, commandSender.getName(), false, false);
                }
            }
        } else {
            commandSender.sendMessage(this.getDescription());
        }
        return false;
    }
}
