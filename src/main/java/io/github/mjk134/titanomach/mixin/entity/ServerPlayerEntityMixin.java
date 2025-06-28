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
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
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
    public abstract void sendAbilitiesUpdate();

    @Shadow
    public abstract boolean isDisconnected();

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

    @Shadow
    @Final
    public ServerPlayerInteractionManager interactionManager;




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

        PlayerManager playerManager = self.getServer().getPlayerManager();
        ServerWorld serverWorld = getWorld();

        playerManager.sendToAll(new PlayerRemoveS2CPacket(new ArrayList<>(Collections.singleton(this.getUuid()))));
        playerManager.sendToAll(PlayerListS2CPacket.entryFromPlayer(Collections.singleton((ServerPlayerEntity) (Object) this)));

        var tracker = ((ServerChunkLoadingManagerAccessor) serverWorld.getChunkManager().chunkLoadingManager)
                .getEntityTrackers()
                .get(this.getId());

        tracker.getSeenBy().forEach(tracking -> {
            tracker.getServerEntity().startTracking(tracking.getPlayer());
        });

        ServerWorld level = this.getWorld();

        this.networkHandler.sendPacket(new PlayerRespawnS2CPacket(
                new CommonPlayerSpawnInfo(
                        level.getDimensionEntry(),
                        level.getRegistryKey(),
                        BiomeAccess.hashSeed(level.getSeed()),
                        this.interactionManager.getGameMode(),
                        this.interactionManager.getPreviousGameMode(),
                        level.isDebugWorld(),
                        level.isFlat(),
                        this.getLastDeathPos(),
                        this.getPortalCooldown(),
                        level.getSeaLevel()
                ), (byte) 3)
        );


        this.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(0, net.minecraft.entity.player.PlayerPosition.fromEntity(this), Collections.emptySet()));
        this.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.getInventory().getSelectedSlot()));
        this.networkHandler.sendPacket(new DifficultyS2CPacket(level.getDifficulty(), level.getLevelProperties().isDifficultyLocked()));
        this.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(this.experienceProgress, this.totalExperience, this.experienceLevel));

        playerManager.sendWorldInfo(self, level);
        playerManager.sendCommandTree(self);

        this.networkHandler.sendPacket(new HealthUpdateS2CPacket(this.getHealth(), this.getHungerManager().getFoodLevel(), this.getHungerManager().getSaturationLevel()));

        for (StatusEffectInstance statusEffect : this.getStatusEffects()) {
            this.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), statusEffect, false));
        }

        var equipmentList = new ArrayList<Pair<EquipmentSlot, ItemStack>>();
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            if (!itemStack.isEmpty()) {
                equipmentList.add(new Pair<>(equipmentSlot, itemStack.copy()));
            }
        }

        if (!equipmentList.isEmpty()) {
            this.networkHandler.sendPacket(new EntityEquipmentUpdateS2CPacket(this.getId(), equipmentList));
        }

        if (!this.getPassengerList().isEmpty()) {
            this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(this));
        }
        if (this.hasVehicle()) {
            assert this.getVehicle() != null;
            this.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(this.getVehicle()));
        }

        this.sendAbilitiesUpdate();
        playerManager.sendPlayerStatus(self);

        ModLogger.info("Skin reload complete for player {}", self.getName().getString());
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
        ModLogger.info("Setting skin for player " + self.getName().getString());
        try {
            ModLogger.info("Clearing existing skin for player");
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

    @Inject(method="writeCustomData", at = @At("TAIL"))
    private void writeCustomDataToNbt(WriteView view, CallbackInfo ci) {
        if (this.titanomach$getSkinValue().isPresent()) {
            view.putString("skinTexture", this.titanomach$getSkinValue().get());
            if (this.titanomach$getSkinSignature().isPresent()) {
                view.putString("skinSignature", this.titanomach$getSkinSignature().get());
            }
        }
    }

    @Inject(method="readCustomData", at = @At("TAIL"))
    private void readCustomDataFromNbt(ReadView view, CallbackInfo ci) {
        String skinTexture = view.getString("skinTexture", null);
        if (skinTexture == null) return;
        String skinSignature = view.getString("skinSignature", null);
        if (skinSignature == null) return;
        this.skinValue = skinTexture;
        this.skinSignature = skinSignature;
        this.titanomach$setSkin(skinTexture, skinSignature, false);
    }

}
