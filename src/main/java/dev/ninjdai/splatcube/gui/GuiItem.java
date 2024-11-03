package dev.ninjdai.splatcube.gui;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class GuiItem {
    @Setter private ItemStack itemStack;
    private final BiConsumer<InventoryPreClickEvent, GuiItem> event;

    public GuiItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.event = (inventoryPreClickEvent, g) -> inventoryPreClickEvent.setCancelled(true);
    }

    public GuiItem(@NotNull ItemStack itemStack, Consumer<InventoryPreClickEvent> event) {
        this.itemStack = itemStack;
        this.event = ((inventoryPreClickEvent, item) -> event.accept(inventoryPreClickEvent));
    }

    public GuiItem(@NotNull ItemStack itemStack, BiConsumer<InventoryPreClickEvent, GuiItem> event) {
        this.itemStack = itemStack;
        this.event = event;
    }
}