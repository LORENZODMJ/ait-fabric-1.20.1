package mdteam.ait.core.item;

import mdteam.ait.core.blockentities.console.ConsoleBlockEntity;
import mdteam.ait.core.blockentities.door.DoorBlockEntity;
import mdteam.ait.core.blockentities.door.ExteriorBlockEntity;
import mdteam.ait.core.blocks.ConsoleBlock;
import mdteam.ait.core.util.TardisUtil;
import mdteam.ait.core.util.data.AbsoluteBlockPos;
import mdteam.ait.tardis.Tardis;
import mdteam.ait.tardis.TardisTravel;
import mdteam.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static mdteam.ait.tardis.TardisTravel.State.FLIGHT;
import static mdteam.ait.tardis.TardisTravel.State.LANDED;

public class RemoteItem extends Item {

    public RemoteItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack itemStack = context.getStack();

        if (world.isClient() || player == null)
            return ActionResult.PASS;

        NbtCompound nbt = itemStack.getOrCreateNbt();

        // Link to exteriors tardis if it exists and player is crouching
        if (player.isSneaking()) {
            if (world.getBlockEntity(pos) instanceof ConsoleBlockEntity consoleBlockEntity) {
                if (consoleBlockEntity.getTardis() == null)
                    return ActionResult.FAIL;
                nbt.putUuid("tardis", consoleBlockEntity.getTardis().getUuid());
                return ActionResult.SUCCESS;
            }
        }

        // Move tardis to the clicked pos
        if (!nbt.contains("tardis"))
            return ActionResult.FAIL;

        Tardis tardis = ServerTardisManager.getInstance().getTardis(nbt.getUuid("tardis"));
        //System.out.println(ServerTardisManager.getInstance().getTardis(nbt.getUuid("tardis")));

        if (tardis != null) {
            tardis.getDoor().setLocked(tardis.getDoor().isLocked());
            if(world != TardisUtil.getTardisDimension()) {
                world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS);

                TardisTravel travel = tardis.getTravel();

                travel.setDestination(new AbsoluteBlockPos.Directed(pos.up(), world, player.getMovementDirection().getOpposite()), true);
                // travel.toggleHandbrake();

                //FIXME: this is not how you do it!
                if (travel.getState() == LANDED)
                    travel.dematerialise(true);
                if (travel.getState() == FLIGHT)
                    travel.materialise();

                return ActionResult.SUCCESS;
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.BLOCKS, 1F, 0.2F);
                player.sendMessage(Text.literal("Cannot translocate exterior to interior dimension!"), true);
                return ActionResult.PASS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Text.of("Hold shift for more info"));
            return;
        }

        NbtCompound tag = stack.getOrCreateNbt();
        String text = tag.contains("tardis") ? tag.getUuid("tardis").toString().substring(0, 8)
                : "Remote does not identify with any TARDIS";

        tooltip.add(Text.literal("→ " + text).formatted(Formatting.DARK_AQUA));
    }
}
