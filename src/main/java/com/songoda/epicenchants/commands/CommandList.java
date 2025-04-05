package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandList extends AbstractCommand {
    private final EpicEnchants plugin;

    public CommandList(EpicEnchants plugin) {
        super(CommandType.PLAYER_ONLY, "list");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("chat")) {
            this.plugin.getLocale().newMessage(this.plugin.getEnchantManager().getValues().stream()
                            .sorted(Comparator.comparing(enchant -> enchant.getGroup().getOrder()))
                            .map(enchant -> enchant.getColoredIdentifier(true)).collect(Collectors.joining("&7, ")))
                    .sendPrefixedMessage(sender);
            return ReturnType.SUCCESS;
        }
        this.plugin.getInfoManager().getMainInfoMenu().open((Player) sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicenchants.list";
    }

    @Override
    public String getSyntax() {
        return "list [chat]";
    }

    @Override
    public String getDescription() {
        return "List all enchants with their description.";
    }
}
