package dev.ninjdai.splatcube.gui;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Gui extends Inventory {

    public static EventNode<InventoryEvent> node = EventNode.type("gui-node", EventFilter.INVENTORY);
    public HashMap<Integer, GuiItem> slotItems = new HashMap<>();

    public Gui(@NotNull InventoryType inventoryType, @NotNull Component title) {
        super(inventoryType, title);

        node.addListener(InventoryPreClickEvent.class, e -> {
            if (e.getInventory() == this) {
                e.setCancelled(true);
                if (slotItems.containsKey(e.getSlot())) {
                    slotItems.get(e.getSlot()).getEvent().accept(e, slotItems.get(e.getSlot()));
                }
            }
        });
    }

    public void updateItemStack(int slot) {
        if(slotItems.containsKey(slot)) {
            this.setItemStack(slot, slotItems.get(slot).getItemStack());
        }
    }

    public void addGuiItem(int slot, @NotNull GuiItem item) {
        this.setItemStack(slot, item.getItemStack());
        if (item.getEvent() != null) {
            slotItems.put(slot, item);
        }
    }

    public void addGuiItem(int x, int y, @NotNull GuiItem item) {
        addGuiItem(x + (y * 9), item);
    }

    public boolean isSlotFree(int slot) {
        return this.getItemStack(slot).isAir();
    }

    public void applyMask(Mask mask, ItemStack maskItem) {
        for (int i = 0; i < mask.getWidth(); i++) {
            for (int j = 0; j < mask.getHeight(); j++) {
                if (mask.isMasked(i, j)) {
                    if (this.isSlotFree(i + (j * 9))) this.addGuiItem(i + (j * 9), new GuiItem(maskItem));
                }
            }
        }
    }
}