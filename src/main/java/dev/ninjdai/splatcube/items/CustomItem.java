package dev.ninjdai.splatcube.items;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

@Getter @Setter
public class CustomItem {
    public static Tag<String> idTag = Tag.String("id");

    private final ItemStack itemStack;
    private final String id;

    public CustomItem(ItemStack itemStack, String id) {
        this.itemStack = itemStack.withTag(idTag, id);
        this.id = id;
    }
}
