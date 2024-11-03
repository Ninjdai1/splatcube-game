package dev.ninjdai.splatcube.items;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter @Setter
public class ClickableItem extends CustomItem {
    private Consumer<PlayerUseItemEvent> rightClickEvent;
    private Consumer<PlayerHandAnimationEvent> leftClickEvent;

    public ClickableItem(@NotNull ItemStack itemStack, @NotNull String id, Consumer<PlayerUseItemEvent> rightClickEvent, Consumer<PlayerHandAnimationEvent> leftClickEvent) {
        super(itemStack, id);
        this.rightClickEvent = rightClickEvent;
        this.leftClickEvent = leftClickEvent;

        ClickableItems.addItem(this);
    }
}
