package mdteam.ait.client.models.exteriors;

import mdteam.ait.AITMod;
import mdteam.ait.core.blockentities.door.ExteriorBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BoothExteriorModel extends ExteriorModel {

	public static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID, ("textures/blockentities/exteriors/k2_booth.png"));
	public static final Identifier EMISSION = new Identifier(AITMod.MOD_ID, ("textures/blockentities/exteriors/k2_booth_emission.png"));

	private final ModelPart booth;
	public BoothExteriorModel(ModelPart root) {
		this.booth = root.getChild("booth");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData booth = modelPartData.addChild("booth", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData telephone = booth.addChild("telephone", ModelPartBuilder.create().uv(93, 67).cuboid(-6.0F, -35.0F, 7.0F, 12.0F, 22.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 48).cuboid(-2.0F, -21.0F, 5.0F, 4.0F, 8.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 38).cuboid(-4.0F, -24.0F, 5.0F, 3.0F, 3.0F, 2.0F, new Dilation(0.0F))
		.uv(0, 11).cuboid(-6.0F, -26.0F, 6.0F, 7.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r1 = telephone.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -21.0F, 7.0F, 4.0F, 7.0F, 3.0F, new Dilation(0.01F)), ModelTransform.of(0.8012F, 0.0F, -2.1413F, 0.0F, 0.3491F, 0.0F));

		ModelPartData cube_r2 = telephone.addChild("cube_r2", ModelPartBuilder.create().uv(0, 27).cuboid(-2.0F, -21.0F, 6.0F, 4.0F, 7.0F, 3.0F, new Dilation(0.01F)), ModelTransform.of(-1.1432F, 0.0F, -1.2016F, 0.0F, -0.3491F, 0.0F));

		ModelPartData door = booth.addChild("door", ModelPartBuilder.create().uv(62, 69).cuboid(-13.5F, -41.0F, -0.5F, 14.0F, 40.0F, 1.0F, new Dilation(0.0F))
		.uv(14, 37).cuboid(-13.5F, -22.0F, -1.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(6.5F, 0.0F, -9.5F));

		ModelPartData roof = booth.addChild("roof", ModelPartBuilder.create().uv(0, 27).cuboid(-9.5F, -42.0F, -9.5F, 19.0F, 1.0F, 19.0F, new Dilation(0.0F))
		.uv(0, 0).cuboid(-9.0F, -50.0F, -9.0F, 18.0F, 8.0F, 18.0F, new Dilation(0.0F))
		.uv(58, 9).cuboid(-9.0F, -50.0F, -9.0F, 18.0F, 4.0F, 18.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData walls = booth.addChild("walls", ModelPartBuilder.create().uv(9, 111).cuboid(7.0F, -41.0F, -9.0F, 2.0F, 40.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r3 = walls.addChild("cube_r3", ModelPartBuilder.create().uv(0, 69).cuboid(-7.0F, -41.0F, -10.0F, 14.0F, 40.0F, 1.0F, new Dilation(0.0F))
		.uv(93, 92).cuboid(7.0F, -41.0F, -9.0F, 2.0F, 40.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData cube_r4 = walls.addChild("cube_r4", ModelPartBuilder.create().uv(31, 69).cuboid(-7.0F, -41.0F, -10.0F, 14.0F, 40.0F, 1.0F, new Dilation(0.0F))
		.uv(102, 92).cuboid(7.0F, -41.0F, -9.0F, 2.0F, 40.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		ModelPartData cube_r5 = walls.addChild("cube_r5", ModelPartBuilder.create().uv(0, 69).cuboid(-7.0F, -41.0F, -10.0F, 14.0F, 40.0F, 1.0F, new Dilation(0.0F))
		.uv(0, 111).cuboid(7.0F, -41.0F, -9.0F, 2.0F, 40.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		ModelPartData floor = booth.addChild("floor", ModelPartBuilder.create().uv(58, 48).cuboid(-9.0F, -1.01F, -9.0F, 18.0F, 0.0F, 18.0F, new Dilation(0.0F))
		.uv(0, 48).cuboid(-9.5F, -1.0F, -9.5F, 19.0F, 1.0F, 19.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 256, 256);
	}

	@Override
	public void renderWithAnimations(ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
		matrices.push();
		this.booth.getChild("door").yaw = exterior.getDoor().getState().getLeft() == 0 ? 0: -1.575f;
		matrices.scale(0.95f, 0.95f, 0.95f);
		matrices.translate(0, -1.5f, 0);

		super.renderWithAnimations(exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);
		matrices.pop();
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		this.booth.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public Identifier getTexture() {
		return TEXTURE;
	}

	@Override
	public Identifier getEmission() {
		return EMISSION;
	}

	@Override
	public ModelPart getPart() {
		return booth;
	}
}