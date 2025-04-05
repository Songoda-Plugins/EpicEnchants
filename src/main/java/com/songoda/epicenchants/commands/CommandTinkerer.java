package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.menus.TinkererMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandTinkerer extends AbstractCommand {
    private final EpicEnchants plugin;

    public CommandTinkerer(EpicEnchants plugin) {
        super(CommandType.PLAYER_ONLY, "tinkerer");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        new TinkererMenu(this.plugin, this.plugin.getFileManager().getConfiguration("menus/tinkerer-menu")).open(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicenchants.tinkerer";
    }

    @Override
    public String getSyntax() {
        return "tinkerer";
    }

    @Override
    public String getDescription() {
        return "Opens the Tinkerer.";
    }
}
