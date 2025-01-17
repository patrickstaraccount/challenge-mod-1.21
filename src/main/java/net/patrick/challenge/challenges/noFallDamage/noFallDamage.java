package net.patrick.challenge.challenges.noFallDamage;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.patrick.challenge.commands.miscCommands.flyCommand;
import net.patrick.challenge.commands.miscCommands.godCommand;
import net.patrick.challenge.commands.timer.PlayerTimerData;

import static net.patrick.challenge.commands.timer.TimerCommand.playerTimers;

//code for the noFallDamage Challenge
public class noFallDamage {
    private static boolean active = false;

    public static void register(){

        //executes after an entity died
        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            MinecraftServer server = livingEntity.getServer();

            //checks if died entity is of EnderDragonEntity and active is true
            if (livingEntity instanceof EnderDragonEntity && active && server != null){
                //checks if EnderDragon was killed by a player
                if(damageSource != null && damageSource.getAttacker() instanceof ServerPlayerEntity player){
                    //set active to false, stop the timer, set player into Spectator and send feedback to the player
                    active = false;
                    playerTimers.remove(player);
                    player.changeGameMode(GameMode.SPECTATOR);
                    sendCompletionMessage(server, player);
                }
            }
        });

        //executes before and entity dies
        ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, v) -> {

            //checks if entity is of ServerPlayerEntity and active is true
            if (livingEntity instanceof ServerPlayerEntity player && active) {
                //check is damageSource is not FallDamage
                if (!damageSource.isOf(DamageTypes.FALL)) {
                    //set active to false, stop the timer, send feedback to the player, set players health and hunger to max and set player into spectator
                    MinecraftServer server = livingEntity.getServer();
                    assert server != null;
                    active = false;
                    playerTimers.remove(player);
                    sendFailMessage(server);
                    player.setHealth(player.getMaxHealth());
                    player.getHungerManager().setFoodLevel(20);
                    player.changeGameMode(GameMode.SPECTATOR);
                    return false;
                }
            }
            //allow death
            return true;
        });

        //executes before an entity takes damage
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {

            //check if active is true and source is fallDamage and entity is of ServerPlayerEntity
            if(active && source.isOf(DamageTypes.FALL) && entity instanceof ServerPlayerEntity player){
                //set active to false, stop the timer, set player into spectator and send feedback to the player
                noFallDamageFeedback.noFallFailed(player, PlayerTimerData.load(player));
                active = false;
                playerTimers.remove(player);
                player.changeGameMode(GameMode.SPECTATOR);
                return false;
            }else
                //allow damage if source is not FallDamage
                return true;
        });


    }
    //start the challenge
    public static void startChallenge(ServerPlayerEntity player, ServerCommandSource source){
        //checks if active is false
        if(!active){
            //set active to true, set player into survival, set fly and god to false, start the timer and send feedback to the player
            noFallDamageFeedback.challengeStart(source);
            playerTimers.put(player, 0);
            flyCommand.flyPlayers.remove(player);
            godCommand.godPlayers.remove(player);
            player.changeGameMode(GameMode.SURVIVAL);
            active = true;
        }else {
            //send feedback if challenge is already started
            noFallDamageFeedback.noFallStartError(source);
        }
    }
    //end the challenge
    public static void endChallenge(ServerPlayerEntity player, ServerCommandSource source){
        //checks if active is false
        if(!active){
            //send feedback if challenge is not started yet
            noFallDamageFeedback.noFallEndError(source);
        }else {
            //set active to false, set player into creative, set fly to true, stop the timer and send feedback to the player
            noFallDamageFeedback.challengeEnd(source, PlayerTimerData.load(player));
            playerTimers.remove(player);
            PlayerTimerData.save(player, 0);
            flyCommand.flyPlayers.add(player);
            player.changeGameMode(GameMode.CREATIVE);
            active = false;
        }
    }

    //send feedback to the player, when EnderDragon is killed
    private static void sendCompletionMessage(MinecraftServer server, ServerPlayerEntity playerKilledDragon){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            noFallDamageFeedback.completionMessage(player, PlayerTimerData.load(player), playerKilledDragon);
        }
    }

    //send Feedback, when player died
    private static void sendFailMessage(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            noFallDamageFeedback.noFallFailedN(player,  PlayerTimerData.load(player));
        }
    }
}
