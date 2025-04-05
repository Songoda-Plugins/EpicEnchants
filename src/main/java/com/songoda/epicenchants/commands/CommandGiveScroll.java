package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.CommandCommons;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandGiveScroll extends AbstractCommand {
    private final EpicEnchants plugin;

    public CommandGiveScroll(EpicEnchants plugin) {
        super(CommandType.CONSOLE_OK, "givescroll");
        this.plugin = plugin;
    }

    //givescroll <giveType> <player> [amount] [success-rate]
    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 2 || args.length > 5) {
            return ReturnType.SYNTAX_ERROR;
        }

        String giveType = args[0];
        OfflinePlayer target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            this.plugin.getLocale().newMessage("&cThis player does not exist...").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        int amount = 1;
        int successRate = -1;

        if (args.length > 2) {
            if (!CommandCommons.isInt(args[2], sender)) {
                return ReturnType.FAILURE;
            }
            amount = Integer.parseInt(args[2]);
        }

        if (args.length > 3) {
            if (!CommandCommons.isInt(args[3], sender)) {
                return ReturnType.FAILURE;
            }
            successRate = Integer.parseInt(args[3]);
        }

        String messageKey;
        switch (giveType.toLowerCase()) {
            case "whitescroll":
                target.getPlayer().getInventory().addItem(this.plugin.getSpecialItems().getWhiteScroll(amount));
                messageKey = "whitescroll";
                break;
            case "blackscroll":
                messageKey = "blackscroll";
                target.getPlayer().getInventory().addItem(this.plugin.getSpecialItems().getBlackScroll(amount, successRate));
                break;
            default:
                this.plugin.getLocale().getMessage("command.giveunknown")
                        .processPlaceholder("unknown", giveType)
                        .sendPrefixedMessage(sender);
                return ReturnType.FAILURE;
        }

        this.plugin.getLocale().getMessage("command." + messageKey + ".received")
                .sendPrefixedMessage(target.getPlayer());
        this.plugin.getLocale().getMessage("command." + messageKey + ".gave")
                .processPlaceholder("player", target.getName())
                .sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return Arrays.asList("whitescroll", "blackscroll");
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 3 || args.length == 4) {
            List<String> rates = new ArrayList<>();
            for (int i = 1; i <= (args.length == 3 ? 10 : 100); i++) {
                rates.add(String.valueOf(i));
            }
            return rates;
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicenchants.givescroll";
    }

    @Override
    public String getSyntax() {
        return "givescroll <whitescroll/blackscroll> <player> [amount] [success-rate]";
    }

    @Override
    public String getDescription() {
        return "Give enchant scrolls to players.";
    }
}
