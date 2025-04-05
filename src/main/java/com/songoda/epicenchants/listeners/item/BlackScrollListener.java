package com.songoda.epicenchants.listeners.item;

import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTCompound;
import com.songoda.core.third_party.de.tr7zw.nbtapi.NBTItem;
import com.songoda.epicenchants.objects.Enchant;
import com.songoda.epicenchants.EpicEnchants;
import com.songoda.epicenchants.utils.single.RomanNumber;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static com.songoda.epicenchants.utils.single.GeneralUtils.getRandomElement;

public class BlackScrollListener extends ItemListener {
    public BlackScrollListener(EpicEnchants instance) {
        super(instance);
    }

    @Override
    void onApply(InventoryClickEvent event, NBTItem cursor, NBTItem current) {
        if (!cursor.hasTag("black-scroll") || !cursor.getBoolean("black-scroll")) {
            return;
        }

        event.setCancelled(true);
        NBTCompound compound = current.getCompound("enchants");

        if (compound == null || compound.getKeys().isEmpty()) {
            this.instance.getLocale().getMessage("blackscroll.noenchants")
                    .sendPrefixedMessage(event.getWhoClicked());
            return;
        }

        String id = getRandomElement(compound.getKeys());
        int level = compound.getInteger(id);
        Enchant enchant = this.instance.getEnchantManager().getValueUnsafe(id);
        ItemStack toSet = this.instance.getEnchantUtils().removeEnchant(event.getCurrentItem(), enchant);

        event.getWhoClicked().getInventory().addItem(enchant.getBook().get(enchant, level, cursor.getInteger("success-rate"), 100));
        event.setCurrentItem(toSet);

        this.instance.getLocale().getMessage("blackscroll.success")
                .processPlaceholder("enchant", enchant.getIdentifier())
                .processPlaceholder("group_color", enchant.getGroup().getColor())
                .processPlaceholder("group_name", enchant.getGroup().getName())
                .processPlaceholder("level", RomanNumber.toRoman(level))
                .sendPrefixedMessage(event.getWhoClicked());

        useItem(event);
    }
}
