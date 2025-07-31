package io.github.mjk134.titanomach.mixin.advancement;

import io.github.mjk134.titanomach.server.tasks.AdvancementTask;
import io.github.mjk134.titanomach.server.tasks.TaskManager;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.mjk134.titanomach.Titanomach.CONFIG;

@Mixin(AdvancementFrame.class)
public class AdvancementFrameMixin {

    @Inject(method = "getChatAnnouncementText", at = @At("HEAD"))
    private void completeAdvancement(AdvancementEntry advancement, ServerPlayerEntity player, CallbackInfoReturnable<MutableText> cir) {
        TaskManager taskManager = CONFIG.getTaskManager();
        if (taskManager.getTaskFromPlayer(player) instanceof AdvancementTask task) {
            task.onAdvancementComplete(advancement, player);
        }
    }
}
