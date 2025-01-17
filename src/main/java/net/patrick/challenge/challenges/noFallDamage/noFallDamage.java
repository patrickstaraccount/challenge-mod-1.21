package net.patrick.challenge.challenges.noFallDamage;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.patrick.challenge.commands.challengeCommand.challengeFeedback;
import net.patrick.challenge.commands.miscCommands.flyCommand;
import net.patrick.challenge.commands.miscCommands.godCommand;
import net.patrick.challenge.commands.timer.PlayerTimerData;

import static net.patrick.challenge.commands.timer.TimerCommand.playerTimers;

public class noFallDamage {
    private static boolean active = false;

    public static void register(){
        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            MinecraftServer server = livingEntity.getServer();
            if (livingEntity instanceof EnderDragonEntity && active && server != null){
                if(damageSource != null && damageSource.getAttacker() instanceof ServerPlayerEntity){
                    ServerPlayerEntity player = (ServerPlayerEntity) damageSource.getAttacker();
                    active = false;
                    playerTimers.remove(player);
                    player.changeGameMode(GameMode.SPECTATOR);
                    sendCompletionMessage(server, player);
                }
            }
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, v) -> {
            if (livingEntity instanceof ServerPlayerEntity player && active) {
                if (!damageSource.isOf(DamageTypes.FALL)) {
                    MinecraftServer server = livingEntity.getServer();
                    active = false;
                    playerTimers.remove(player);
                    sendFailMessage(server);
                    player.setHealth(player.getMaxHealth());
                    player.getHungerManager().setFoodLevel(20);
                    player.changeGameMode(GameMode.SPECTATOR);
                    return false;
                }
            }
            return true;
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if(active && source.isOf(DamageTypes.FALL) && entity instanceof ServerPlayerEntity player){
                noFallDamageFeedback.noFallFailed(player, PlayerTimerData.load(player));
                active = false;
                playerTimers.remove(player);
                player.changeGameMode(GameMode.SPECTATOR);
                return false;
            }else return true;
        });


    }
    public static void startChallenge(ServerPlayerEntity player, ServerCommandSource source){
        if(!active){
            noFallDamageFeedback.challengeStart(source);
            playerTimers.put(player, 0);
            flyCommand.flyPlayers.remove(player);
            godCommand.godPlayers.remove(player);
            player.changeGameMode(GameMode.SURVIVAL);
            active = true;
        }else {
            noFallDamageFeedback.noFallStartError(source);
        }
    }

    public static void endChallenge(ServerPlayerEntity player, ServerCommandSource source){
        if(!active){
            noFallDamageFeedback.noFallEndError(source);
        }else {
            noFallDamageFeedback.challengeEnd(source, PlayerTimerData.load(player));
            playerTimers.remove(player);
            PlayerTimerData.save(player, 0);
            flyCommand.flyPlayers.add(player);
            player.changeGameMode(GameMode.CREATIVE);
            active = false;
        }
    }

    private static void sendCompletionMessage(MinecraftServer server, ServerPlayerEntity playerKilledDragon){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            noFallDamageFeedback.completionMessage(player, PlayerTimerData.load(player), playerKilledDragon);
        }
    }
    private static void sendFailMessage(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            noFallDamageFeedback.noFallFailedN(player,  PlayerTimerData.load(player));
        }
    }
}
