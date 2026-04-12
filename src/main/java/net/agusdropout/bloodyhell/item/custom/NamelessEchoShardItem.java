package net.agusdropout.bloodyhell.item.custom;


import net.agusdropout.bloodyhell.entity.effects.NamelessTrialRiftEntity;
import net.agusdropout.bloodyhell.entity.unknown.custom.EchoOfTheNamelessEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

public class NamelessEchoShardItem extends Item {

    public NamelessEchoShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide() && entityLiving instanceof Player player) {
            ServerLevel serverLevel = (ServerLevel) level;

            EchoOfTheNamelessEntity currentLamp = serverLevel.getEntitiesOfClass(EchoOfTheNamelessEntity.class,
                    player.getBoundingBox().inflate(EchoOfTheNamelessEntity.REPEALING_LAMP_RADIUS),
                    lamp -> player.getUUID().equals(lamp.getOwnerUUID()) && lamp.getEntityState() == EchoOfTheNamelessEntity.STATE_IDLE && lamp.getEnergy() > 0.0F
            ).stream().min(Comparator.comparingDouble(player::distanceTo)).orElse(null);

            if (currentLamp != null) {
                currentLamp.triggerResonance(0);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            } else {
                Entity target = serverLevel.getEntitiesOfClass(EchoOfTheNamelessEntity.class,
                        player.getBoundingBox().inflate(64.0D),
                        lamp -> player.getUUID().equals(lamp.getOwnerUUID()) && lamp.getEntityState() != EchoOfTheNamelessEntity.STATE_BURROWING
                ).stream().min(Comparator.comparingDouble(player::distanceTo)).orElse(null);

                if (target == null) {
                    target = serverLevel.getEntitiesOfClass(NamelessTrialRiftEntity.class,
                            player.getBoundingBox().inflate(64.0D),
                            rift -> player.getUUID().equals(rift.getTargetPlayer())
                    ).stream().min(Comparator.comparingDouble(player::distanceTo)).orElse(null);
                }

                if (target != null) {
                    double dist = player.distanceTo(target);
                    float pitch = (float) (2.0F - (Math.min(dist, 32.0D) / 32.0F));

                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0F, pitch);

                    sendGuidanceBurst(serverLevel, player.getEyePosition(), target.position().add(0, 0.5, 0));

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                } else {
                    player.displayClientMessage(Component.literal("The whispers fade into silence...").withStyle(ChatFormatting.DARK_GRAY), true);
                }
            }
        }

        return stack;
    }

    private void sendGuidanceBurst(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 direction = end.subtract(start).normalize();
        double distance = start.distanceTo(end);

        for (double d = 0; d < Math.min(distance, 15.0); d += 0.5) {
            Vec3 point = start.add(direction.scale(d));
            level.sendParticles(ParticleTypes.FIREWORK,
                    point.x, point.y, point.z,
                    1, 0, 0, 0, 0.01);
        }
    }
}