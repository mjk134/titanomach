package io.github.mjk134.titanomach.server.entity;

import com.mojang.authlib.properties.Property;

import java.util.Optional;

/**
 * Includes additional methods for skin changes.
 * Borrowed from fabric tailor
 */
public interface ServerTitanomachPlayer {
    String PROPERTY_TEXTURES = "textures";
    /**
     * Reloads player's skin.
     */
    void titanomach$reloadSkin();

    /**
     * Sets the skin to the specified player.
     *
     * @param skinData skin texture data
     * @param reload   whether to send packets around for skin reload
     */
    void titanomach$setSkin(Property skinData, boolean reload);

    /**
     * Sets the skin to the specified player.
     *
     * @param value     skin texture value
     * @param signature skin texture signature
     * @param reload    whether to send packets around for skin reload
     */
    void titanomach$setSkin(String value, String signature, boolean reload);

    /**
     * Gets player's skin value.
     *
     * @return skin value as string, null if player has no skin set.
     */
    Optional<String> titanomach$getSkinValue();

    /**
     * Gets player's skin signature.
     *
     * @return skin signature as string, null if player has no skin set.
     */
    Optional<String> titanomach$getSkinSignature();

    /**
     * Gets the most recent time when player changed their skin.
     *
     * @return time of skin change.
     */
    long titanomach$getLastSkinChange();

    /**
     * Resets the skin timer.
     */
    void titanomach$resetLastSkinChange();

    /**
     * Helper function to get texture hash from skin
     * that was set with the mod.
     * <p>
     * Can be used with <a href="https://mc-heads.net/avatar/">https://mc-heads.net/avatar/%7Btextureid%7D</a>
     * to get the head texture.
     * </p>
     *
     * @return player's skin id (hash)
     */
    String titanomach$getSkinId();


    void titanomach$clearSkin();
}