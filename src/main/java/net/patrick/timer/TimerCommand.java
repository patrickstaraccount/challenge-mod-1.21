package net.patrick.timer;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.patrick.timer.command.DevCleanSuggestionProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                    if(sec < 60){
                        player.sendMessage(Text.literal(min + "m " + sec + "s")
                                        .setStyle(Style.EMPTY
                                                .withColor(Formatting.GOLD)
                                                .withBold(true)),
                                true);
                    }else{
                        min++;
                        timerValue = 0;
                        sec = 0;
                        player.sendMessage(Text.literal(min + "m " + sec + "s")
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
                .then(CommandManager.argument("test", StringArgumentType.string())
                    .suggests(new DevCleanSuggestionProvider())
                    .executes(commandContext -> {
                        String typ = StringArgumentType.getString(commandContext, "test");
                        ServerCommandSource source = commandContext.getSource();
                        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();

                        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                            source.sendFeedback(() -> Text.literal("This can only be used by a player"), false);
                            return 1;
                        }

                        if (Objects.equals(typ, "")){
                            typ = "help";
                        }

                        switch (typ) {
                            case "start" -> {
                                playerTimers.put(player, 0);
                                PlayerTimerData.save(player, 0);
                                source.sendFeedback(() -> Text.literal("[Timer] Timer started at 0"), false);
                            }
                            case "pause" -> {
                                if (playerTimers.containsKey(player)) {
                                    int currentTimerValue = playerTimers.get(player); // Get current timer
                                    PlayerTimerData.save(player, currentTimerValue); // Save to NBT
                                    playerTimers.remove(player); // Stop tracking in memory
                                    source.sendFeedback(() -> Text.literal("[Timer] Timer paused at: " + min + "m " + currentTimerValue + "s"), false);
                                } else {
                                    source.sendFeedback(() -> Text.literal("[Timer] No active timer to pause!"), false);
                                }
                            }
                            case "resume" -> {
                                int persistedTimerValue = PlayerTimerData.load(player); // Get saved timer value from NBT
                                playerTimers.put(player, persistedTimerValue); // Add to active timers
                                source.sendFeedback(() -> Text.literal("[Timer] Timer resumed at: " + min + "m " + persistedTimerValue + "s"), false);
                            }
                            case "reset" -> {
                                PlayerTimerData.save(player, 0);
                                playerTimers.remove(player);
                                TimerCommand.reset();
                                source.sendFeedback(() -> Text.literal("[Timer] Timer reset to 0"), false);
                            }
                            case "help" -> {
                                source.sendFeedback(() -> Text.literal("[Timer] /timer start to start the Timer"), false);
                                source.sendFeedback(() -> Text.literal("[Timer] /timer pause to pause the Timer"), false);
                                source.sendFeedback(() -> Text.literal("[Timer] /timer resume to resume the Timer at the last paused stage"), false);
                                source.sendFeedback(() -> Text.literal("[Timer] /timer reset to reset the Timer back to 0"), false);
                            }
                            default -> {
                                source.sendFeedback(() -> Text.literal("[Timer] Type /timer help for Syntax"), false);
                            }
                        }
                        return 1;
                    })));
        });
    }

    private static void reset() {
        min = 0;
        hour = 0;
        day = 0;
    }
}
