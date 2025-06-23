package io.github.mjk134.titanomach.mixin.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import io.github.mjk134.titanomach.mixin.world.EntityTrackerAccessor;
import io.github.mjk134.titanomach.mixin.world.ServerChunkLoadingManagerAccessor;
import io.github.mjk134.titanomach.server.entity.ServerTitanomachPlayer;
import io.github.mjk134.titanomach.server.network.FakeTextDisplayHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static io.github.mjk134.titanomach.Titanomach.ModLogger;


@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements FakeTextDisplayHolder, ServerTitanomachPlayer {
    @Shadow
    private PlayerInput playerInput;

    @Shadow
    public abstract ServerWorld getWorld();

    @Shadow public ServerPlayNetworkHandler networkHandler;

    @Shadow @NotNull public abstract GameMode getGameMode();

    @Unique
    private int[] fakeTextDisplayIds = new int[0];
    @Unique
    private UUID[] fakeTextDisplayUuids = new UUID[0];

    @Unique
    private static final String STEVE = "MHF_STEVE";

    @Unique
    private final ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

    @Unique
    private final GameProfile gameProfile = this.getGameProfile();

    @Unique
    private final PropertyMap map = this.gameProfile.getProperties();



    @Unique
    private String skinValue;
    @Unique
    private String skinSignature;
    @Unique
    private long lastSkinChangeTime = 0;




    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initFakeArmorStand(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions,
                                   CallbackInfo callbackInfo) {
        fakeTextDisplayIds = new int[]{EntityAccessor.getCurrentId().incrementAndGet(), EntityAccessor.getCurrentId().incrementAndGet()};
        fakeTextDisplayUuids = new UUID[]{UUID.randomUUID(), UUID.randomUUID()};
    }

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    public void getCustomPlayerListName(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(getDisplayName());
    }

    @Inject(method = "setPlayerInput", at = @At("HEAD"))
    public void updateTextDisplays(PlayerInput newInput, CallbackInfo ci) {
        if (fakeTextDisplayIds.length > 0 && newInput.sneak() != playerInput.sneak()) {
            byte flags = (byte) (newInput.sneak() ? 0 : 1 << 1); // See through blocks when not sneaking
            ServerChunkManager manager = getWorld().getChunkManager();
            manager.sendToOtherNearbyPlayers(this, new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[0],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextDisplayFlags(), flags))));
            manager.sendToOtherNearbyPlayers(this, new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[1],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextData(), displayNameText(newInput, true)))));
        }
    }

    @Override
    protected void setFlag(int index, boolean value) {
        super.setFlag(index, value);
        // Invisible flag
        if (fakeTextDisplayIds.length > 0 && index == 5) {
            ServerChunkManager manager = getWorld().getChunkManager();
            manager.sendToOtherNearbyPlayers(this, new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[0],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextData(), displayNameText(playerInput, false)))));
            manager.sendToOtherNearbyPlayers(this, new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[1],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextData(), displayNameText(playerInput, true)))));
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        if (fakeTextDisplayIds.length > 0) {
            ServerPlayNetworkHandler playerNetworker = player.networkHandler;
            playerNetworker.sendPacket(new EntitySpawnS2CPacket(fakeTextDisplayIds[0], fakeTextDisplayUuids[0], getX(), getY(), getZ(),
                    0.0F, 0.0F, EntityType.TEXT_DISPLAY, 0, Vec3d.ZERO, 0.0));
            playerNetworker.sendPacket(new EntitySpawnS2CPacket(fakeTextDisplayIds[1], fakeTextDisplayUuids[1], getX(), getY(), getZ(),
                    0.0F, 0.0F, EntityType.TEXT_DISPLAY, 0, Vec3d.ZERO, 0.0));
            playerNetworker.sendPacket(new EntityPassengersSetS2CPacket(this)); // Text display passenger is added through mixin in network handler to increase compatibility with other mods

            playerNetworker.sendPacket(new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[0],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextData(), displayNameText(playerInput, false)),
                            DataTracker.SerializedEntry.of(DisplayEntityAccessor.getTranslationData(), new Vector3f(0.0F, 0.2F, 0.0F)),
                            DataTracker.SerializedEntry.of(DisplayEntityAccessor.getBillboardData(), (byte) 3), // Centre billboard
                            DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextOpacityData(), (byte) 127),
                            DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextDisplayFlags(), playerInput.sneak() ? (byte) 0 : (byte) (1 << 1))))); // See through blocks when not sneaking

            playerNetworker.sendPacket(new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[1],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextData(), displayNameText(playerInput, true)),
                            DataTracker.SerializedEntry.of(DisplayEntityAccessor.getTranslationData(), new Vector3f(0.0F, 0.2F, 0.0F)),
                            DataTracker.SerializedEntry.of(DisplayEntityAccessor.getBillboardData(), (byte) 3), // Centre billboard
                            DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getBackgroundData(), 0))));
        }
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        if (fakeTextDisplayIds.length > 0) {
            player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(fakeTextDisplayIds));
        }
    }

    @Override
    public void titanomach$updateName() {
        if (fakeTextDisplayIds.length > 0) {
            getWorld().getChunkManager().sendToOtherNearbyPlayers(this, new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[0],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextData(), displayNameText(playerInput, false)))));
            getWorld().getChunkManager().sendToOtherNearbyPlayers(this, new EntityTrackerUpdateS2CPacket(fakeTextDisplayIds[1],
                    List.of(DataTracker.SerializedEntry.of(DisplayEntityAccessor.TextDisplayEntityAccessor.getTextData(), displayNameText(playerInput, true)))));
        }
    }

    @Override
    public void titanomach$reloadSkin() {
        if (self.getServer() == null) {
            ModLogger.error("Tried to reload skin form client side! This should not happen!");
            return;
        }

        ModLogger.info("Reloading skin for player {}", self.getName().getString());

        // Refreshing in tablist for each player
        PlayerManager playerManager = self.getServer().getPlayerManager();

        playerManager.sendToAll(new PlayerRemoveS2CPacket(new ArrayList<>(Collections.singleton(self.getUuid()))));
        playerManager.sendToAll(PlayerListS2CPacket.entryFromPlayer(Collections.singleton(self)));

        ServerWorld serverWorld = getWorld();
        ServerChunkManager manager = serverWorld.getChunkManager();
        ServerChunkLoadingManager storage = manager.chunkLoadingManager;
        EntityTrackerAccessor trackerEntry = ((ServerChunkLoadingManagerAccessor) storage).getEntityTrackers().get(self.getId());



        trackerEntry.getSeenBy().forEach(tracking -> trackerEntry.getServerEntity().startTracking(tracking.getPlayer()));
        ModLogger.info("Trackers for the player: " + trackerEntry.getSeenBy().size());
        trackerEntry.getSeenBy().forEach(tracking ->
                ModLogger.info("Tracker for player: " + tracking.getPlayer().getName().getString())
        );


        // need to change the player entity on the client
        ModLogger.info("Reloading player skin on player's client.");

        this.networkHandler.sendPacket(
                new PlayerRespawnS2CPacket(
                        new CommonPlayerSpawnInfo(
                                serverWorld.getDimensionEntry(),
                                serverWorld.getRegistryKey(),
                                serverWorld.getBiomeAccess().hashCode(),
                                this.getGameMode(),
                                this.getGameMode(),
                                serverWorld.isDebugWorld(),
                                serverWorld.isFlat(),
                                this.getLastDeathPos(),
                                this.getPortalCooldown(),
                                serverWorld.getSeaLevel()
                        ),
                        PlayerRespawnS2CPacket.KEEP_ALL
                )
        );

        this.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0, PlayerPosition.fromEntity(self), Collections.emptySet()));
        this.networkHandler.sendPacket(new SetCursorItemS2CPacket(this.getInventory().getSelectedStack()));
        this.networkHandler.sendPacket(new DifficultyS2CPacket(serverWorld.getDifficulty(), serverWorld.getLevelProperties().isDifficultyLocked()));

        this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
        playerManager.sendWorldInfo(self, serverWorld);
        playerManager.sendCommandTree(self);

        this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.getHungerManager().getFoodLevel(), this.getHungerManager().getSaturationLevel()));

        for (StatusEffectInstance statusEffect : this.getStatusEffects()) {
            this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(self.getId(), statusEffect, false));
        }

        var equipmentList = new ArrayList<Pair<EquipmentSlot, ItemStack>>();
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = self.getEquippedStack(equipmentSlot);
            if (!itemStack.isEmpty()) {
                equipmentList.add(Pair.of(equipmentSlot, itemStack));
            }
        }

        if (!equipmentList.isEmpty()) {
            this.networkHandler.sendPacket(new EntityEquipmentUpdateS2CPacket(self.getId(), equipmentList));
        }

        if (!self.getPassengerList().isEmpty()) {
            this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(self));
        }
        if (self.hasVehicle()) {
            assert self.getVehicle() != null;
            this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(self.getVehicle()));
        }

        self.sendAbilitiesUpdate();
        playerManager.sendPlayerStatus(self);
    }

    @Override
    public int[] titanomach$getFakeTextDisplayIds() {
        return fakeTextDisplayIds;
    }

    @Unique
    private Text displayNameText(PlayerInput input, boolean disappearWhenSneaking) {
        return isInvisible() || (input.sneak() && disappearWhenSneaking) ? Text.empty() : getDisplayName();
    }


    @Override
    public void titanomach$resetLastSkinChange() {
        this.lastSkinChangeTime = 0;
    }

    @Override
    public long titanomach$getLastSkinChange() {
        return this.lastSkinChangeTime;
    }

    @Override
    public Optional<String> titanomach$getSkinSignature() {
        if (this.skinSignature == null) {
            try {
                Property property = map.get("textures").iterator().next();
                this.skinSignature = property.signature();
            } catch (Exception ignored) {
                // Player has no skin data, no worries
            }
        }

        return Optional.ofNullable(this.skinSignature);
    }

    @Override
    public void titanomach$setSkin(Property skinData, boolean reload) {
        ModLogger.debug("Setting skin for player " + self.getName().getString());
        try {
            ModLogger.debug("Clearing existing skin for player");
            this.map.removeAll(ServerTitanomachPlayer.PROPERTY_TEXTURES);
        } catch (Exception ignored) {
            // Player has no skin data, no worries
        }

        try {
            this.map.put(ServerTitanomachPlayer.PROPERTY_TEXTURES, skinData);

            // Saving skin data
            this.skinValue = skinData.value();
            this.skinSignature = skinData.signature();

            // Reloading skin
            if (reload) {
                this.titanomach$reloadSkin();
            }

            this.lastSkinChangeTime = System.currentTimeMillis();
        } catch (Error e) {
            // Something went wrong when trying to set the skin
            ModLogger.error(e.getMessage());
        }
    }

    @Override
    public void titanomach$setSkin(String value, String signature, boolean reload) {
        this.titanomach$setSkin(new Property("textures", value, signature), reload);
    }

    @Override
    public Optional<String> titanomach$getSkinValue() {
        if (this.skinValue == null) {
            try {
                Property property = map.get("textures").iterator().next();
                this.skinValue = property.value();
            } catch (Exception ignored) {
                // Player has no skin data, no worries
            }
        }

        return Optional.ofNullable(this.skinValue);
    }

    @Override
    public void titanomach$clearSkin() {
        try {
            this.map.removeAll(ServerTitanomachPlayer.PROPERTY_TEXTURES);
            // Ensure that the skin is completely cleared to prevent the save bug.
            this.skinValue = null;
            this.skinSignature = null;
            this.titanomach$reloadSkin();
        } catch (Exception ignored) {
            // Player has no skin data, no worries
        }
    }


//    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
//    private void writeCustomDataToNbt(CompoundTag tag, CallbackInfo ci) {
//        if (this.fabrictailor_getSkinValue().isPresent()) {
//            CompoundTag skinDataTag = new CompoundTag();
//            skinDataTag.putString("value", this.fabrictailor_getSkinValue().get());
//            if (this.fabrictailor_getSkinSignature().isPresent()) {
//                skinDataTag.putString("signature", this.fabrictailor_getSkinSignature().get());
//            }
//
//            tag.put("fabrictailor:skin_data", skinDataTag);
//        }
//    }
//
//    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
//    private void readCustomDataFromNbt(CompoundTag tag, CallbackInfo ci) {
//        Optional<CompoundTag> skinDataTagOP = tag.getCompound("fabrictailor:skin_data");
//        if(skinDataTagOP.isEmpty()) return; // This causes an java.util.NoSuchElementException for new players I believe.
//        CompoundTag skinDataTag = skinDataTagOP.get();
//        Optional<String> skinValueOP = skinDataTag.getString("value");
//        Optional<String> skinSignatureOP = skinDataTag.getString("signature");
//        // https://fabricmc.net/2025/03/24/1215.html#nbt
//        if (skinValueOP.isEmpty() || skinSignatureOP.isEmpty()) {
//            return;
//        }
//
//        this.skinValue = skinValueOP.get();
//        this.skinSignature = skinSignatureOP.get();
//        this.fabrictailor_setSkin(this.skinValue, this.skinSignature, false);
//    }

}
