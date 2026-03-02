package net.agusdropout.bloodyhell.event.handlers;

import net.agusdropout.bloodyhell.BloodyHell;
import net.agusdropout.bloodyhell.capability.crimsonveilPower.PlayerCrimsonveilProvider;

import net.agusdropout.bloodyhell.capability.insight.PlayerInsightProvider;
import net.agusdropout.bloodyhell.networking.ModMessages;
import net.agusdropout.bloodyhell.networking.packet.CrimsonVeilDataSyncS2CPacket;
import net.agusdropout.bloodyhell.util.capability.CrimsonVeilHelper;
import net.agusdropout.bloodyhell.util.capability.InsightHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerCapabilityHandler {

    public static void handlePlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();

            event.getOriginal().getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });

            event.getOriginal().getCapability(PlayerInsightProvider.PLAYER_INSIGHT).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerInsightProvider.PLAYER_INSIGHT).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });

            event.getOriginal().invalidateCaps();
        }
    }

    public static void handlePlayerJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            CrimsonVeilHelper.sync(player);
            InsightHelper.syncInsight(player);
        }
    }

    public static void handleAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerCrimsonveilProvider.PLAYER_CRIMSONVEIL).isPresent()) {
                event.addCapability(new ResourceLocation(BloodyHell.MODID, "crimsonveil"), new PlayerCrimsonveilProvider());
            }
            if (!event.getObject().getCapability(PlayerInsightProvider.PLAYER_INSIGHT).isPresent()) {
                event.addCapability(new ResourceLocation(BloodyHell.MODID, "insight"), new PlayerInsightProvider());
            }
        }
    }
}