package dev.luan.server.blockhandler;

import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public final class EmptyBlockHandler implements BlockHandler {
    private final String nameSpaceId;

    public EmptyBlockHandler(String nameSpaceId) {
        this.nameSpaceId = nameSpaceId;
    }

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from(this.nameSpaceId);
    }
}