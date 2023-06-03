package net.core.commands.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import eu.pixelstudios.cloudbridge.api.CloudAPI;
import net.core.Options;

import java.util.Locale;

public class RegPlayersCommand extends Command {
    public RegPlayersCommand(String s) {
        super(s);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        commandSender.sendMessage(Options.prefix + "§7Currently §e" + CloudAPI.getInstance().getRegisteredPlayers() + " §7player(s) are registered.");
        return false;
    }
}
