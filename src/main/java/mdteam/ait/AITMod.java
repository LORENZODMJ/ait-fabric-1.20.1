package mdteam.ait;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import mdteam.ait.core.*;
import mdteam.ait.core.components.block.radio.RadioNBTComponent;
import mdteam.ait.core.util.TardisUtil;
import mdteam.ait.tardis.manager.TardisManager;
import mdteam.ait.tardis.wrapper.client.manager.ClientTardisManager;
import mdteam.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AITMod implements ModInitializer {

	public static final ComponentKey<RadioNBTComponent> RADIONBT =
			ComponentRegistry.getOrCreate(new Identifier(AITMod.MOD_ID, "radionbt"), RadioNBTComponent.class);

	public static final OwoItemGroup AIT_ITEM_GROUP = OwoItemGroup.builder(new Identifier(AITMod.MOD_ID, "item_group"),
			() -> Icon.of(AITItems.TARDIS_ITEM.getDefaultStack())).build();

	public static final String MOD_ID = "ait";

    public static final Logger LOGGER = LoggerFactory.getLogger("ait");

	@Override
	public void onInitialize() {
		AITDesktops.init();
		FieldRegistrationHandler.register(AITItems.class, MOD_ID, false);
		FieldRegistrationHandler.register(AITBlocks.class, MOD_ID, false);
		FieldRegistrationHandler.register(AITSounds.class, MOD_ID, false);
		FieldRegistrationHandler.register(AITBlockEntityTypes.class, MOD_ID, false);
		FieldRegistrationHandler.register(AITEntityTypes.class, MOD_ID, false);
		AIT_ITEM_GROUP.initialize();
		AITEvents.init();
		AITDesktops.init();
		TardisUtil.init();
		TardisManager.init();
	}
}