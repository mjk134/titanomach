package io.github.mjk134.titanomach.mixin.world;

import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(targets = "net.minecraft.server.world.ServerChunkLoadingManager$EntityTracker")
public interface EntityTrackerAccessor {
    @Accessor("entry")
    EntityTrackerEntry getServerEntity();

    @Accessor("listeners")
    Set<PlayerAssociatedNetworkHandler> getSeenBy();
}