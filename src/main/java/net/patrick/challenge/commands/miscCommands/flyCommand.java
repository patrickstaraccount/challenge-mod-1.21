package net.patrick.challenge.commands.miscCommands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;

//adds a fly command to the game so the player can fly
public class flyCommand {
    //all players who can fly are stored here
    public static final Set<ServerPlayerEntity> flyPlayers = new HashSet<>();
    public static void register(){
        //registering the command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("fly")
                //is executed, when player uses the command
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    Text t = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text t2 = Text.literal("Fly").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
                    Text t3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text fly = Text.empty().append(t).append(t2).append(t3);

                    //checks if player is creative or spectator
                    assert player != null;
                    if (player.isCreative() || player.isSpectator()){
                        source.sendFeedback(() -> Text.empty().append(fly).append("Du kannst diesen Command nur nutzen wenn du im Survival modus bist"), false);
                    }else if(flyPlayers.contains(player)){
                        //when player is in the hashset, then remove player from hashset and send feedback
                        flyPlayers.remove(player);
                        source.sendFeedback(() -> Text.empty().append(fly).append("Deaktiviert"), false);
                    }else {
                        //when player is not in the hashset, add player to hashset and send feedback
                        flyPlayers.add(player);
                        source.sendFeedback(() -> Text.empty().append(fly).append("Aktiviert"), false);
                    }
                    return 1;
            }));
        });

        //executes at the start of every tick
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            for(ServerPlayerEntity player : world.getPlayers()){
                //checks for every player in the world if he is in the hashset
                var abilities = player.getAbilities();
                if(flyPlayers.contains(player)){
                    //when player is the hashset, set abilitie allowFlying to true
                    abilities.allowFlying = true;

                }else {
                    //when player is not in the hashset, set abilitie allowFlying to false
                    abilities.allowFlying = false;
                    abilities.flying = false;

                }
                player.sendAbilitiesUpdate();
            }
        });

        //executes at the start of every tick
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            for (ServerPlayerEntity player : world.getPlayers()){
                //checks for every player in the world if he is in the hashset
                if(!flyPlayers.contains(player)){
                    //checks if player is creative or spectator
                    if(player.isCreative() || player.isSpectator()){
                        //when player is creative or spectator, add player to the hashset
                        flyPlayers.add(player);
                    }
                }
            }
        });
    }
}
