package net.agusdropout.bloodyhell.entity.ai.goals;

import net.agusdropout.bloodyhell.entity.custom.UnknownLanternEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

public class LanternPersecutionGoal extends Goal {

    private final UnknownLanternEntity lantern;
    private Player targetPlayer;
    private int gazeTicks = 0;

    private static final double MAX_VISIBLE_DISTANCE = 15.0D;

    public LanternPersecutionGoal(UnknownLanternEntity lantern) {
        this.lantern = lantern;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.lantern.isSummoning()) return false;

        UUID targetId = this.lantern.getTargetPlayer();
        if (targetId == null) return false;

        Player player = this.lantern.level().getPlayerByUUID(targetId);

        if (player == null || !player.isAlive() || player.isSpectator()) {
            if (!this.lantern.level().isClientSide()) {
                this.lantern.fail();
            }
            return false;
        }

        this.targetPlayer = player;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.gazeTicks = 0;
        this.lantern.setTarget(this.targetPlayer);
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive()) return;

        this.lantern.getLookControl().setLookAt(this.targetPlayer, 30.0F, 30.0F);

        double distanceToPlayerSqr = this.lantern.distanceToSqr(this.targetPlayer);

        if (distanceToPlayerSqr <= 3.0D) {
            if (!this.lantern.level().isClientSide()) {
                this.lantern.fail();
            }
            return;
        }

        this.lantern.getNavigation().moveTo(this.targetPlayer, 1.07D);

        boolean isPlayerLooking = this.isPlayerLookingAtMe(this.targetPlayer, this.lantern);


        if (isPlayerLooking) {
            this.gazeTicks++;
            this.lantern.setGazeIntensity(Math.min(1.0f, (float)this.gazeTicks / 30.0f));

            if (this.gazeTicks >= 30) {
                this.targetPlayer.hurt(this.lantern.damageSources().magic(), 4.0F);
                this.lantern.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.0F, 0.5F);
                this.gazeTicks = 0;
            }
        } else {
            this.gazeTicks = Math.max(0, this.gazeTicks - 1);
            this.lantern.setGazeIntensity((float)this.gazeTicks / 30.0f);
        }
    }

    @Override
    public void stop() {
        this.lantern.setGazing(false);
        this.targetPlayer = null;
        this.gazeTicks = 0;
        this.lantern.getNavigation().stop();
    }

    private boolean isPlayerLookingAtMe(Player player, UnknownLanternEntity lantern) {
        Vec3 playerView = player.getViewVector(1.0F).normalize();

        Vec3 toLantern = new Vec3(
                lantern.getX() - player.getX(),
                lantern.getEyeY() - player.getEyeY(),
                lantern.getZ() - player.getZ()
        );

        double distance = toLantern.length();


        if (distance > MAX_VISIBLE_DISTANCE) {
            return false;
        }

        toLantern = toLantern.normalize();

        double dotProduct = playerView.dot(toLantern);


        if (dotProduct > 1.0D - 0.025D) {
            return player.hasLineOfSight(lantern);
        }

        return false;
    }
}