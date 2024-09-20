package net.minestom.server.instance.anvil;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.entity.metadata.other.EndCrystalMeta;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.DyedItemColor;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class VanillaLoader extends AnvilLoader {
    private final Path entitiesPath;

    public VanillaLoader(@NotNull Path path) {
        super(path);
        this.entitiesPath = path.resolve("entities");
    }

    @Override
    public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
        var future = super.loadChunk(instance, chunkX, chunkZ);

        int regionX = ChunkUtils.toRegionCoordinate(chunkX);
        int regionZ = ChunkUtils.toRegionCoordinate(chunkZ);

        var entityFile = entitiesPath.resolve("r." + regionX + "." + regionZ + ".mca").toFile();
        if (!entityFile.exists()) return future;
        try (RegionFile regionFile = new RegionFile(entityFile.toPath())) {
            var data = regionFile.readChunkData(chunkX, chunkZ);
            if (data != null) {
                var entitiesList = data.getList("Entities");

                entitiesList.forEach(entityTag -> {
                    var binaryTag = (CompoundBinaryTag) entityTag;
                    String id = binaryTag.getString("id");
                    var entityType = EntityType.fromNamespaceId(id);
                    if (entityType == null) throw new RuntimeException("Unknown entity type from id " + id);

                    var posTag = (ListBinaryTag) binaryTag.get("Pos");
                    var rotationTag = (ListBinaryTag) binaryTag.get("Rotation");

                    double[] posList = new double[]{0, 0, 0};
                    if (posTag != null) {
                        for (int i = 0; i < posTag.size(); i++) {
                            var tag = (DoubleBinaryTag) posTag.get(i);
                            posList[i] = tag.value();
                        }
                    }

                    float[] rotationList = new float[]{0, 0};
                    if (rotationTag != null) {
                        for (int i = 0; i < rotationTag.size(); i++) {
                            var tag = (FloatBinaryTag) rotationTag.get(i);
                            rotationList[i] = tag.value();
                        }
                    }

                    var pos = new Pos(posList[0], posList[1], posList[2], rotationList[0], rotationList[1]);
                    if (entityType.equals(EntityType.ITEM_FRAME)) {
                        return;
                    } else if (entityType.equals(EntityType.END_CRYSTAL)) {
                        var entity = new Entity(EntityType.END_CRYSTAL);
                        var meta = (EndCrystalMeta) entity.getEntityMeta();
                        meta.setShowingBottom(false);

                        entity.setInstance(instance, pos);
                        return;
                    }

                    if (!entityType.equals(EntityType.ARMOR_STAND)) {
                        new Entity(entityType).setInstance(instance, pos);
                        return;
                    }

                    var entity = new LivingEntity(entityType);
                    entity.setInstance(instance, pos);

                    if (entity.getEntityMeta() instanceof ArmorStandMeta meta) {
                        meta.setSmall(binaryTag.getBoolean("Small"));
                        meta.setHasArms(binaryTag.getBoolean("ShowArms"));
                        meta.setHasNoBasePlate(binaryTag.getBoolean("NoBasePlate"));
                        meta.setHasNoGravity(binaryTag.getBoolean("NoGravity"));
                        meta.setInvisible(binaryTag.getBoolean("Invisible"));

                        var pose = binaryTag.getCompound("Pose");
                        if (posTag != null) {
                            var headVec = new Vec(0, 0, 0);
                            var leftArmVec = new Vec(0, 0, 0);
                            var leftLegVec = new Vec(0, 0, 0);
                            var rightArmVec = new Vec(0, 0, 0);
                            var rightLegVec = new Vec(0, 0, 0);

                            for (int i = 0; i < 3; i++) {
                                var headList = pose.getList("Head");
                                var leftArmList = pose.getList("LeftArm");
                                var leftLegList = pose.getList("LeftLeg");
                                var rightArmList = pose.getList("RightArm");
                                var rightLegList = pose.getList("RightLeg");

                                if (i == 0) {
                                    if (headList.size() >= 3) headVec = headVec.withX(headList.getDouble(i));
                                    if (leftArmList.size() >= 3)
                                        leftArmVec = leftArmVec.withX(leftArmList.getDouble(i));
                                    if (leftLegList.size() >= 3)
                                        leftLegVec = leftLegVec.withX(leftLegList.getDouble(i));
                                    if (rightArmList.size() >= 3)
                                        rightArmVec = rightArmVec.withX(rightArmList.getDouble(i));
                                    if (rightLegList.size() >= 3)
                                        rightLegVec = rightLegVec.withX(rightLegList.getDouble(i));
                                }
                                if (i == 1) {
                                    if (headList.size() >= 3) headVec = headVec.withY(headList.getDouble(i));
                                    if (leftArmList.size() >= 3)
                                        leftArmVec = leftArmVec.withY(leftArmList.getDouble(i));
                                    if (leftLegList.size() >= 3)
                                        leftLegVec = leftLegVec.withY(leftLegList.getDouble(i));
                                    if (rightArmList.size() >= 3)
                                        rightArmVec = rightArmVec.withY(rightArmList.getDouble(i));
                                    if (rightLegList.size() >= 3)
                                        rightLegVec = rightLegVec.withY(rightLegList.getDouble(i));
                                }
                                if (i == 2) {
                                    if (headList.size() >= 3) headVec = headVec.withZ(headList.getDouble(i));
                                    if (leftArmList.size() >= 3)
                                        leftArmVec = leftArmVec.withZ(leftArmList.getDouble(i));
                                    if (leftLegList.size() >= 3)
                                        leftLegVec = leftLegVec.withZ(leftLegList.getDouble(i));
                                    if (rightArmList.size() >= 3)
                                        rightArmVec = rightArmVec.withZ(rightArmList.getDouble(i));
                                    if (rightLegList.size() >= 3)
                                        rightLegVec = rightLegVec.withZ(rightLegList.getDouble(i));
                                }
                            }
                            meta.setHeadRotation(headVec);
                            meta.setLeftArmRotation(leftArmVec);
                            meta.setLeftLegRotation(leftLegVec);
                            meta.setRightArmRotation(rightArmVec);
                            meta.setRightLegRotation(rightLegVec);
                        }

                        var items = (ListBinaryTag) binaryTag.get("ArmorItems");
                        if (items != null) {
                            for (int i = 0; i < items.size(); i++) {
                                var tag = items.getCompound(i);
                                var material = Material.fromNamespaceId(tag.getString("id"));

                                if (material == null) {
                                    continue;
                                }

                                var stack = ItemStack.of(material);
                                if (material.name().startsWith("minecraft:leather_")) {
                                    var rgb = tag.getCompound("components").getCompound("minecraft:dyed_color").getInt("rgb");
                                    stack = stack.with(ItemComponent.DYED_COLOR, DyedItemColor.LEATHER.withColor(new Color(rgb)));
                                } else if (material.name().equals("minecraft:player_head")) {
                                    var properties = (CompoundBinaryTag) tag.getCompound("components").getCompound("minecraft:profile").getList("properties").get(0);
                                    stack = stack.with(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin(properties.getString("value"), properties.getString("name"))));
                                }

                                if (i == 0) entity.setBoots(stack);
                                if (i == 1) entity.setLeggings(stack);
                                if (i == 2) entity.setChestplate(stack);
                                if (i == 3) entity.setHelmet(stack);
                            }
                        }
                    }
                });
            }

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return future;
    }
}