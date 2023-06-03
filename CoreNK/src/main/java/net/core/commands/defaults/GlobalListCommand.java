package net.core.commands.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import eu.pixelstudios.cloudbridge.api.CloudAPI;
import net.core.Options;

public class GlobalListCommand extends Command {
    public GlobalListCommand(String s) {
        super(s);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        commandSender.sendMessage(Options.prefix + "§7Currently §e" + CloudAPI.getInstance().getPlayers().size() + " §7player(s) are online.");
        return false;
    }
}
