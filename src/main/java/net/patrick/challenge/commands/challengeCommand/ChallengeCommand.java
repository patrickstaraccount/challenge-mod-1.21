package net.patrick.challenge.commands.challengeCommand;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.patrick.challenge.commands.timerCommand.PlayerTimerData;

import static net.patrick.challenge.commands.timerCommand.TimerCommand.playerTimers;

public class ChallengeCommand {
    private static boolean active = false;

    public static void register(){
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if(active && source.isOf(DamageTypes.FALL) && entity instanceof ServerPlayerEntity player){
                challengeFeedback.noFallFailed(player, PlayerTimerData.load(player));
                active = false;
                playerTimers.remove(player);
                ((ServerPlayerEntity) entity).changeGameMode(GameMode.SPECTATOR);
                return true;
            }else return true;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("challenge")
                .then(CommandManager.literal("help").executes(context -> {
                    ServerCommandSource source = context.getSource();

                    challengeFeedback.help(source);
                    return 1;
                }))
                .then(CommandManager.literal("noFallDamage")
                    .then(CommandManager.literal("start")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            if(!active){
                                challengeFeedback.noFallStart(source);
                                playerTimers.put(player, 0);
                                player.changeGameMode(GameMode.SURVIVAL);
                                active = true;
                                return 1;
                            }else {
                                challengeFeedback.noFallStartError(source);
                                return 0;
                            }

                        }))
                    .then(CommandManager.literal("end")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            if(!active){
                                challengeFeedback.noFallEndError(source);
                                return 0;
                            }else {
                                challengeFeedback.noFallEnd(source, PlayerTimerData.load(player));
                                playerTimers.remove(player);
                                player.changeGameMode(GameMode.CREATIVE);
                                active = false;
                                return 1;
                            }
                        }))));
        });
    }
}
