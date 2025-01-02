package net.patrick.challenge;

import net.fabricmc.api.ModInitializer;
import net.patrick.challenge.commands.challengeCommand.ChallengeCommand;
import net.patrick.challenge.commands.timerCommand.TimerCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Challenge implements ModInitializer {
	public static final String MOD_ID = "timer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



	@Override
	public void onInitialize() {
		TimerCommand.register();        //register TimerCommand
		ChallengeCommand.register();    //register ChallengeCommand
		PlayerJoinHandler.register();	//register welcome Message
	}
}