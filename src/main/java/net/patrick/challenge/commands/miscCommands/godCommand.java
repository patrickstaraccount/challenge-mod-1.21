package net.patrick.challenge.commands.miscCommands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

public class godCommand {
    public static final Set<ServerPlayerEntity> godPlayers = new HashSet<>();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("god")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    Text t = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text t2 = Text.literal("God").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
                    Text t3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text god = Text.empty().append(t).append(t2).append(t3);

                    if(godPlayers.contains(player)){
                        godPlayers.remove(player);
                        source.sendFeedback(() -> Text.empty().append(god).append("Deaktiviert"), false);
                    }else{
                        godPlayers.add(player);
                        source.sendFeedback(() -> Text.empty().append(god).append("Aktiviert"), false);
                        player.setHealth(player.getMaxHealth());
                    }
                    return 1;
            }));
        });
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if(entity instanceof ServerPlayerEntity player && godPlayers.contains(player)){
                return false;
            }
            return true;
        });

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            for(ServerPlayerEntity player : world.getPlayers())
                if (godPlayers.contains(player)){
                    var abilities = player.getAbilities();

                    abilities.invulnerable = true;
                    player.sendAbilitiesUpdate();
                }else{
                    var abilities = player.getAbilities();

                    abilities.invulnerable = false;
                    player.sendAbilitiesUpdate();
                }
        });
    }
}
