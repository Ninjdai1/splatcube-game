package dev.ninjdai.splatcube.items;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

@Getter
public enum Items {
    BLASTER(new RangedWeapon(ItemStack.of(Material.STICK).withCustomName(Component.text("Blaster").decoration(TextDecoration.ITALIC, false)),
        "blaster",
        1,
        0.1f,
        8,
        2
    )),
    SNIPER(new RangedWeapon(ItemStack.of(Material.CARROT_ON_A_STICK).withCustomName(Component.text("Sninker").decoration(TextDecoration.ITALIC, false)),
        "sninker",
        4,
        0.1f,
        13,
        1
    ));

    private final CustomItem item;
    Items(CustomItem item) {
        this.item = item;
    }
}
