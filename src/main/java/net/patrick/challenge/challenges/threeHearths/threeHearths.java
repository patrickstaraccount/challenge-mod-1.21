package net.patrick.challenge.challenges.threeHearths;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.patrick.challenge.commands.timer.PlayerTimerData;

import static net.patrick.challenge.commands.miscCommands.flyCommand.flyPlayers;
import static net.patrick.challenge.commands.miscCommands.godCommand.godPlayers;
import static net.patrick.challenge.commands.timer.TimerCommand.playerTimers;

public class threeHearths {
    private static boolean active = false;

    public static void register(){
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            MinecraftServer server = entity.getServer();
            if(entity instanceof EnderDragonEntity && active && server != null){
                if (damageSource.getAttacker() instanceof ServerPlayerEntity player){
                    active = false;
                    playerTimers.remove(player);
                    flyPlayers.add(player);
                    player.changeGameMode(GameMode.SPECTATOR);
                    sendCompletionMessage(server, player);
                    PlayerTimerData.save(player, 0);
                    modifyHealth(player, 20);
                }
            }
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) ->  {
            MinecraftServer server = entity.getServer();
            if(active && entity instanceof  ServerPlayerEntity player && server != null){
                active = false;
                sendFailMessage(server);
                flyPlayers.add(player);
                player.setHealth(player.getMaxHealth());
                player.getHungerManager().setFoodLevel(20);
                player.changeGameMode(GameMode.SPECTATOR);
                playerTimers.remove(player);
                PlayerTimerData.save(player, 0);
                modifyHealth(player, 20);
                return false;
            }
            return true;
        });
    }

    public static void startChallenge(ServerPlayerEntity player, double healthAmount, ServerCommandSource source) {
        if(active){
            threeHearthsFeedback.threeHearthsStartError(source);
        }else{
            threeHearthsFeedback.challengeStart(source);
            modifyHealth(player, healthAmount);
            playerTimers.put(player, 0);
            flyPlayers.remove(player);
            godPlayers.remove(player);
            player.changeGameMode(GameMode.SURVIVAL);
            active = true;
        }
    }

    public static void endChallenge(ServerPlayerEntity player, double healthAmount, ServerCommandSource source){
        if(active){
            threeHearthsFeedback.challengeEnd(source, PlayerTimerData.load(player));
            modifyHealth(player, healthAmount);
            playerTimers.remove(player);
            PlayerTimerData.save(player, 0);
            player.changeGameMode(GameMode.CREATIVE);
            flyPlayers.add(player);
            active = false;
        }else{
            threeHearthsFeedback.threeHearthsEndError(source);
        }
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

    private static void sendCompletionMessage(MinecraftServer server, ServerPlayerEntity playerKilledDragon){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            threeHearthsFeedback.completionMessage(player, PlayerTimerData.load(player), playerKilledDragon);
        }
    }

    private static void sendFailMessage(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            threeHearthsFeedback.threeHearthsFailedN(player, PlayerTimerData.load(player));
        }
    }
}

