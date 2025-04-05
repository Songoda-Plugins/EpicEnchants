package com.songoda.epicenchants.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.menus.EnchanterMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandEnchanter extends AbstractCommand {
    private final EpicEnchants plugin;

    public CommandEnchanter(EpicEnchants plugin) {
        super(CommandType.PLAYER_ONLY, "enchanter");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        new EnchanterMenu(this.plugin, this.plugin.getFileManager().getConfiguration("menus/enchanter-menu"), player).open(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicenchants.enchanter";
    }

    @Override
    public String getSyntax() {
        return "enchanter";
    }

    @Override
    public String getDescription() {
        return "Opens the Enchanter.";
    }
}
