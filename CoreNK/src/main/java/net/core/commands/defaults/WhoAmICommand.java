package net.core.commands.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import eu.pixelstudios.cloudbridge.api.CloudAPI;
import net.core.Options;

public class WhoAmICommand extends Command {
    public WhoAmICommand(String s) {
        super(s);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        commandSender.sendMessage(Options.prefix + "§7You are currently online on §e" + CloudAPI.getInstance().getCurrentServer().getName() + "§7.");
        return false;
    }
}