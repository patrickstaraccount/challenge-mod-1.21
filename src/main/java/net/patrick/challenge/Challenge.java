package net.patrick.challenge;

import net.fabricmc.api.ModInitializer;
import net.patrick.challenge.challenges.noFallDamage.noFallDamage;
import net.patrick.challenge.challenges.threeHearths.threeHearths;
import net.patrick.challenge.commands.challengeCommand.ChallengeCommand;
import net.patrick.challenge.commands.miscCommands.flyCommand;
import net.patrick.challenge.commands.miscCommands.godCommand;
import net.patrick.challenge.commands.miscCommands.healCommand;
import net.patrick.challenge.commands.timer.TimerCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Challenge implements ModInitializer {
	public static final String MOD_ID = "timer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



	@Override
	public void onInitialize() {
		TimerCommand.register();        //register TimerCommand
		ChallengeCommand.register();    //register ChallengeCommand
		godCommand.register();          //register godCommand
		healCommand.register();         //register healCommand
		flyCommand.register();          //register flyCommand
		noFallDamage.register();        //register noFallDamage Challenge
	}
}