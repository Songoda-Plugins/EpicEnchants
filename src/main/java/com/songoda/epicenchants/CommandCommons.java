package com.songoda.epicenchants;

import com.songoda.core.utils.NumberUtils;
import org.bukkit.command.CommandSender;

public class CommandCommons {
    public static boolean isInt(String number, CommandSender sender) {
        if (NumberUtils.isInt(number)) {
            return true;
        }

        EpicEnchants.getPlugin(EpicEnchants.class).getLocale().newMessage("Not a number.").sendPrefixedMessage(sender);
        return false;
    }
}
