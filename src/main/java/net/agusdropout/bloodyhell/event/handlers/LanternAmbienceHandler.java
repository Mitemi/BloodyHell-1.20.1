package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.config.ModClientConfigs;
import net.agusdropout.bloodyhell.effect.ModEffects;
import net.agusdropout.bloodyhell.particle.ModParticles;
import net.agusdropout.bloodyhell.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BloodyHell.MODID, value = Dist.CLIENT)
public class LanternAmbienceHandler {

    public static float currentIntensity = 0.0f;
    private static final float TRANSITION_SPEED = 0.015f;
    private static float previousGazeIntensity = 0.0f;
    private static SimpleSoundInstance activeGazeSound = null;

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;


        float targetIntensity = 0.0f;
        if (mc.player.hasEffect(ModEffects.FRENZY.get())) {
            int amplifier = mc.player.getEffect(ModEffects.FRENZY.get()).getAmplifier();
            targetIntensity = Mth.clamp((amplifier + 1) / 100.0f, 0.0f, 1.0f);
        }


        if (currentIntensity < targetIntensity) {
            currentIntensity = Math.min(targetIntensity, currentIntensity + TRANSITION_SPEED);
        } else if (currentIntensity > targetIntensity) {
            currentIntensity = Math.max(targetIntensity, currentIntensity - TRANSITION_SPEED);
        }


        if (currentIntensity > 0.2f && mc.level.random.nextFloat() < (currentIntensity * 0.4f)) {
            double offsetX = (mc.level.random.nextDouble() - 0.5) * 16.0;
            double offsetY = (mc.level.random.nextDouble() - 0.5) * 10.0 + 2.0;
            double offsetZ = (mc.level.random.nextDouble() - 0.5) * 16.0;

            mc.level.addParticle(ModParticles.EYE_PARTICLE.get(),
                    mc.player.getX() + offsetX, mc.player.getY() + offsetY, mc.player.getZ() + offsetZ,
                    0.0, 0.0, 0.0);
        }

        if (currentIntensity > 0.0f && previousGazeIntensity == 0.0f) {
            activeGazeSound = SimpleSoundInstance.forUI(ModSounds.UNKNOWN_LANTERN_GAZE.get(), 1.0F, 1.0F);
            mc.getSoundManager().play(activeGazeSound);
        } else if (currentIntensity == 0.0f && activeGazeSound != null) {
            mc.getSoundManager().stop(activeGazeSound);
            activeGazeSound = null;
        }

        previousGazeIntensity = currentIntensity;
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        if (currentIntensity <= 0 || !ModClientConfigs.ENABLE_FOV_EFFECTS.get()) return;

        double fov = event.getFOV();
        float time = Minecraft.getInstance().player.tickCount + (float) event.getPartialTick();

        float heartbeat = (float) Math.sin(time * 0.2f) * (15.0f * currentIntensity);
        float jitter = (Minecraft.getInstance().player.getRandom().nextFloat() - 0.5f) * (2.0f * currentIntensity);

        event.setFOV(fov - (heartbeat + jitter));
    }
}