package net.patrick.challenge;


import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.patrick.challenge.commands.timerCommand.feedback;

public class PlayerJoinHandler {
    public static void register(){

        //is executed, when player joins world
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            feedback.welcome(player);
        });
    }
}
