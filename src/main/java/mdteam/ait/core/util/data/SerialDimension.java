package mdteam.ait.core.util.data;

import com.google.gson.*;
import mdteam.ait.core.util.TardisUtil;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.lang.reflect.Type;

public class SerialDimension {

    @Exclude
    private final World dimension;
    private final String value;

    @Exclude
    private final String registry;

    public SerialDimension(World dimension) {
        this.dimension = dimension;

        this.value = this.dimension.getRegistryKey().getValue().toString();
        this.registry = this.dimension.getRegistryKey().getRegistry().toString();
    }

    public SerialDimension(Identifier value) {
        this(TardisUtil.findWorld(value));
    }

    public SerialDimension(String value) {
        this(TardisUtil.findWorld(value));
    }

    public String getValue() {
        return value;
    }

    public String getRegistry() {
        return registry;
    }

    public World get() {
        return dimension;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SerialDimension other)
            return this.value.equals(other.getValue());

        return false;
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer implements JsonSerializer<SerialDimension>, JsonDeserializer<SerialDimension> {

        @Override
        public SerialDimension deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new SerialDimension(json.getAsString());
        }

        @Override
        public JsonElement serialize(SerialDimension src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }
}
