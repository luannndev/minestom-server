package dev.luan.server.textdisplay;

import dev.luan.server.textdisplay.impl.TextDisplayData;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TextDisplay {
    private final List<Entity> entities;

    /**
     * @param instance The instance where the text display will be displayed.
     * @param pos The position where the text display will be displayed.
     * @param lines The lines of the text display.
     */
    public TextDisplay(Instance instance, Pos pos, float spacing, TextLineBuilder... lines) {
        this.entities = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            var line = Arrays.stream(lines).toList().reversed().get(i);
            var entity = new Entity(EntityType.TEXT_DISPLAY);
            var meta = (TextDisplayMeta) entity.getEntityMeta();

            entity.setNoGravity(true);
            meta.setBackgroundColor(line.get(TextDisplayData.BACKGROUND));

            meta.setScale(line.get(TextDisplayData.SCALE));
            meta.setText(line.get(TextDisplayData.CONTENT));
            meta.setBillboardRenderConstraints(line.get(TextDisplayData.BILLBOARD));

            var finalPos = pos.add(0, (double) i * spacing, 0);
            entity.setInstance(instance, finalPos).thenAccept(unused -> entity.teleport(finalPos));

            this.entities.add(entity);
        }
    }

    /**
     * Removes all the entities from the instance.
     */
    public void remove() {
        for (Entity entity : this.entities) entity.remove();
        this.entities.clear();
    }
}