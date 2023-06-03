package net.core.commands.admin;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.Options;
import net.core.corePlayer.CorePlayer;
import org.checkerframework.checker.nullness.Opt;

public class OpCommand extends Command {

    public OpCommand(String s) {
        super(s);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof CorePlayer)){
            commandSender.sendMessage(Options.prefix + "§cYou must be a player.");
            return false;
        }
        if (!commandSender.hasPermission("core.command.op")){
            commandSender.sendMessage(Options.prefix + "§cNo permissions.");
            return false;
        }
        if (strings.length == 1){
            if (!strings[0].equalsIgnoreCase(commandSender.getName())){
                commandSender.sendMessage(Options.prefix + "§cYou can only give yourself op.");
                return false;
            }
            if (Server.getInstance().isOp(strings[0])){
                commandSender.sendMessage(Options.prefix + "§cYou are already an operator.");
                return false;
            }
            Server.getInstance().addOp(strings[0].toLowerCase());
            commandSender.sendMessage(Options.prefix + "§cYou are now an operator.");
            return false;
        } else {
            commandSender.sendMessage(Options.prefix + "§cUsage: /op <your name>");
        }
        return false;
    }
}
