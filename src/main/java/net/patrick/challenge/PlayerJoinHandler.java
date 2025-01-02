package net.patrick.challenge;


import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.patrick.challenge.commands.challengeCommand.challengeFeedback;
import net.patrick.challenge.commands.timerCommand.timerFeedback;

public class PlayerJoinHandler {
    public static void register(){

        //is executed, when player joins world
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            ServerPlayerEntity player = serverPlayNetworkHandler.getPlayer();
            timerFeedback.welcome(player);
            challengeFeedback.welcome(player);
        });
    }
}
