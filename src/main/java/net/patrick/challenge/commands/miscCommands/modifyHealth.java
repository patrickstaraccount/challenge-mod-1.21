package net.patrick.challenge.commands.miscCommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class modifyHealth {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("modHealth")
                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        ServerCommandSource source = context.getSource();
                        ServerPlayerEntity player = source.getPlayer();

                        int healthAmount = IntegerArgumentType.getInteger(context, "amount");
                        if (player != null) {
                            modHealth(player, healthAmount);
                        }
                        return 1;
                    })));
        });
    }

    private static void modHealth(ServerPlayerEntity player, double healthAmount){
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
