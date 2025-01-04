package net.patrick.challenge.commands.miscCommands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

public class flyCommand {
    private static final Set<ServerPlayerEntity> flyPlayers = new HashSet<>();
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("fly")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    Text t = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text t2 = Text.literal("Fly").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
                    Text t3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text fly = Text.empty().append(t).append(t2).append(t3);

                    if(flyPlayers.contains(player)){
                        flyPlayers.remove(player);
                        source.sendFeedback(() -> Text.empty().append(fly).append("Deaktiviert"), false);
                    }else {
                        flyPlayers.add(player);
                        source.sendFeedback(() -> Text.empty().append(fly).append("Aktiviert"), false);
                    }
                    return 1;
            }));
        });

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            for(ServerPlayerEntity player : world.getPlayers()){
                if(flyPlayers.contains(player)){
                    var abilities = player.getAbilities();
                    abilities.allowFlying = true;

                    player.sendAbilitiesUpdate();
                }else {
                    var abilities = player.getAbilities();
                    abilities.allowFlying = false;
                    abilities.flying = false;

                    player.sendAbilitiesUpdate();
                }
            }
        });
    }
}
