package mdteam.ait.client.renderers.exteriors;

import mdteam.ait.client.animation.ClassicAnimation;
import mdteam.ait.client.animation.ExteriorAnimation;
import mdteam.ait.client.animation.PulsatingAnimation;
import mdteam.ait.client.models.doors.*;
import mdteam.ait.client.models.exteriors.*;
import mdteam.ait.core.AITSounds;
import mdteam.ait.core.blockentities.door.ExteriorBlockEntity;
import mdteam.ait.core.sounds.MatSound;
import mdteam.ait.tardis.TardisTravel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum ExteriorEnum {

    CAPSULE() {
        @Override
        public ExteriorAnimation createAnimation(ExteriorBlockEntity entity) {
            return new ClassicAnimation(entity);
        }

        @Override
        public boolean isDoubleDoor() {
            return true;
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public ExteriorModel createModel() {
            return new CapsuleExteriorModel(CapsuleExteriorModel.getTexturedModelData().createModel());
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public DoorModel createDoorModel() {
            return new CapsuleDoorModel(CapsuleDoorModel.getTexturedModelData().createModel());
        }

        @Override
        public Class<? extends ExteriorModel> getModelClass() {
            return CapsuleExteriorModel.class;
        }

        @Override
        public Class<? extends DoorModel> getDoorClass() {
            return CapsuleDoorModel.class;
        }
    },
    SHELTER() {
        @Override
        public MatSound getSound(TardisTravel.State state) {
            return switch(state) {
                case LANDED, FLIGHT -> AITSounds.LANDED_ANIM;
                case DEMAT -> AITSounds.EIGHT_DEMAT_ANIM;
                case MAT -> AITSounds.EIGHT_MAT_ANIM;
            };
        }
        @Override
        public ExteriorAnimation createAnimation(ExteriorBlockEntity entity) {
            return new ClassicAnimation(entity);
        }

        @Override
        public boolean isDoubleDoor() {
            return false;
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public ExteriorModel createModel() {
            return new FalloutExteriorModel(FalloutExteriorModel.getTexturedModelData().createModel());
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public DoorModel createDoorModel() {
            return new FalloutDoorModel(FalloutDoorModel.getTexturedModelData().createModel());
        }

        @Override
        public Class<? extends ExteriorModel> getModelClass() {
            return FalloutExteriorModel.class;
        }

        @Override
        public Class<? extends DoorModel> getDoorClass() {
            return FalloutDoorModel.class;
        }
    },
    TOYOTA() {
        @Override
        public ExteriorAnimation createAnimation(ExteriorBlockEntity entity) {
            return new PulsatingAnimation(entity);
        }

        @Override
        public boolean isDoubleDoor() {
            return true;
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public ExteriorModel createModel() {
            return new ToyotaExteriorModel(ToyotaExteriorModel.getTexturedModelData().createModel());
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public DoorModel createDoorModel() {
            return new ToyotaDoorModel(ToyotaDoorModel.getTexturedModelData().createModel());
        }

        @Override
        public Class<? extends ExteriorModel> getModelClass() {
            return ToyotaExteriorModel.class;
        }

        @Override
        public Class<? extends DoorModel> getDoorClass() {
            return ToyotaDoorModel.class;
        }
    },
    TARDIM() {
        @Override
        public ExteriorAnimation createAnimation(ExteriorBlockEntity entity) {
            return new PulsatingAnimation(entity);
        }

        @Override
        public boolean isDoubleDoor() {
            return true;
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public ExteriorModel createModel() {
            return new TardimExteriorModel(TardimExteriorModel.getTexturedModelData().createModel());
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public DoorModel createDoorModel() {
            return TOYOTA.createDoorModel();
        }

        @Override
        public Class<? extends ExteriorModel> getModelClass() {
            return TardimExteriorModel.class;
        }

        @Override
        public Class<? extends DoorModel> getDoorClass() {
            return TOYOTA.getDoorClass();
        }
    },
    BOOTH {
        @Override
        public ExteriorAnimation createAnimation(ExteriorBlockEntity entity) {
            return new PulsatingAnimation(entity);
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public ExteriorModel createModel() {
            return new BoothExteriorModel(BoothExteriorModel.getTexturedModelData().createModel());
        }

        @Override
        @Environment(value= EnvType.CLIENT)
        public DoorModel createDoorModel() {
            return new BoothDoorModel(BoothDoorModel.getTexturedModelData().createModel());
        }

        @Override
        public boolean isDoubleDoor() {
            return false;
        }

        @Override
        public Class<? extends ExteriorModel> getModelClass() {
            return BoothExteriorModel.class;
        }

        @Override
        public Class<? extends DoorModel> getDoorClass() {
            return BoothDoorModel.class;
        }
    };

    public abstract ExteriorAnimation createAnimation(ExteriorBlockEntity entity);
    @Environment(value= EnvType.CLIENT)
    public abstract ExteriorModel createModel();
    @Environment(value= EnvType.CLIENT)
    public abstract DoorModel createDoorModel();
    public abstract boolean isDoubleDoor();
    public abstract Class<? extends ExteriorModel> getModelClass();
    public abstract Class<? extends DoorModel> getDoorClass();

    public MatSound getSound(TardisTravel.State state) {
        return switch(state) {
            case LANDED, FLIGHT -> AITSounds.LANDED_ANIM;
            case DEMAT -> AITSounds.DEMAT_ANIM;
            case MAT -> AITSounds.MAT_ANIM;
        };
    }
}
