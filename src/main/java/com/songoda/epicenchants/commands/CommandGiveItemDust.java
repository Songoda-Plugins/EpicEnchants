package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.CommandCommons;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandGiveItemDust extends AbstractCommand {
    private final EpicEnchants plugin;

    public CommandGiveItemDust(EpicEnchants plugin) {
        super(CommandType.CONSOLE_OK, "giveitemdust");
        this.plugin = plugin;
    }

    //giveitemdust <player> <group> [type] [percentage]
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

        List<Group> groups = this.plugin.getGroupManager().getValues().stream()
                .filter(group -> group.getIdentifier().equalsIgnoreCase(args[1])).collect(Collectors.toList());

        if (groups.isEmpty()) {
            this.plugin.getLocale().newMessage("&cThe group you entered was no found...").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        Group group = groups.get(0);

        String dustType = null;
        int percentage = -1;

        if (args.length > 2) {
            dustType = args[2];
        }

        if (args.length > 3) {
            if (!CommandCommons.isInt(args[3], sender)) {
                return ReturnType.FAILURE;
            }
            percentage = Integer.parseInt(args[3]);
        }

        target.getPlayer().getInventory().addItem(this.plugin.getSpecialItems().getDust(group, dustType, percentage, true));
        this.plugin.getLocale().getMessage("command.dust.received")
                .processPlaceholder("group", group.getIdentifier())
                .sendPrefixedMessage(target.getPlayer());
        this.plugin.getLocale().getMessage("command.dust.gave")
                .processPlaceholder("player", target.getPlayer().getName())
                .processPlaceholder("group", group.getIdentifier())
                .sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return this.plugin.getGroupManager()
                    .getValues()
                    .stream()
                    .map(Group::getIdentifier)
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            List<String> dusts = new ArrayList<>();

            FileConfiguration dustConfig = this.plugin.getFileManager().getConfiguration("items/dusts");
            dusts.addAll(dustConfig.getConfigurationSection("dusts").getKeys(false));
            return dusts;
        } else if (args.length == 4) {
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
        return "epicenchants.giveitemdust";
    }

    @Override
    public String getSyntax() {
        return "giveitemdust <player> <group> [type] [percentage]";
    }

    @Override
    public String getDescription() {
        return "Give item dust.";
    }
}
