package io.github.mjk134.titanomach.mixin.advancement;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.mjk134.titanomach.Titanomach.MOD_LOGGER;

@Mixin(AdvancementFrame.class)
public class AdvancementFrameMixin {

    @Inject(method = "getChatAnnouncementText", at = @At("HEAD"))
    private void completeAdvancement(AdvancementEntry advancement, ServerPlayerEntity player, CallbackInfoReturnable<MutableText> cir) {
        // TODO: This can be used to check advancements...
    }
}
