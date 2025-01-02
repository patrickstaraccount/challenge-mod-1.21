package net.patrick.challenge.commands.timerCommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class TimerCommand {
    public static final Map<ServerPlayerEntity, Integer> playerTimers = new HashMap<>();
    private static int tickCounter = 0;

    public static void register() {

        //timer using ServerTickEvent
        ServerTickEvents.START_SERVER_TICK.register(minecraftServer -> {
            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;
                for (Map.Entry<ServerPlayerEntity, Integer> entry : playerTimers.entrySet()) {
                    ServerPlayerEntity player = entry.getKey();
                    int timerValue = entry.getValue();

                    //increment timer
                    timerValue++;
                    playerTimers.put(player, timerValue);
                    PlayerTimerData.save(player, timerValue);

                    //calculate time based on timerValue
                    int days = timerValue / (24 * 3600);
                    int hours = (timerValue % (24 * 3600)) / 3600;
                    int minutes = (timerValue % 3600) / 60;
                    int seconds = timerValue % 60;

                    //display timer
                    String timeMessage = formatTimeMessage(days, hours, minutes, seconds);
                    player.sendMessage(Text.literal(timeMessage)
                                    .setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)),
                            true);
                }
            }
        });

        //registering the command
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("timer")

                //command to start the timer
                .then(CommandManager.literal("start")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();
                        ServerPlayerEntity player = source.getPlayer();

                        //check if source is player
                        if(!isPlayer(source)) return 0;

                        //update and save timer
                        playerTimers.put(player, 0);
                        PlayerTimerData.save(player, 0);

                        //send timerFeedback to the source
                        timerFeedback.start(source);
                        return 1;
                    }))

                //command to pause the timer at any point
                .then(CommandManager.literal("pause")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();
                        ServerPlayerEntity player = source.getPlayer();

                        //check if source is player
                        if(!isPlayer(source)) return 0;

                        //check if there is a timer to pause
                        if (playerTimers.containsKey(player)) {
                            int currentTimerValue = playerTimers.get(player);

                            //calculate var: days, hours, minutes, seconds
                            int days = currentTimerValue / (24 * 3600);
                            int hours = (currentTimerValue % (24 * 3600)) / 3600;
                            int minutes = (currentTimerValue % 3600) / 60;
                            int seconds = currentTimerValue % 60;

                            //update and save timer
                            PlayerTimerData.save(player, currentTimerValue);
                            playerTimers.remove(player);

                            //send timerFeedback to the source
                            timerFeedback.pause(source, days, hours, minutes, seconds);
                        } else {
                            //send timerFeedback to the source if there is no timer to pause
                            timerFeedback.noPause(source);
                        }
                        return 1;
                    }))

                //command to resume the timer only when paused before
                .then(CommandManager.literal("resume")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();
                        ServerPlayerEntity player = source.getPlayer();

                        //check if source is player
                        if(!isPlayer(source)) return 0;

                        //get timerValue from players storage
                        int persistedTimerValue = PlayerTimerData.load(player);

                        //check if there is active timer in storage of player to resume
                        if (persistedTimerValue > 0) {

                            //update players timer with value from storage
                            playerTimers.put(player, persistedTimerValue);

                            //send timerFeedback to the source
                            timerFeedback.resume(source, persistedTimerValue);

                        } else {
                            //send timerFeedback to the source if there is no active timer to resume
                            timerFeedback.noResume(source);
                        }
                        return 1;
                    }))

                //command to reset the timer back to 0
                .then(CommandManager.literal("reset")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();
                        ServerPlayerEntity player = source.getPlayer();

                        //check if source is player
                        if(!isPlayer(source)) return 0;

                        //update and save timer
                        PlayerTimerData.save(player, 0);
                        playerTimers.remove(player);

                        //send timerFeedback to the source
                        timerFeedback.reset(source);
                        return 1;
                    }))

                //command to show syntax of possible subcommands
                .then(CommandManager.literal("help")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();
                        ServerPlayerEntity player = source.getPlayer();

                        //check if source is player
                        if(!isPlayer(source)) return 0;

                        //send timerFeedback to the source
                        timerFeedback.help(source);
                        return 1;
                    }))

                //command to set the timer to a custom value
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("sec", IntegerArgumentType.integer(0))
                    .then(CommandManager.argument("min", IntegerArgumentType.integer(0))
                    .then(CommandManager.argument("hour", IntegerArgumentType.integer(0))
                    .then(CommandManager.argument("day", IntegerArgumentType.integer(0))
                        .executes(commandContext -> {
                            ServerCommandSource source = commandContext.getSource();
                            ServerPlayerEntity player = source.getPlayer();

                            //check if source is player
                            if(!isPlayer(source)) return 0;


                            //get player input
                            int seconds = IntegerArgumentType.getInteger(commandContext, "sec");
                            int minutes = IntegerArgumentType.getInteger(commandContext, "min");
                            int hours = IntegerArgumentType.getInteger(commandContext, "hour");
                            int days = IntegerArgumentType.getInteger(commandContext, "day");

                            //check input
                            if (seconds >= 60 || minutes >= 60 || hours >= 24) {
                                timerFeedback.invalidInput(source);
                                return 0;
                            }
                            if (days > 1000){
                                timerFeedback.invalidDay(source);
                                return 0;
                            }

                            //calculate total seconds
                            int totalSeconds = seconds + (minutes * 60) + (hours * 3600) + (days * 24 * 3600);

                            //update and save the player's timer to the set value
                            playerTimers.put(player, totalSeconds);
                            PlayerTimerData.save(player, totalSeconds);

                            //send timerFeedback with formatted time
                            String timeMessage = formatTimeMessage(days, hours, minutes, seconds);
                            timerFeedback.set(source, timeMessage);
                            return 1;
                        })
                ))))));
        });

    }

    //formats timer message based on time
    public static String formatTimeMessage(int day, int hour, int min, int sec) {
        StringBuilder timeMessage = new StringBuilder();

        if (day > 0) {
            timeMessage.append(day).append("d ");
            timeMessage.append(hour).append("h ");
        }else if (hour > 0) {
            timeMessage.append(hour).append("h ");
        }
        timeMessage.append(min).append("m ");
        timeMessage.append(sec).append("s");
        return timeMessage.toString();
    }

    private static boolean isPlayer(ServerCommandSource source){
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            timerFeedback.isPlayer(source);
            return false;
        }else return true;
    }

}
