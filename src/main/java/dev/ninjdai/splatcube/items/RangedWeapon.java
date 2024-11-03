package dev.ninjdai.splatcube.items;

import dev.ninjdai.splatcube.SplatPlayer;
import dev.ninjdai.splatcube.projectiles.PaintBall;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RangedWeapon extends ClickableItem{
    public RangedWeapon(@NotNull ItemStack itemStack, @NotNull String id, float power, float spread, int inkConsumption, int radius) {
        super(itemStack, id, event->{
            SplatPlayer player = (SplatPlayer) event.getPlayer();
            if (player.isSquid()) return;

            if (player.getInk() < inkConsumption) return;

            PaintBall projectile = new PaintBall(player, radius);
            projectile.setItem(ItemStack.of(
                    player.getColor().getMaterial()
            ));

            player.addInk(-inkConsumption);

            Pos pos = player.getPosition().add(0, player.getEyeHeight(), 0);
            projectile.setInstance(event.getInstance(), pos);
            projectile.shoot(pos, power, spread);
        }, event->{});
    }
}
