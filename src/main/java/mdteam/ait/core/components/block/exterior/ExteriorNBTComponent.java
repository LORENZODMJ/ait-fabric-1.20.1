package mdteam.ait.core.components.block.exterior;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import mdteam.ait.client.renderers.exteriors.ExteriorEnum;
import mdteam.ait.client.renderers.exteriors.MaterialStateEnum;
import mdteam.ait.core.helper.desktop.DesktopInit;
import mdteam.ait.core.helper.desktop.DesktopSchema;
import mdteam.ait.core.helper.desktop.TARDISDesktop;
import mdteam.ait.core.helper.desktop.impl.WarDesktop;
import mdteam.ait.core.tardis.TardisHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

import static mdteam.ait.AITMod.EXTERIORNBT;
public class ExteriorNBTComponent implements ExteriorDataComponent, AutoSyncedComponent {
    public ExteriorEnum currentExterior;
    public BlockEntity blockEntity;
    public MaterialStateEnum materialState;
    public float leftDoorRotation, rightDoorRotation;

    public ExteriorNBTComponent(BlockEntity blockentity) {
        this.currentExterior = ExteriorEnum.SHELTER;
        this.blockEntity = blockentity;
        this.materialState = MaterialStateEnum.SOLID;
        this.leftDoorRotation = 0;
        this.rightDoorRotation = 0;
    }

    @Override
    public ExteriorEnum getExterior() {
        return this.currentExterior;
    }

    @Override
    public void setExterior(ExteriorEnum exterior) {
        this.currentExterior = exterior;
        EXTERIORNBT.sync(this.blockEntity);
    }

    @Override
    public float getLeftDoorRotation() {
        return this.leftDoorRotation;
    }

    @Override
    public float getRightDoorRotation() {
        return this.rightDoorRotation;
    }

    @Override
    public void setLeftDoorRotation(float newRot) {
        this.leftDoorRotation = newRot;
        EXTERIORNBT.sync(this.blockEntity);
    }

    @Override
    public void setRightDoorRotation(float newRot) {
        this.rightDoorRotation = newRot;
        EXTERIORNBT.sync(this.blockEntity);
    }

    @Override
    public MaterialStateEnum getCurrentMaterialState() {
        return this.materialState;
    }

    @Override
    public void setMaterialState(MaterialStateEnum newMaterialState) {
        this.materialState = newMaterialState;
        EXTERIORNBT.sync(this.blockEntity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if(tag.contains("currentExterior")) this.currentExterior = ExteriorEnum.values()[tag.getInt("currentExterior")]; EXTERIORNBT.sync(this.blockEntity);
        if(tag.contains("materialState")) this.materialState = MaterialStateEnum.values()[tag.getInt("materialState")]; EXTERIORNBT.sync(this.blockEntity);
        if(tag.contains("leftDoorRotation")) this.leftDoorRotation = tag.getFloat("leftDoorRotation"); EXTERIORNBT.sync(this.blockEntity);
        if(tag.contains("rightDoorRotation")) this.rightDoorRotation = tag.getFloat("rightDoorRotation"); EXTERIORNBT.sync(this.blockEntity);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("currentExterior", this.currentExterior.ordinal());
        tag.putInt("materialState", this.materialState.ordinal());
        tag.putFloat("leftDoorRotation", this.leftDoorRotation);
        tag.putFloat("rightDoorRotation", this.rightDoorRotation);
        /*else
            this.setTardisUuid(new UUID(1, 1));*/
    }
}
