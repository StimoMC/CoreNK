package net.core.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.corePlayer.CorePlayer;
import net.core.manager.reportManager.Report;
import net.core.manager.reportManager.forms.ReportPlayerForm;

public class ReportCommand extends Command {
    public ReportCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof CorePlayer)) return false;
        ReportPlayerForm.sendReportPlayerForm((Player) commandSender);
        return false;
    }
}
