package io.github.mjk134.titanomach.mixin.authlib;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.UUID;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

// Chatgpt told me to do this
@Mixin(value = GameProfile.class, remap = false)
public abstract class GameProfileMixin {

    @Shadow
    public abstract UUID getId();

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true, remap = false)
    private void titanomach$modifyName(CallbackInfoReturnable<String> cir) {
        if (CONFIG == null || !CONFIG.isEnabled()) return;

        UUID id = this.getId();
        if (id == null) return;

        var playerCfg = CONFIG.getPlayerConfig(id.toString());
        if (playerCfg != null) {
            cir.setReturnValue(playerCfg.getRandomIdentity().getPlayerName());
        }
    }
}