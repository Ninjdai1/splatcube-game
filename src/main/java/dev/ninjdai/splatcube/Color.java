package dev.ninjdai.splatcube;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

@Getter
public enum Color {
    ORANGE(Block.ORANGE_CONCRETE, Block.WAXED_CUT_COPPER_SLAB, Block.WAXED_CUT_COPPER_STAIRS, Material.ORANGE_CONCRETE, NamedTextColor.GOLD),
    BLUE(Block.BLUE_CONCRETE, Block.WAXED_EXPOSED_CUT_COPPER_SLAB, Block.WAXED_EXPOSED_CUT_COPPER_STAIRS, Material.BLUE_CONCRETE, NamedTextColor.BLUE),
    PINK(Block.PINK_CONCRETE, Block.WAXED_WEATHERED_CUT_COPPER_SLAB, Block.WAXED_WEATHERED_CUT_COPPER_STAIRS, Material.PINK_CONCRETE, NamedTextColor.LIGHT_PURPLE),
    LIME(Block.LIME_CONCRETE, Block.WAXED_OXIDIZED_CUT_COPPER_SLAB, Block.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Material.LIME_CONCRETE, NamedTextColor.GREEN),;

    private final Block block;
    private final Block slab;
    private final Block stairs;
    private final Material material;
    private final TextColor color;
    Color(Block block, Block slab, Block stairs, Material material, TextColor color) {
        this.block = block;
        this.slab = slab;
        this.stairs = stairs;
        this.material = material;
        this.color = color;
    }
}
