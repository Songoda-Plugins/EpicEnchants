package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.objects.Enchant;
import com.songoda.epicenchants.CommandCommons;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandGiveBook extends AbstractCommand {
    private final EpicEnchants plugin;

    public CommandGiveBook(EpicEnchants plugin) {
        super(CommandType.CONSOLE_OK, "givebook");
        this.plugin = plugin;
    }

    //givebook <player> <enchant> [level] [success-rate] [destroy-rate]
    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 2 || args.length > 5) {
            return ReturnType.SYNTAX_ERROR;
        }

        OfflinePlayer target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            this.plugin.getLocale().newMessage("&cThis player does not exist...").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Optional<Enchant> optionalEnchant = this.plugin.getEnchantManager().getValue(args[1].replaceAll("_", " "));

        if (!optionalEnchant.isPresent()) {
            this.plugin.getLocale().newMessage("&cNo enchants exist with that name...").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Enchant enchant = optionalEnchant.get();
        int level = -1;
        int successRate = -1;
        int destroyRate = -1;

        if (args.length > 2) {
            if (!CommandCommons.isInt(args[2], sender)) {
                return ReturnType.FAILURE;
            }
            level = Integer.parseInt(args[2]);
        }

        if (args.length > 3) {
            if (!CommandCommons.isInt(args[3], sender)) {
                return ReturnType.FAILURE;
            }
            successRate = Integer.parseInt(args[3]);
        }

        if (args.length > 4) {
            if (!CommandCommons.isInt(args[4], sender)) {
                return ReturnType.FAILURE;
            }
            destroyRate = Integer.parseInt(args[4]);
        }

        if (level != -0 && (level > enchant.getMaxLevel() || level < 0)) {
            this.plugin.getLocale().getMessage("command.book." + (level > enchant.getMaxLevel() ? "maxlevel" : "minlevel"))
                    .processPlaceholder("enchant", enchant.getIdentifier())
                    .processPlaceholder("max_level", enchant.getMaxLevel())
                    .sendPrefixedMessage(sender);
            return ReturnType.SUCCESS;
        }

        target.getPlayer().getInventory().addItem(enchant.getBook().get(enchant, level, successRate, destroyRate));
        this.plugin.getLocale().getMessage("command.book.received")
                .processPlaceholder("enchant", enchant.getIdentifier())
                .sendPrefixedMessage(target.getPlayer());
        this.plugin.getLocale().getMessage("command.book.gave")
                .processPlaceholder("player", target.getPlayer().getName())
                .processPlaceholder("enchant", enchant.getIdentifier())
                .sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return this.plugin.getEnchantManager().getValues()
                    .stream().map(Enchant::getIdentifier).collect(Collectors.toList());
        } else if (args.length == 3) {
            Enchant enchant = this.plugin
                    .getEnchantManager()
                    .getValues()
                    .stream()
                    .findFirst()
                    .orElse(null);
            List<String> levels = new ArrayList<>();
            if (enchant != null) {
                for (int i = 1; i <= enchant.getMaxLevel(); i++) {
                    levels.add(String.valueOf(i));
                }
            }
            return levels;
        } else if (args.length == 4 || args.length == 5) {
            List<String> rates = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                rates.add(String.valueOf(i));
            }
            return rates;
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicenchants.givebook";
    }

    @Override
    public String getSyntax() {
        return "givebook <player> <enchant> [level] [success-rate] [destroy-rate]";
    }

    @Override
    public String getDescription() {
        return "Give enchant books to players.";
    }
}
