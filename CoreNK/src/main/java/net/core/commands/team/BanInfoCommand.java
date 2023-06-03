package net.core.commands.team;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.manager.banManager.Bans;

public class BanInfoCommand extends Command {
    public BanInfoCommand(String s) {
        super(s);
        this.setPermission("core.command.baninfo");
        this.setDescription("Use: /baninfo <name>");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!testPermission(commandSender)) return false;
        if (strings.length < 1){
            commandSender.sendMessage(this.getDescription());
            return false;
        }

        String playerName = strings[0];
        return false;
    }
}
