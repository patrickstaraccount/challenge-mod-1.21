package net.patrick.timer;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerTickManager;
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
    public static void register() {

        ServerTickEvents.START_SERVER_TICK.register(minecraftServer -> {
            tickcounter++;
            if(tickcounter >= 20){
                tickcounter = 0;
                for (Map.Entry<ServerPlayerEntity, Integer> entry : playerTimers.entrySet()){
                    ServerPlayerEntity player = entry.getKey();
                    int timerValue = entry.getValue();

                    player.sendMessage(Text.literal("Timer:" + timerValue)
                                    .setStyle(Style.EMPTY
                                            .withColor(Formatting.GOLD)
                                            .withBold(true)),
                            true);
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
                                    source.sendFeedback(() -> Text.literal("[Timer] Timer paused at: " + currentTimerValue), false);
                                } else {
                                    source.sendFeedback(() -> Text.literal("[Timer] No active timer to pause!"), false);
                                }
                            }
                            case "resume" -> {
                                int persistedTimerValue = PlayerTimerData.load(player); // Get saved timer value from NBT
                                playerTimers.put(player, persistedTimerValue); // Add to active timers
                                source.sendFeedback(() -> Text.literal("[Timer] Timer resumed at: " + persistedTimerValue), false);
                            }
                            case "reset" -> {
                                PlayerTimerData.save(player, 0);
                                playerTimers.put(player, 0); // Reset in memory
                                source.sendFeedback(() -> Text.literal("[Timer] Timer reset to 0"), false);
                            }
                            default -> {
                                source.sendFeedback(() -> Text.literal("[Timer] Invalid action. Use /timer start, pause, resume, or reset."), false);
                            }
                        }
                        return 1;
                    })));
        });
    }
}
