package mdteam.ait.core.item;

import mdteam.ait.client.renderers.exteriors.ExteriorEnum;
import mdteam.ait.core.AITDesktops;
import mdteam.ait.core.AITDimensions;
import mdteam.ait.core.blockentities.ConsoleBlockEntity;
import mdteam.ait.core.blockentities.ExteriorBlockEntity;
import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.TardisTravel;
import mdteam.ait.tardis.handler.properties.PropertiesHandler;
import mdteam.ait.tardis.util.AbsoluteBlockPos;
import mdteam.ait.tardis.util.TardisUtil;
import mdteam.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static mdteam.ait.tardis.TardisTravel.State.LANDED;

public class SonicItem extends Item {

    public static final String MODE_KEY = "mode";
    public static final String INACTIVE = "inactive";

    public SonicItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = new ItemStack(this);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(MODE_KEY, 0);
        return stack;
    }

    public enum Mode implements StringIdentifiable {
        INACTIVE(Formatting.GRAY) {
            @Override
            public void run(Tardis tardis, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
            }
        },
        INTERACTION(Formatting.GREEN) {
            @Override
            public void run(Tardis tardis, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                BlockState blockState = world.getBlockState(pos);

                if (!(CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)))
                    return;

                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.4f + 0.8f);
                world.setBlockState(pos, blockState.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
        },
        OVERLOAD(Formatting.RED) {
            @Override
            public void run(Tardis tardis, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                // fixme temporary replacement for exterior changing

                BlockEntity entity = world.getBlockEntity(pos);
                Block block = world.getBlockState(pos).getBlock();

                if (entity instanceof ExteriorBlockEntity exteriorBlock) {
                    TardisTravel.State state = exteriorBlock.tardis().getTravel().getState();

                    if (!(state == TardisTravel.State.LANDED || state == TardisTravel.State.FLIGHT)) {
                        return;
                    }

                    ExteriorEnum[] values = ExteriorEnum.values();
                    int nextIndex = (exteriorBlock.tardis().getExterior().getType().ordinal() + 1) % values.length;
                    exteriorBlock.tardis().getExterior().setType(values[nextIndex]);
                    //System.out.println(exteriorBlock.getTardis().getExterior().getType());

                    exteriorBlock.tardis().markDirty();
                }

                // fixme this doesnt work because a dispenser requires that you have redstone power input or the state wont trigger :/ - Loqor
                /*if(player.isSneaking() && block instanceof DispenserBlock dispenser) {
                    world.setBlockState(pos, world.getBlockState(pos).with(Properties.TRIGGERED, true), Block.NO_REDRAW);
                    //world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
                }*/

                if(block instanceof TntBlock tnt) {
                    TntBlock.primeTnt(world, pos);
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
            }
        },
        SCANNING(Formatting.AQUA) {
            @Override
            public void run(Tardis tardis, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                // fixme temporary replacement for interior changing

                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof ExteriorBlockEntity exteriorBlock) {
                    TardisTravel.State state = exteriorBlock.tardis().getTravel().getState();
                    if (!(state == TardisTravel.State.LANDED || state == TardisTravel.State.FLIGHT))
                        return;
                    Identifier nextInteriorId = InteriorSelectItem.getNextInterior(exteriorBlock.tardis().getDesktop().getSchema().id().getPath());
                    exteriorBlock.tardis().getDesktop().changeInterior(AITDesktops.get(nextInteriorId));
                    player.sendMessage(Text.literal(nextInteriorId.toString()), true);
                } else if (world.getRegistryKey() == World.OVERWORLD && !world.isClient()) {
                    player.sendMessage(Text.literal(TardisUtil.isRiftChunk(
                                    (ServerWorld) world, pos) ? "RIFT FOUND" : "RIFT NOT FOUND")
                            .formatted(Formatting.AQUA).formatted(Formatting.BOLD));
                }
            }
        },
        TARDIS(Formatting.BLUE) {
            @Override
            public void run(Tardis tardis, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
                if (world == TardisUtil.getTardisDimension()) {
                    world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.BLOCKS, 1F, 0.2F);
                    player.sendMessage(Text.literal("Cannot translocate exterior to interior dimension"), true);
                    return;
                }

                world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS);

                TardisTravel travel = tardis.getTravel();
                BlockPos temp = player.getBlockPos();

                if (world.getBlockState(pos).isReplaceable()) temp = pos;

                PropertiesHandler.setBool(tardis.getHandlers().getProperties(), PropertiesHandler.HANDBRAKE, false);
                PropertiesHandler.setBool(tardis.getHandlers().getProperties(), PropertiesHandler.AUTO_LAND, true);

                travel.setDestination(new AbsoluteBlockPos.Directed(temp, world, player.getMovementDirection()), true);
                // fixme leave this alone for now, im getting rid of the stattenheim remotes recipe and making it creative only and removing the sonic's ability to actually make it come to you. - Loqor
                //if (travel.getState() == LANDED) travel.dematerialise(true);

                player.sendMessage(Text.literal("Handbrake disengaged, destination set to current position"), true);
            }
        };

        public Formatting format;

        Mode(Formatting format) {
            this.format = format;
        }

        public abstract void run(Tardis tardis, World world, BlockPos pos, PlayerEntity player, ItemStack stack);

        @Override
        public String asString() {
            return StringUtils.capitalize(this.toString().replace("_", " "));
        }
    }

    // fixme no me gusta nada
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();

        if (player == null)
            return ActionResult.FAIL;
        if (world.isClient()) return ActionResult.SUCCESS;

        NbtCompound nbt = itemStack.getOrCreateNbt();

        if (player.isSneaking()) {
            if (world.getBlockEntity(pos) instanceof ConsoleBlockEntity consoleBlock) {
                if (consoleBlock.getTardis() == null)
                    return ActionResult.PASS;

                link(consoleBlock.getTardis(), itemStack);
                return ActionResult.SUCCESS;
            }

            // cycleMode(itemStack);
            // return ActionResult.SUCCESS;
        }

        if (!nbt.contains(MODE_KEY)) return ActionResult.FAIL;

        Tardis tardis = getTardis(itemStack);
        if (tardis == null) return ActionResult.FAIL;

        Mode mode = intToMode(nbt.getInt(MODE_KEY));
        mode.run(tardis, world, pos, player, itemStack);

        return ActionResult.SUCCESS;
    }

    public static Tardis getTardis(ItemStack item) {
        NbtCompound nbt = item.getOrCreateNbt();

        if (!nbt.contains("tardis")) return null;

        return ServerTardisManager.getInstance().getTardis(UUID.fromString(nbt.getString("tardis")));
    }

    public static void link(Tardis tardis, ItemStack item) {
        NbtCompound nbt = item.getOrCreateNbt();

        if (tardis == null) return;

        if (!nbt.contains("tardis")) { // fixme dont think you can relink to new tardis
            nbt.putString("tardis", tardis.getUuid().toString());
            nbt.putInt(MODE_KEY, 0);
            nbt.putBoolean(INACTIVE, true);
        }
    }

    public static void playSonicSounds(PlayerEntity player) {
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.PLAYERS, 1f, 2f);
    }

    public static void cycleMode(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!(nbt.contains("tardis")) || !(nbt.contains(MODE_KEY))) return;

        SonicItem.setMode(stack, nbt.getInt(MODE_KEY) + 1 <= Mode.values().length - 1 ? nbt.getInt(MODE_KEY) + 1 : 0);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        NbtCompound nbt = itemStack.getOrCreateNbt();

        if (world.isClient()) return TypedActionResult.pass(itemStack);

        if (user.isSneaking()) {
            cycleMode(itemStack);
        } else {
            playSonicSounds(user);

            // @TODO idk we should make the sonic be usable not just on blocks, especially for scanning about looking for rifts - Loqor

            /*Mode mode = intToMode(nbt.getInt(MODE_KEY));
            mode.run(null, world, user.getBlockPos(), user, itemStack);*/
        }

        return TypedActionResult.pass(itemStack);
    }

    public static int findModeInt(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null || !nbtCompound.contains("tardis"))
            return 0;
        return nbtCompound.getInt(MODE_KEY);
    }

    public static void setMode(ItemStack stack, int mode) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt(MODE_KEY, mode);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Text.literal("Hold shift for more info").formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
            return;
        }

        NbtCompound tag = stack.getOrCreateNbt();
        String text = tag.contains("tardis") ? tag.getString("tardis").substring(0, 8)
                : "None";

        if (tag.contains("tardis")) { // Adding the sonics mode
            tooltip.add(Text.literal("Mode:").formatted(Formatting.BLUE));

            Mode mode = intToMode(tag.getInt(MODE_KEY));
            tooltip.add(Text.literal(mode.asString()).formatted(mode.format).formatted(Formatting.BOLD));

            tooltip.add(ScreenTexts.EMPTY);
        }

        tooltip.add(Text.literal("TARDIS: ").formatted(Formatting.BLUE));
        tooltip.add(Text.literal("> " + text).formatted(Formatting.GRAY));
    }

    public Mode intToMode(int mode) {
        return Mode.values()[mode];
    }
}
