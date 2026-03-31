package net.agusdropout.bloodyhell.entity.minions.ai;

import net.agusdropout.bloodyhell.entity.minions.custom.BastionOfTheUnknownEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class RefugeUnderBastionShieldGoal extends Goal {
    private final Mob entity;
    private  BastionOfTheUnknownEntity targetBastion;

     public RefugeUnderBastionShieldGoal(Mob entity) {
        this.entity = entity;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
     }

     public boolean canUse() {
         if(entity instanceof OwnableEntity ownableEntity){
             Optional<BastionOfTheUnknownEntity> selectedBastion = entity.level().getEntitiesOfClass(BastionOfTheUnknownEntity.class, entity.getBoundingBox().inflate(100),
                     bastion -> bastion.getOwner() != null && bastion.getOwner().getUUID().equals(ownableEntity.getOwnerUUID()) && bastion.isSpecialShielding())
                     .stream().findFirst();
             if(selectedBastion.isPresent()){

                 this.targetBastion = selectedBastion.get();
                 if(entity.distanceTo(targetBastion) < 4.0D){
                     return false;
                 } else {
                        return true;
                 }
             }
         }

         return false;
     }


    public void start() {
            if (targetBastion != null) {
                BlockPos bastionPos = targetBastion.blockPosition();
                entity.getNavigation().moveTo(bastionPos.getX(), bastionPos.getY(), bastionPos.getZ(),1.1f);
            }
     }



     public boolean canContinueToUse() {
         return targetBastion != null && targetBastion.isSpecialShielding() && entity.distanceTo(targetBastion) > 4.0D;
     }

}
