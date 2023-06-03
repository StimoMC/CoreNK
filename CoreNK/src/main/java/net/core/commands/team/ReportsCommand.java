package net.core.commands.team;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.corePlayer.CorePlayer;
import net.core.manager.reportManager.forms.ReportListForm;

public class ReportsCommand extends Command {
    public ReportsCommand(String name) {
        super(name);
        this.setPermission("core.reports.manage");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof CorePlayer)){
            return false;
        }

        if (!this.testPermission(commandSender)){
            return false;
        }

        ReportListForm.listReportsForm((Player) commandSender);
        return false;
    }
}
