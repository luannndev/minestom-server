package dev.luan.server.textdisplay.impl;

import dev.luan.server.attribute.ServerAttribute;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;

public final class TextDisplayData {
    public static ServerAttribute<Vec> SCALE = new ServerAttribute<>(new Vec(1, 1, 1));
    public static ServerAttribute<Component> CONTENT = new ServerAttribute<>(Component.empty());
    public static ServerAttribute<Integer> BACKGROUND = new ServerAttribute<>(0x000000);
    public static ServerAttribute<AbstractDisplayMeta.BillboardConstraints> BILLBOARD = new ServerAttribute<>(AbstractDisplayMeta.BillboardConstraints.CENTER);
}