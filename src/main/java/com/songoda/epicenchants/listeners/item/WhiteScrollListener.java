package com.songoda.epicenchants.listeners.item;

import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.utils.objects.ItemBuilder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WhiteScrollListener extends ItemListener {
    public WhiteScrollListener(EpicEnchants instance) {
        super(instance);
    }

    @Override
    void onApply(InventoryClickEvent event, NBTItem cursor, NBTItem current) {
        if (!cursor.hasTag("white-scroll") || !cursor.getBoolean("white-scroll")) {
            return;
        }

        event.setCancelled(true);

        if (current.hasTag("protected")) {
            this.instance.getLocale().getMessage("whitescroll.alreadyapplied")
                    .sendPrefixedMessage(event.getWhoClicked());
            return;
        }

        if (!this.instance.getItemGroup().isValid(XMaterial.matchXMaterial(event.getCurrentItem().getType()))) {
            return;
        }

        current.setBoolean("protected", true);
        this.instance.getLocale().getMessage("whitescrollapplied").sendPrefixedMessage(event.getWhoClicked());

        ItemStack toSet = new ItemBuilder(current.getItem()).addLore(this.instance.getSpecialItems().getWhiteScrollLore()).build();

        event.getClickedInventory().setItem(event.getSlot(), toSet);
        useItem(event);
    }
}
