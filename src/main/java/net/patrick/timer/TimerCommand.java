package net.patrick.timer;

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
    private static int tickcounter = 0;
    private static int min = 0;
    private static int hour = 0;
    private static int day = 0;
    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(minecraftServer -> {
            tickcounter++;
            if(tickcounter >= 20){
                tickcounter = 0;
                for (Map.Entry<ServerPlayerEntity, Integer> entry : playerTimers.entrySet()){
                    ServerPlayerEntity player = entry.getKey();
                    int timerValue = entry.getValue();
                    int sec = timerValue;
                    if (timerValue < 60) {
                        String timeMessage = formatTimeMessage(day, hour, min, timerValue);
                        player.sendMessage(Text.literal(timeMessage)
                            .setStyle(Style.EMPTY
                                .withColor(Formatting.GOLD)
                                .withBold(true)),
                                true);
                    } else {
                        timerValue = 0;
                        min++;
                        if (min >= 60) {
                            min = 0;
                            hour++;
                            if (hour >= 24) {
                                hour = 0;
                                day++;
                            }
                        }
                        String timeMessage = formatTimeMessage(day, hour, min, timerValue);
                        player.sendMessage(Text.literal(timeMessage)
                            .setStyle(Style.EMPTY
                                .withColor(Formatting.GOLD)
                                .withBold(true)),
                                true);
                    }
                    playerTimers.put(player, timerValue + 1);
                    PlayerTimerData.save(player, timerValue + 1);
                }
            }
        });

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("timer")
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
                    .then(CommandManager.literal("pause")
                        .executes(commandContext -> {
                            ServerCommandSource source = commandContext.getSource();

                            if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                                source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                                return 0;
                            }

                            if (playerTimers.containsKey(player)) {
                                int currentTimerValue = playerTimers.get(player);
                                PlayerTimerData.save(player, currentTimerValue);
                                playerTimers.remove(player);
                                source.sendFeedback(() -> Text.literal("[Timer] Timer paused at: " + min + "m " + currentTimerValue + "s"), false);
                            } else {
                                source.sendFeedback(() -> Text.literal("[Timer] No active timer to pause!"), false);
                            }
                            return 1;
                        }))
                    .then(CommandManager.literal("resume")
                        .executes(commandContext -> {
                            ServerCommandSource source = commandContext.getSource();

                            if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                                source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                                return 0;
                            }

                            int persistedTimerValue = PlayerTimerData.load(player);
                            playerTimers.put(player, persistedTimerValue);
                            source.sendFeedback(() -> Text.literal("[Timer] Timer resumed at: " + min + "m " + persistedTimerValue + "s"), false);
                            return 1;
                        }))
                    .then(CommandManager.literal("reset")
                        .executes(commandContext -> {
                            ServerCommandSource source = commandContext.getSource();

                            if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
                                source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                                return 0;
                            }

                            PlayerTimerData.save(player, 0);
                            playerTimers.remove(player);
                            reset();
                            source.sendFeedback(() -> Text.literal("[Timer] Timer reset to 0"), false);
                            return 1;
                        }))
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
                    // Separate subcommand for setting a timer value
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

                                int timerValue = IntegerArgumentType.getInteger(commandContext, "sec");
                                min = IntegerArgumentType.getInteger(commandContext, "min");
                                hour = IntegerArgumentType.getInteger(commandContext, "hour");
                                day = IntegerArgumentType.getInteger(commandContext, "day");
                                playerTimers.put(player, timerValue);
                                source.sendFeedback(() -> Text.literal("[Timer] Timer set to: " + day + "d " + hour + "h " + min + "m " + timerValue + "s"), false);
                                return 1;
                            })
                        )
                    )
            ))));
        });

    }

    private static void reset() {
        min = 0;
        hour = 0;
        day = 0;
    }
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
