package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.objects.Group;
import com.songoda.epicenchants.EpicEnchants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CommandGiveRandomBook extends AbstractCommand {

    private final EpicEnchants plugin;

    public CommandGiveRandomBook(EpicEnchants plugin) {
        super(CommandType.CONSOLE_OK, "giverandombook");
        this.plugin = plugin;
    }

    //giverandombook <player> <group>
    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 2 || args.length > 6) {
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

        target.getPlayer().getInventory().addItem(this.plugin.getSpecialItems().getMysteryBook(group));
        this.plugin.getLocale().getMessage("command.randombook.received")
                .sendPrefixedMessage(target.getPlayer());
        this.plugin.getLocale().getMessage("command.randombook.gave")
                .processPlaceholder("player", target.getPlayer().getName())
                .sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return this.plugin.getGroupManager()
                    .getValues()
                    .stream()
                    .map(Group::getIdentifier)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicenchants.giverandombook";
    }

    @Override
    public String getSyntax() {
        return "giverandombook <player> <group>";
    }

    @Override
    public String getDescription() {
        return "Give random enchant books to players.";
    }
}
