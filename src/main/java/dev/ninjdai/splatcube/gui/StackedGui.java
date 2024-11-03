package dev.ninjdai.splatcube.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

public class StackedGui extends Gui {
    public StackedGui(@NotNull InventoryType inventoryType, @NotNull Component title) {
        super(inventoryType, title);
        assert inventoryType == InventoryType.CHEST_2_ROW || inventoryType == InventoryType.CHEST_3_ROW || inventoryType == InventoryType.CHEST_4_ROW || inventoryType == InventoryType.CHEST_5_ROW || inventoryType == InventoryType.CHEST_6_ROW;

        fillBottomRom();
    }

    public StackedGui(@NotNull InventoryType inventoryType, @NotNull Component title , @NotNull Inventory parent) {
        super(inventoryType, title);
        assert inventoryType == InventoryType.CHEST_2_ROW || inventoryType == InventoryType.CHEST_3_ROW || inventoryType == InventoryType.CHEST_4_ROW || inventoryType == InventoryType.CHEST_5_ROW || inventoryType == InventoryType.CHEST_6_ROW;

        fillBottomRom();
        addGuiItem(0, (this.getSize() / 9 - 1), new GuiItem(
            ItemStack.of(Material.ARROW).withCustomName(Component.text("Back").decoration(TextDecoration.ITALIC, false)).withLore(parent.getTitle().decoration(TextDecoration.ITALIC, false)),
            event -> {
                event.setCancelled(true);
                event.getPlayer().openInventory(parent);
            }
        ));
    }

    public void fillBottomRom() {
        fillBottomRom(GuiItems.GLASS_PANE_FILLER);
    }

    public void fillBottomRom(GuiItem item) {
        int y = (this.getSize() / 9 - 1);
        for (int x = 0; x < 9; x++) {
            addGuiItem(x, y, item);
        }

        addGuiItem(4, this.getSize()/9 -1, GuiItems.CLOSE_INVENTORY);
    }
}
