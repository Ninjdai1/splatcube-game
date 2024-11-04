package dev.ninjdai.splatcube;

import dev.ninjdai.splatcube.items.Items;
import dev.ninjdai.splatcube.utils.BlockUtils;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class SplatPlayer extends Player {
    public static final double INK_SWIM_SPEED = 0.15;
    public static final double WALK_SPEED = 0.1;
    public static final double GROUND_SWIM_SPEED = 0.05;

    public static final double DEFAULT_SCALE = 0.85;

    @Setter
    private Color color = Color.BLUE;
    private final int maxInk = 200;
    private int ink = maxInk;
    private final Items weapon = Items.BLASTER;
    private boolean isSquid = false;
    private boolean swimmingUp = false;

    private final Entity squid = new Entity(EntityType.ITEM_DISPLAY);

    public SplatPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
        setInk(maxInk);
        setScale(DEFAULT_SCALE);
        setSneakSpeed(1);

        ItemDisplayMeta m = (ItemDisplayMeta) squid.getEntityMeta();
        m.setItemStack(ItemStack.AIR);
        m.setPosRotInterpolationDuration(2);
        squid.setSynchronizationTicks(Long.MAX_VALUE);
        squid.setNoGravity(true);
        squid.setBoundingBox(boundingBox);
    }

    @Override
    public void spawn() {
        super.spawn();
        squid.setInstance(instance, position);
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

    public void toggleSquidForm(boolean setSquid) {
        isSquid = setSquid;
        setInvisible(isSquid);
        if (isSquid) {
            inventory.setItemStack(4, ItemStack.AIR);
            ItemDisplayMeta m = (ItemDisplayMeta) squid.getEntityMeta();
            m.setItemStack(ItemStack.of(Material.AMETHYST_BLOCK));
            squid.addPassenger(this);
        } else {
            inventory.setItemStack(4, getWeapon().getItem().getItemStack());
            setScale(DEFAULT_SCALE);
            ItemDisplayMeta m = (ItemDisplayMeta) squid.getEntityMeta();
            m.setItemStack(ItemStack.AIR);
            squid.removePassenger(this);
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
            if (isSquid) squid.teleport(Main.MAP.team1Config.spawnPos.getFirst());
            else teleport(Main.MAP.team1Config.spawnPos.getFirst());
        }

        if (isSquid) {
            float forward = getVehicleInformation().getForward();
            float sideways = getVehicleInformation().getSideways();
            boolean shouldJump = getVehicleInformation().shouldJump();
            
            Vec vel = new Vec(Math.cos(position.yaw() / 57.3) * sideways, Math.sin(position.yaw() / 57.3) * sideways)
                    .add(Math.sin(- position.yaw() / 57.3) * forward, 0, Math.cos(- position.yaw() / 57.3) * forward);

            if (shouldJump && instance.getBlock(squid.getPosition().add(0, -1, 0))!=Block.AIR) {
                vel = vel.add(0, 10, 0);
            }

            Block nextBlock = instance.getBlock(squid.getPosition().add(vel));
            if (nextBlock == color.getBlock()) {
                vel = vel.add(0, 1, 0);swimmingUp = true;
            } else {
                if (swimmingUp){
                    if((forward != 0 || sideways != 0)) swimmingUp = false;
                }
                else vel = vel.add(0, -1, 0);
            }

            squid.setVelocity(vel.mul(7));
        } else {
            squid.teleport(position);
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
