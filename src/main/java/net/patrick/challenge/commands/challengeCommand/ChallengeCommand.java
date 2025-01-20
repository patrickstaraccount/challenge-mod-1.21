package net.patrick.challenge.commands.challengeCommand;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.patrick.challenge.challenges.noFallDamage.noFallDamage;
import net.patrick.challenge.challenges.threeHearths.threeHearths;

//adds a challenge command to the game
public class ChallengeCommand {
    private static boolean noFallChallengeActive = false;
    private static boolean threeHearthsActive = false;

    public static void register(){

        //registering the command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("challenge")

                //command to show syntax
                .then(CommandManager.literal("help").executes(context -> {
                    ServerCommandSource source = context.getSource();

                    //send help message to the player
                    challengeFeedback.help(source);
                    return 1;
                }))

                //command to select the challenge to play
                .then(CommandManager.literal("noFallDamage")
                    .then(CommandManager.literal("start")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            //start the challenge and set challengeVariable to true
                            noFallDamage.startChallenge(player, source);
                            setNoFallChallengeActive(true);
                            return 1;
                        }))
                    .then(CommandManager.literal("end")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            //end the challenge and set challengeVariable to false
                            noFallDamage.endChallenge(player, source);
                            setNoFallChallengeActive(false);
                            return 1;
                        })))

                //command to select the challenge to play
                .then(CommandManager.literal("3hearths")
                    .then(CommandManager.literal("start")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            //start the challenge and set challengeVariable to true
                            threeHearths.startChallenge(player, 6);
                            return 1;
                        }))
                    .then(CommandManager.literal("end")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            //end the challenge and set challengeVariable to false
                            threeHearths.endChallenge(player, 20);
                            return 1;
                        }))));
        });
    }

    //setter for challengeVariable
    public static void setNoFallChallengeActive(boolean noFallChallengeActive) {
        ChallengeCommand.noFallChallengeActive = noFallChallengeActive;
    }

    //getter for challengeVariable
    public static boolean isNoFallChallengeActive() {
        return noFallChallengeActive;
    }

    public static boolean isThreeHearthsActive() { return threeHearthsActive; }

    public static void setThreeHearthsActive(boolean threeHearthsActive) {
        ChallengeCommand.threeHearthsActive = threeHearthsActive;
    }
}
