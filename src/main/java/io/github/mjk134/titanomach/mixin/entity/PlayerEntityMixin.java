package io.github.mjk134.titanomach.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType,
                                World world) {
        super(entityType, world);
    }

    @WrapOperation(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getName()Lnet/minecraft/text/Text;"))
    public Text setCustomName(PlayerEntity player, Operation<Text> original) {
        if (CONFIG != null) {
            if (CONFIG.isEnabled()) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    String playerId = serverPlayer.getUuid().toString();
                    if (Objects.nonNull(CONFIG.getPlayerConfig(playerId))) return Text.literal(CONFIG.getPlayerConfig(playerId).getRandomIdentity().getPlayerName());
                }
            }
        }
        return original.call(player);
    }

}