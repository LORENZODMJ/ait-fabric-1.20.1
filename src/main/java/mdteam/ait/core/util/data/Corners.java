package mdteam.ait.core.util.data;

import com.google.gson.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.lang.reflect.Type;

public class Corners {
    @Exclude
    private final Box box;
    private final BlockPos first;
    private final BlockPos second;

    public Corners(BlockPos first, BlockPos second) {
        this.box = new Box(first, second);

        this.first = first;
        this.second = second;
    }

    public Box getBox() {
        return box;
    }

    public BlockPos getFirst() {
        return first;
    }

    public BlockPos getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Corners{" +
                "box=" + box +
                ", first=" + first +
                ", second=" + second +
                '}';
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer implements JsonDeserializer<Corners> {

        @Override
        public Corners deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject corners = json.getAsJsonObject();

            return new Corners(
                    context.deserialize(corners.get("first"), BlockPos.class),
                    context.deserialize(corners.get("second"), BlockPos.class)
            );
        }
    }
}
