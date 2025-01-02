package net.patrick.timer.command.timerCommand;

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
    private static final Map<ServerPlayerEntity, Integer> playerTimers = new HashMap<>();
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
                        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                            source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                            return 0;
                        }

                        playerTimers.put(player, 0);
                        PlayerTimerData.save(player, 0);
                        source.sendFeedback(() -> Text.literal("[Timer] Timer started at 0"), false);
                        return 1;
                    }))

                //command to pause the timer at any point
                .then(CommandManager.literal("pause")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();

                        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                            source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                            return 0;
                        }

                        if (playerTimers.containsKey(player)) {
                            int currentTimerValue = playerTimers.get(player);

                            int days = currentTimerValue / (24 * 3600);
                            int hours = (currentTimerValue % (24 * 3600)) / 3600;
                            int minutes = (currentTimerValue % 3600) / 60;
                            int seconds = currentTimerValue % 60;

                            PlayerTimerData.save(player, currentTimerValue);
                            playerTimers.remove(player);
                            source.sendFeedback(() -> Text.literal("[Timer] Timer paused at: " + formatTimeMessage(days, hours, minutes, seconds)), false);
                        } else {
                            source.sendFeedback(() -> Text.literal("[Timer] No active timer to pause!"), false);
                        }
                        return 1;
                    }))

                //command to resume the timer only when paused before
                .then(CommandManager.literal("resume")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();

                        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                            source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                            return 0;
                        }

                        int persistedTimerValue = PlayerTimerData.load(player);
                        if (persistedTimerValue > 0) {
                            playerTimers.put(player, persistedTimerValue);
                            source.sendFeedback(() -> Text.literal("[Timer] Timer resumed at: " + formatTimeMessage(
                                    persistedTimerValue / (24 * 3600),
                                            (persistedTimerValue % (24 * 3600)) / 3600,
                                            (persistedTimerValue % 3600) / 60,
                                            persistedTimerValue % 60)),
                                    false);
                        } else {
                            source.sendFeedback(() -> Text.literal("[Timer] No timer to resume!"), false);
                        }
                        return 1;
                    }))

                //command to reset the timer back to 0
                .then(CommandManager.literal("reset")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();

                        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                            source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                            return 0;
                        }

                        PlayerTimerData.save(player, 0);
                        playerTimers.remove(player);
                        source.sendFeedback(() -> Text.literal("[Timer] Timer reset to 0"), false);
                        return 1;
                    }))

                //command to
                .then(CommandManager.literal("help")
                    .executes(commandContext -> {
                        ServerCommandSource source = commandContext.getSource();
                        source.sendFeedback(() -> Text.literal("[Timer] /timer start to start the Timer"), false);
                        source.sendFeedback(() -> Text.literal("[Timer] /timer pause to pause the Timer"), false);
                        source.sendFeedback(() -> Text.literal("[Timer] /timer resume to resume the Timer at the last paused stage"), false);
                        source.sendFeedback(() -> Text.literal("[Timer] /timer reset to reset the Timer back to 0"), false);
                        source.sendFeedback(() -> Text.literal("[Timer] /timer set <value> to set the Timer to a specific value"), false);
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

                            if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                                source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                                return 0;
                            }

                            //get player input
                            int seconds = IntegerArgumentType.getInteger(commandContext, "sec");
                            int minutes = IntegerArgumentType.getInteger(commandContext, "min");
                            int hours = IntegerArgumentType.getInteger(commandContext, "hour");
                            int days = IntegerArgumentType.getInteger(commandContext, "day");

                            //check input
                            if (seconds >= 60 || minutes >= 60 || hours >= 24) {
                                source.sendFeedback(() -> Text.literal("[Timer] Invalid time values! Seconds must be < 60, minutes must be < 60, and hours must be < 24."), false);
                                return 0;
                            }

                            //calculate total seconds
                            int totalSeconds = seconds + (minutes * 60) + (hours * 3600) + (days * 24 * 3600);

                            //update and save the player's timer to the set value
                            playerTimers.put(player, totalSeconds);
                            PlayerTimerData.save(player, totalSeconds);

                            //send feedback with formatted time
                            String timeMessage = formatTimeMessage(days, hours, minutes, seconds);
                            source.sendFeedback(() -> Text.literal("[Timer] Timer set to: " + timeMessage), false);
                            return 1;
                        })
                ))))));
        });

    }

    //formats timer message based on time
    private static String formatTimeMessage(int day, int hour, int min, int sec) {
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

}
