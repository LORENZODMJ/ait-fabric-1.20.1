package mdteam.ait.core.util;

import io.wispforest.owo.ops.WorldOps;
import mdteam.ait.core.AITDimensions;
import mdteam.ait.core.blockentities.door.DoorBlockEntity;
import mdteam.ait.core.blockentities.door.ExteriorBlockEntity;
import mdteam.ait.core.util.data.AbsoluteBlockPos;
import mdteam.ait.core.util.data.Corners;
import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.TardisDesktop;
import mdteam.ait.tardis.TardisTravel;
import mdteam.ait.tardis.manager.TardisManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

import static mdteam.ait.tardis.wrapper.server.manager.ServerTardisManager.CHANGE_EXTERIOR;

@SuppressWarnings("unused")
public class TardisUtil {

    private static final Random RANDOM = new Random();

    private static MinecraftServer SERVER;
    private static ServerWorld TARDIS_DIMENSION;

    private static Path SAVE_PATH;

    public static void init() {
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                SERVER = null;
            }
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            //System.out.println("Loaded world " + world.getRegistryKey());
            SAVE_PATH = server.getSavePath(WorldSavePath.ROOT);

            if (world.getRegistryKey() == World.OVERWORLD) {
                SERVER = server;
            }

            if (world.getRegistryKey() == AITDimensions.TARDIS_DIM_WORLD) {
                TARDIS_DIMENSION = world;
            }
        });
    }

    public static void setServer(MinecraftServer server) {
        SERVER = server;
    }

    public static ServerWorld getTardisDimension() {
        return TARDIS_DIMENSION;
    }

    public static Path getSavePath() {
        return SAVE_PATH;
    }

    public static void changeExteriorWithScreen(UUID uuid) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);
        ClientPlayNetworking.send(CHANGE_EXTERIOR, buf);
    }

    public static boolean inBox(Box box, BlockPos pos) {
        return pos.getX() <= box.maxX && pos.getX() >= box.minX &&
                pos.getZ() <= box.maxZ && pos.getZ() >= box.minZ;
    }

    public static boolean inBox(Corners corners, BlockPos pos) {
        return inBox(corners.getBox(), pos);
    }

    public static DoorBlockEntity getDoor(Tardis tardis) {
        if (!(TardisUtil.getTardisDimension().getBlockEntity(tardis.getDesktop().getInteriorDoorPos()) instanceof DoorBlockEntity door))
            return null;

        return door;
    }

    public static ExteriorBlockEntity getExterior(Tardis tardis) {
        if (!(tardis.getTravel().getPosition().getBlockEntity() instanceof ExteriorBlockEntity exterior))
            return null;

        return exterior;
    }

    public static Corners findInteriorSpot() {
        BlockPos first = findRandomPlace();

        return new Corners(
                first, first.add(256, 0, 256)
        );
    }

    public static BlockPos findRandomPlace() {
        return new BlockPos(RANDOM.nextInt(100000), 0, RANDOM.nextInt(100000));
    }

    public static BlockPos findBlockInTemplate(StructureTemplate template, BlockPos pos, Direction direction, Block targetBlock) {
        List<StructureTemplate.StructureBlockInfo> list = template.getInfosForBlock(
                pos, new StructurePlacementData().setRotation(
                        TardisUtil.directionToRotation(direction)
                ), targetBlock
        );

        if (list.isEmpty())
            return null;

        return list.get(0).pos();
    }

    public static BlockRotation directionToRotation(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockRotation.CLOCKWISE_180;
            case EAST -> BlockRotation.COUNTERCLOCKWISE_90;
            case WEST -> BlockRotation.CLOCKWISE_90;
            default -> BlockRotation.NONE;
        };
    }

    public static BlockPos offsetInteriorDoorPosition(Tardis tardis) {
        return TardisUtil.offsetInteriorDoorPosition(tardis.getDesktop());
    }

    public static BlockPos offsetInteriorDoorPosition(TardisDesktop desktop) {
        return TardisUtil.offsetDoorPosition(desktop.getInteriorDoorPos());
    }

    public static BlockPos offsetExteriorDoorPosition(Tardis tardis) {
        return TardisUtil.offsetExteriorDoorPosition(tardis.getTravel());
    }

    public static BlockPos offsetExteriorDoorPosition(TardisTravel travel) {
        return TardisUtil.offsetDoorPosition(travel.getPosition());
    }

    public static BlockPos offsetDoorPosition(AbsoluteBlockPos.Directed pos) {
        return switch (pos.getDirection()) {
            case DOWN, UP -> throw new IllegalArgumentException("Cannot adjust door position with direction: " + pos.getDirection());
            case NORTH -> new BlockPos.Mutable(pos.getX() + 0.5, pos.getY(), pos.getZ() - 1);
            case SOUTH -> new BlockPos.Mutable(pos.getX() + 0.5, pos.getY(), pos.getZ() + 1);
            case EAST -> new BlockPos.Mutable(pos.getX() + 1, pos.getY(), pos.getZ() + 0.5);
            case WEST -> new BlockPos.Mutable(pos.getX() - 1, pos.getY(), pos.getZ() + 0.5);
        };
    }

    public static void teleportOutside(Tardis tardis, ServerPlayerEntity player) {
        TardisUtil.teleportWithDoorOffset(player, tardis.getTravel().getPosition());
    }

    public static void teleportInside(Tardis tardis, ServerPlayerEntity player) {
        TardisUtil.teleportWithDoorOffset(player, tardis.getDesktop().getInteriorDoorPos());
    }

    private static void teleportWithDoorOffset(ServerPlayerEntity player, AbsoluteBlockPos.Directed pos) {
        Vec3d vec = TardisUtil.offsetDoorPosition(pos).toCenterPos();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SERVER.executeSync(() -> {WorldOps.teleportToWorld(player, (ServerWorld) pos.getWorld(), vec, pos.getDirection().asRotation(), player.getPitch());
                    player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player)); });
            }
        }, 20);
    }

    public static Tardis findTardisByInterior(BlockPos pos) {
        for (Tardis tardis : TardisManager.getInstance().getLookup().values()) {
            if (TardisUtil.inBox(tardis.getDesktop().getCorners(), pos))
                return tardis;
        }

        return null;
    }

    @Nullable
    public static PlayerEntity getPlayerInsideInterior(Tardis tardis) {
        return getPlayerInsideInterior(tardis.getDesktop().getCorners());
    }

    @Nullable
    public static PlayerEntity getPlayerInsideInterior(Corners corners) {
        for (PlayerEntity player : TardisUtil.getTardisDimension().getPlayers()) {
            if (TardisUtil.inBox(corners, player.getBlockPos()))
                return player;
        }
        return null;
    }

    public static List<PlayerEntity> getPlayersInInterior(Corners corners) {
        List<PlayerEntity> list = List.of();

        for (PlayerEntity player : TardisUtil.getTardisDimension().getPlayers()) {
            if (inBox(corners, player.getBlockPos())) list.add(player);
        }

        return list;
    }

    public static ServerWorld findWorld(RegistryKey<World> key) {
        //System.out.println("Trying to find " + key);
        return SERVER.getWorld(key);
    }

    public static ServerWorld findWorld(Identifier identifier) {
        return TardisUtil.findWorld(RegistryKey.of(RegistryKeys.WORLD, identifier));
    }

    public static ServerWorld findWorld(String identifier) {
        return TardisUtil.findWorld(new Identifier(identifier));
    }
}
