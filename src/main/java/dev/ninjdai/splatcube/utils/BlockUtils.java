package dev.ninjdai.splatcube.utils;

import dev.ninjdai.splatcube.Color;
import dev.ninjdai.splatcube.SplatPlayer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.util.HashMap;
import java.util.Map;

public class BlockUtils {
    public static final Map<Integer, BlockType> blockTypeMap = new HashMap<>();

    static {
        for (Block b: Block.values()) {
            blockTypeMap.put(b.id(), getBlockType(b));
        }
    }

    public static Block paintedBlock(Block block, Color color) {
        return switch (blockTypeMap.get(block.id())) {
            case INK_IMMUNE, AIR -> block;
            case INKABLE_FULL_BLOCK -> color.getBlock();
            case INKABLE_SLAB -> color.getSlab().withProperties(block.properties());
            case INKABLE_STAIRS -> color.getStairs().withProperties(block.properties());
        };
    }

    public static boolean isPlayerInk(Block block, SplatPlayer player) {
        return block.compare(player.getColor().getBlock()) || block.compare(player.getColor().getSlab()) || block.compare(player.getColor().getStairs());
    }

    public static BlockType getBlockType(Block block) {
        if (block.isAir()) {
            return BlockType.AIR;
        }  else if (isStairs(block)) {
            return BlockType.INKABLE_STAIRS;
        } else if (isSlab(block)) {
            return BlockType.INKABLE_SLAB;
        } else if (isInkImmune(block)) {
            return BlockType.INK_IMMUNE;
        }
        return BlockType.INKABLE_FULL_BLOCK;
    }

    private static boolean isStairs(Block block) {
        return block.name().toLowerCase().trim().endsWith("stairs");
    }

    private static boolean isSlab(Block block) {
        return block.name().contains("slab");
    }

    private static boolean isCarpet(Block block) {
        return block.name().contains("carpet");
    }

    private static boolean isWool(Block block) {
        return block.name().contains("wool");
    }

    private static boolean isConcretePowder(Block block) {
        return block.name().contains("concrete_powder");
    }

    private static boolean isGlazedTerracotta(Block block) {
        return block.name().contains("glazed_terracotta");
    }

    private static boolean isShulkerBox(Block block) {
        return block.name().contains("shulker_box");
    }

    private static boolean isInkImmune(Block block) {
        if (!block.registry().occludes() || isCarpet(block) || isWool(block) || isConcretePowder(block) || isGlazedTerracotta(block) || isShulkerBox(block) || block.isLiquid() || block.compare(Block.BLAST_FURNACE)) return true;

        for (BlockFace face: BlockFace.values()) {
            if(!block.registry().collisionShape().isFaceFull(face)) return true;
        }
        return false;
    }

    public enum BlockType {
        INKABLE_FULL_BLOCK,
        INKABLE_SLAB,
        INKABLE_STAIRS,
        INK_IMMUNE,
        AIR,
    }
}
