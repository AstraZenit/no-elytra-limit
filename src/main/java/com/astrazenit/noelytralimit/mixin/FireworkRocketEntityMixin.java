package com.astrazenit.noelytralimit.mixin;

import com.astrazenit.noelytralimit.BoostState;
import com.astrazenit.noelytralimit.NoElytraLimit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {

    @Shadow private LivingEntity attachedToEntity;

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("TAIL")
    )
    private void onLaunch(Level level, ItemStack stack, LivingEntity stuckTo, CallbackInfo ci) {
        if (stuckTo instanceof Player player && player.isFallFlying()) {
            BoostState.increaseMax(player.getUUID(), NoElytraLimit.ROCKET_MULTIPLIER, NoElytraLimit.MAX_SPEED);
        }
    }

    @ModifyConstant(method = "tick", constant = @Constant(doubleValue = 1.5))
    private double raiseTarget(double original) {
        if (!(this.attachedToEntity instanceof Player player) || !player.isFallFlying()) {
            return original;
        }

        double currentMax = BoostState.getCurrentMax(player.getUUID());
        double target = currentMax / 20.0;

        // Solo subimos el objetivo si ya es mayor que el vanilla
        return Math.max(original, target);
    }
}