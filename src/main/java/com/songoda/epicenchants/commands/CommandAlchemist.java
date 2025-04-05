package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.menus.AlchemistMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandAlchemist extends AbstractCommand {
    private final EpicEnchants plugin;

    public CommandAlchemist(EpicEnchants plugin) {
        super(CommandType.PLAYER_ONLY, "alchemist");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        new AlchemistMenu(this.plugin, this.plugin.getFileManager().getConfiguration("menus/alchemist-menu")).open(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicenchants.alchemist";
    }

    @Override
    public String getSyntax() {
        return "alchemist";
    }

    @Override
    public String getDescription() {
        return "Opens the Alchemist.";
    }
}
