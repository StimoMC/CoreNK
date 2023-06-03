package net.core.commands.team;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.Options;
import net.core.manager.banManager.Bans;

public class BanIDsCommand extends Command {

    public BanIDsCommand(String s){
        super(s);
        this.setPermission("core.command.banids");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!testPermission(commandSender)) return false;

        StringBuilder message = new StringBuilder(Options.prefix + "§l§cBan Ids§r\n");
        for (Integer integer : Bans.getReasonList()){
            String reason = Bans.getBanReasonById(integer);
            String type = Bans.getBanTypeById(integer);
            Integer time = Bans.getBanTimeById(integer);
            String unit = Bans.getBanUnitById(integer);

            if (time == 0 || unit == null){
                message.append(Options.prefix).append("§e").append(integer).append(" §7> §r").append("§e").append(reason).append(" §7- §c").append(type).append(" §r§7(§ePERMANENT§7)§r").append("\n");
            } else {
                message.append(Options.prefix).append("§e").append(integer).append(" §7> §r").append("§e").append(reason).append(" §7- §c").append(type).append(" §r§7(§e").append(time).append(unit).append("§7)§r").append("\n");
            }
        }
        commandSender.sendMessage(message.toString());
        return false;
    }
}
