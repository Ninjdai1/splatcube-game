package dev.ninjdai.splatcube.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class GuiItems {
    public static GuiItem CLOSE_INVENTORY = new GuiItem(
        ItemStack.of(Material.BARRIER).withCustomName(Component.text("Close", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)),
        event -> {
            event.setCancelled(true);
            event.getPlayer().closeInventory();
        }
    );
    public static GuiItem GLASS_PANE_FILLER = new GuiItem(ItemStack.of(Material.BLACK_STAINED_GLASS_PANE).withCustomName(Component.text("")));
}
