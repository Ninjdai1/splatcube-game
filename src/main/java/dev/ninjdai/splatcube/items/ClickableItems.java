package dev.ninjdai.splatcube.items;

import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerEvent;

import java.util.HashMap;

import static dev.ninjdai.splatcube.items.CustomItem.idTag;

public class ClickableItems {
    public static HashMap<String, ClickableItem> itemEvents = new HashMap<>();

    public static EventNode<ItemEvent> right_node = EventNode.type("right-click-item-node", EventFilter.ITEM);
    public static EventNode<PlayerEvent> left_node = EventNode.type("left-click-item-node", EventFilter.PLAYER);
    static {
        right_node.addListener(PlayerUseItemEvent.class, e -> {
            if (e.getItemStack().hasTag(idTag) && itemEvents.containsKey(e.getItemStack().getTag(idTag))) {
                e.setCancelled(true);
                itemEvents.get(e.getItemStack().getTag(idTag)).getRightClickEvent().accept(e);
            }
        });
        left_node.addListener(PlayerHandAnimationEvent.class, e -> {
            if(e.getHand() != Player.Hand.MAIN) return;
            if (e.getPlayer().getItemInMainHand().hasTag(idTag)){
                //e.setCancelled(true);
                itemEvents.get(e.getPlayer().getItemInMainHand().getTag(idTag)).getLeftClickEvent().accept(e);
            }
        });
    }

    public static void addItem(ClickableItem item){
        itemEvents.put(item.getId(), item);
    }
}