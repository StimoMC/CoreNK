package net.core.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import eu.pixelstudios.cloudbridge.api.CloudAPI;
import eu.pixelstudios.cloudbridge.api.server.status.ServerStatus;
import net.core.manager.groupManager.Nicks;

public class NickCommand extends Command {

    public NickCommand(String s){
        super(s);
        this.setPermission("core.command.nick");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!Nicks.isNicked(commandSender.getName())) {
            if (!this.testPermission(commandSender)) return false;
            (new Nicks()).nickPlayer((Player) commandSender);
        } else {
            if (CloudAPI.getInstance().getCurrentServer().getServerStatus() == ServerStatus.IN_GAME){
                commandSender.sendMessage("§cYou can't unnick while you are in a running round.");
                return false;
            }
            (new Nicks()).unnickPlayer((Player) commandSender);
        }
        return false;
    }
}
