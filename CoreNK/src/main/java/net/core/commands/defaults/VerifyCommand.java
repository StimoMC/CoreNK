package net.core.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import net.core.Options;
import net.core.api.PasswordAPI;
import net.core.manager.verifyManager.Verify;
import org.checkerframework.checker.nullness.Opt;

public class VerifyCommand extends Command {


    public VerifyCommand(String s) {
        super(s);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player)){
            return false;
        }

        if (Verify.getInstance().getVerified(commandSender.getName()) == null){
            if (Verify.getInstance().getVerifyCode(commandSender.getName()) == null){
                String code = PasswordAPI.generatePassword(6);
                Verify.setVerifyCode(commandSender.getName(), code);
                commandSender.sendMessage(Options.prefix + "§aYour verify code§7: §e" + code);
            } else {
                commandSender.sendMessage(Options.prefix + "§cYou have already an verify code. §8(§b" + Verify.getInstance().getVerifyCode(commandSender.getName()) + "§8)");
            }
        } else {
            commandSender.sendMessage(Options.prefix + "§cYou are already verified.");
        }
        return false;
    }
}
