package dev.ninjdai.splatcube;

import dev.ninjdai.splatcube.items.Items;
import dev.ninjdai.splatcube.utils.BlockUtils;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class SplatPlayer extends Player {
    public static final double INK_SWIM_SPEED = 0.15;
    public static final double WALK_SPEED = 0.1;
    public static final double GROUND_SWIM_SPEED = 0.05;

    public static final double DEFAULT_SCALE = 0.85;
    public static final double SQUID_SCALE = 0.5;

    @Setter
    private Color color = Color.BLUE;
    private final int maxInk = 200;
    private int ink = maxInk;
    private Items weapon = Items.BLASTER;
    private boolean isSquid = false;

    public SplatPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
        setInk(maxInk);
        setScale(DEFAULT_SCALE);
        setSneakSpeed(1);
    }

    public void setSpeed(double value) {
        getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(value);
    }

    public void setSneakSpeed(double value) {
        getAttribute(Attribute.PLAYER_SNEAKING_SPEED).setBaseValue(value);
    }

    public void setScale(double value) {
        getAttribute(Attribute.GENERIC_SCALE).setBaseValue(value);
    }

    public void toggleSquidForm(boolean squid) {
        isSquid = squid;
        setInvisible(isSquid);
        if (isSquid) {
            inventory.setItemStack(4, ItemStack.AIR);
            setScale(SQUID_SCALE);
        } else {
            inventory.setItemStack(4, getWeapon().getItem().getItemStack());
            setScale(DEFAULT_SCALE);
        }
    }

    public Block getBelowBlock() {
        return instance.getBlock(
                getPosition().y() == getPosition().blockY() ? this.position.add(0, -1, 0) : this.position
        );
    }

    @Override
    protected void movementTick() {
        super.movementTick();
        if (position.y() <= Main.MAP.minHeight) {
            teleport(Main.MAP.team1Config.spawnPos.getFirst());
        }

        Block b = getBelowBlock();
        if(isOnGround() && b.id() != Block.AIR.id()) {
            if (isSquid() && BlockUtils.isPlayerInk(b, this)) {
                setInvisible(true);
                setSpeed(INK_SWIM_SPEED);
                if (getInk() < maxInk) {
                    addInk(2);
                }
            } else if (isSquid()) {
                setInvisible(false);
                setSpeed(GROUND_SWIM_SPEED);
            } else {
                setSpeed(WALK_SPEED);
            }
        } else {
            setInvisible(false);
        }
    }

    @Override
    public void tick(long time) {
        super.tick(time);
    }

    public void addInk(int inkAmount) {
        setInk(ink + inkAmount);
    }

    public void setInk(int newInk) {
        if (getGameMode()== GameMode.CREATIVE) return;
        if (ink == newInk) return;
        ink = Math.min(newInk, maxInk);
        setExp((float) ink / maxInk);
    }

    public int getInk() {
        if (getGameMode()== GameMode.CREATIVE) return maxInk;
        return ink;
    }
}
