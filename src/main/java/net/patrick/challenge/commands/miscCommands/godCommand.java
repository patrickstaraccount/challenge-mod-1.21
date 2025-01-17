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

//adds a god command to the game so the player is invulnerable
public class godCommand {
    //every player who is invulnerable is stored here
    public static final Set<ServerPlayerEntity> godPlayers = new HashSet<>();

    public static void register() {
        //registering the command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("god")
                //is executed, when player uses the command
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    Text t = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text t2 = Text.literal("God").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
                    Text t3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text god = Text.empty().append(t).append(t2).append(t3);

                    //checks if player is in the hashset
                    if(godPlayers.contains(player)){
                        //when player is in the hashset, remove player from hashset and send feedback
                        godPlayers.remove(player);
                        source.sendFeedback(() -> Text.empty().append(god).append("Deaktiviert"), false);
                    }else{
                        //when player is not in the hashset, add player to the hashset, set health and hunger to max and send feedback
                        godPlayers.add(player);
                        source.sendFeedback(() -> Text.empty().append(god).append("Aktiviert"), false);
                        assert player != null;
                        player.setHealth(player.getMaxHealth());
                        player.getHungerManager().setFoodLevel(20);
                    }
                    return 1;
            }));
        });

        //executes at the start of every tick
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            for(ServerPlayerEntity player : world.getPlayers())
                //checks for every player if he is in the hashset
                if (godPlayers.contains(player)){
                    //when player is in the hashset, set abilitie invulnerable to true and send feedback
                    var abilities = player.getAbilities();

                    abilities.invulnerable = true;
                    player.sendAbilitiesUpdate();
                }else{
                    //when player is not in the hashset, set abilitie invulnerable to false and send feedback
                    var abilities = player.getAbilities();

                    abilities.invulnerable = false;
                    player.sendAbilitiesUpdate();
                }
        });

        //executes at the start of every tick
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            for (ServerPlayerEntity player : world.getPlayers()){
                //checks for every player in the world if he is in the hashset
                if(!godPlayers.contains(player)){
                    //when player is not in the hashset check if player is creative or spectator
                    if(player.isCreative() || player.isSpectator()){
                        //when player is creative or spectator, add player to the hashset
                        godPlayers.add(player);
                    }
                }
            }
        });
    }
}
