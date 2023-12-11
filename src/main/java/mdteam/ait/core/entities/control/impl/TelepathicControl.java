package mdteam.ait.core.entities.control.impl;

import mdteam.ait.core.entities.control.Control;
import mdteam.ait.tardis.Tardis;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;

public class TelepathicControl extends Control {
    public TelepathicControl() {
        super("telepathic circuit");
    }

    @Override
    public boolean runServer(Tardis tardis, ServerPlayerEntity player, ServerWorld world) {
        BlockPos destinationPos = tardis.getTravel().getDestination();
        ServerWorld newWorld = (ServerWorld) tardis.getTravel().getDestination().getWorld();
        RegistryEntry<Biome> biome = newWorld.getBiome(destinationPos);
        String biomeTranslationKey = biome.getKeyOrValue().orThrow().getValue().toShortTranslationKey();
        player.sendMessage(Text.translatable("Destination Biome: " + DimensionControl.capitalizeAndReplaceEach(biomeTranslationKey)), true);
        return true;
    }
}
