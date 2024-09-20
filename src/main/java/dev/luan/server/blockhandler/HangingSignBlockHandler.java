package dev.luan.server.blockhandler;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public final class HangingSignBlockHandler implements BlockHandler {

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("minecraft:hanging_sign");
    }

    @Override
    public @NotNull Collection<Tag<?>> getBlockEntityTags() {
        return List.of(Tag.Boolean("is_waxed"), Tag.NBT("front_text"), Tag.NBT("back_text"));
    }
}