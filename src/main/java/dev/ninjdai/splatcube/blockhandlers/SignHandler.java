package dev.ninjdai.splatcube.blockhandlers;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class SignHandler implements BlockHandler {
    public static final NamespaceID KEY = NamespaceID.from("minecraft:sign");

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return Set.of(
                Tag.Byte("is_waxed"),
                Tag.NBT("front_text"),
                Tag.NBT("back_text"));
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return KEY;
    }

}
