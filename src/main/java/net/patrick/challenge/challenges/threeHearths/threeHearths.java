package net.patrick.challenge.challenges.threeHearths;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static net.patrick.challenge.commands.timer.TimerCommand.playerTimers;

public class threeHearths {
    public static void startChallenge(ServerPlayerEntity player, double healthAmount) {
        modifyHealth(player, 6);
        playerTimers.put(player, 0);
    }

    public static void endChallenge(ServerPlayerEntity player, double healthAmount){
        modifyHealth(player, 20);
        playerTimers.remove(player);
    }

    private static void modifyHealth(ServerPlayerEntity player, double healthAmount){
        AttributeContainer attributes = player.getAttributes();

        // Access the player's max health attribute
        EntityAttributeInstance healthAttribute = attributes.getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            // Remove all modifiers to ensure clean application
            healthAttribute.clearModifiers();

            // Calculate the difference between the default and custom health
            double baseHealth = healthAttribute.getBaseValue();
            double healthDifference = healthAmount - baseHealth;

            // Apply a modifier to adjust the health
            EntityAttributeModifier modifier = new EntityAttributeModifier(
                    Identifier.of("healthmod", "custom_health_modifier"), // Unique Identifier
                    healthDifference,                                                    // Value to add
                    EntityAttributeModifier.Operation.ADD_VALUE                          // Operation
            );

            healthAttribute.addPersistentModifier(modifier);

            // Set the player's current health to match the new max health
            player.setHealth((float) healthAmount);
        }
    }
}

