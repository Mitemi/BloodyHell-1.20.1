package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.client.data.ClientInsightData;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SunParticleSpawnerEvent {

    private static Particle activeSunParticle = null;


    public static final ResourceKey<Level> BLOODY_HELL_DIMENSION =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(BloodyHell.MODID, "soul_dimension"));

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;

            if (level != null && !mc.isPaused() && mc.player != null) {

                boolean hasInsight = ClientInsightData.getPlayerInsight() > 50.0F;
                boolean inRightDimension = level.dimension() == BLOODY_HELL_DIMENSION;

                if (hasInsight && inRightDimension) {

                    if (activeSunParticle == null || !activeSunParticle.isAlive()) {

                        activeSunParticle = mc.particleEngine.createParticle(
                                ModParticles.FRENZIED_SUN.get(),
                                mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                                0.0D, 0.0D, 0.0D
                        );
                    }
                } else {

                    if (activeSunParticle != null) {
                        activeSunParticle.remove();
                        activeSunParticle = null;
                    }
                }
            } else {
                if ((activeSunParticle != null)) {
                    activeSunParticle.remove();
                    activeSunParticle = null;
                }
            }
        }
    }
}