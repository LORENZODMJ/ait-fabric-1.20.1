package mdteam.ait.client;

import mdteam.ait.AITMod;
import mdteam.ait.client.renderers.AITRadioRenderer;
import mdteam.ait.client.renderers.consoles.ConsoleRenderer;
import mdteam.ait.client.renderers.doors.DoorRenderer;
import mdteam.ait.client.renderers.entities.ControlEntityRenderer;
import mdteam.ait.client.renderers.exteriors.ExteriorRenderer;
import mdteam.ait.client.screens.MonitorScreen;
import mdteam.ait.core.AITBlockEntityTypes;
import mdteam.ait.core.AITEntityTypes;
import mdteam.ait.core.entities.ConsoleControlEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Environment(value= EnvType.CLIENT)
public class AITModClient implements ClientModInitializer {

	private static KeyBinding keyBinding;

	private final Identifier PORTAL_EFFECT_SHADER = new Identifier(AITMod.MOD_ID, "shaders/core/portal_effect.json");
	public static final Identifier OPEN_SCREEN = new Identifier(AITMod.MOD_ID, "open_screen");
	public static final Identifier OPEN_SCREEN_TARDIS = new Identifier(AITMod.MOD_ID, "open_screen_tardis");

	@Override
	public void onInitializeClient() {
		setupBlockRendering();
		blockEntityRendererRegister();
		entityAttributeRegister();
		entityRenderRegister();

		ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN,
			(client, handler, buf, responseSender) -> {
				int id = buf.readInt();

				Screen screen = screenFromId(id);
				if (screen == null) return;
				MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreenAndRender(screen));
		});
		ClientPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_TARDIS, // fixme this might not be necessary could just be easier to always use the other method.
				(client, handler, buf, responseSender) -> {
					int id = buf.readInt();
					UUID uuid = buf.readUuid();

					Screen screen = screenFromId(id, uuid);
					if (screen == null) return;
					MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreenAndRender(screen));
				});

		//setKeyBinding();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}

	public static void openScreen(ServerPlayerEntity player, int id) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(id);
		ServerPlayNetworking.send(player, OPEN_SCREEN, buf);
	}
	public static void openScreen(ServerPlayerEntity player, int id, UUID tardis) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(id);
		buf.writeUuid(tardis);
		ServerPlayNetworking.send(player, OPEN_SCREEN_TARDIS, buf);
	}

	/**
	 * This is for screens without a tardis
	 */
	public static Screen screenFromId(int id) {
		return screenFromId(id, null);
	}

	public static Screen screenFromId(int id, UUID tardis) {
		return switch(id) {
			default -> null;
			case 0 -> new MonitorScreen(tardis);
		};
	}


	//@TODO Shader stuff, decide whether or not to use this or glScissor stuff. - Loqor
	/*public void shaderStuffForBOTI() {
		CoreShaderRegistrationCallback.EVENT.register(manager -> {
			manager.register(PORTAL_EFFECT_SHADER, VertexFormats.POSITION_TEXTURE, ShaderProgram::attachReferencedShaders);
		});
	}

	public ShaderProgram getShader() throws IOException {
		return new FabricShaderProgram(MinecraftClient.getInstance().getResourceManager(), PORTAL_EFFECT_SHADER, VertexFormats.POSITION_TEXTURE);
	}*/

	public void blockEntityRendererRegister() {
		BlockEntityRendererRegistry.register(AITBlockEntityTypes.AIT_RADIO_BLOCK_ENTITY_TYPE, AITRadioRenderer::new);
		BlockEntityRendererRegistry.register(AITBlockEntityTypes.DISPLAY_CONSOLE_BLOCK_ENTITY_TYPE, ConsoleRenderer::new);
		BlockEntityRendererRegistry.register(AITBlockEntityTypes.EXTERIOR_BLOCK_ENTITY_TYPE, ExteriorRenderer::new);
		BlockEntityRendererRegistry.register(AITBlockEntityTypes.DOOR_BLOCK_ENTITY_TYPE, DoorRenderer::new);
	}

	public void entityRenderRegister() {
		EntityRendererRegistry.register(AITEntityTypes.CONTROL_ENTITY_TYPE, ControlEntityRenderer::new);
	}

	public void entityAttributeRegister() {
		FabricDefaultAttributeRegistry.register(AITEntityTypes.CONTROL_ENTITY_TYPE, ConsoleControlEntity.createControlAttributes());
	}

	public void setKeyBinding() {
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key." + AITMod.MOD_ID + ".open",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				"category." + AITMod.MOD_ID + ".snap"
		));
	}

	public void setupBlockRendering() {
		BlockRenderLayerMap map = BlockRenderLayerMap.INSTANCE;
		//map.putBlock(FMCBlocks.RADIO, RenderLayer.getCutout());
	}
}