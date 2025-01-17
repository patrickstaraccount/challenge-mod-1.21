package net.patrick.challenge.commands.challengeCommand;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.patrick.challenge.challenges.noFallDamage.noFallDamage;

public class ChallengeCommand {
    private static boolean noFallChallengeActive = false;

    public static void register(){

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

                            noFallDamage.startChallenge(player, source);
                            setNoFallChallengeActive(true);
                            return 1;
                        }))
                    .then(CommandManager.literal("end")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            noFallDamage.endChallenge(player, source);
                            setNoFallChallengeActive(false);
                            return 1;
                        }))));
        });
    }

    public static boolean isNoFallChallengeActive() {
        return noFallChallengeActive;
    }

    public static void setNoFallChallengeActive(boolean noFallChallengeActive) {
        ChallengeCommand.noFallChallengeActive = noFallChallengeActive;
    }
}
