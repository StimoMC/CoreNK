package net.core.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.Options;
import net.core.corePlayer.CorePlayer;
import net.core.utils.Utils;

public class StatsCommand extends Command {

    public StatsCommand(String s) {
        super(s);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (Options.statsTable != null){
                Utils.showStats((Player) commandSender);
            }
        }
        return false;
    }
}
