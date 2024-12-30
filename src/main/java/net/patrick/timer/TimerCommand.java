package net.patrick.timer;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.patrick.timer.command.DevCleanSuggestionProvider;

import java.util.Objects;

public class TimerCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("timer")
                .then(CommandManager.argument("test", StringArgumentType.string())
                .suggests(new DevCleanSuggestionProvider())
                .executes(commandContext -> {
                    String typ = StringArgumentType.getString(commandContext, "test");
                        if (Objects.equals(typ, "")) {
                            typ = "all";
                        }
                        if(!Objects.equals(typ, "start") &&
                           !Objects.equals(typ, "pause") &&
                           !Objects.equals(typ, "reset") &&
                           !Objects.equals(typ, "resume")){
                                commandContext.getSource().sendFeedback(() -> Text.literal("[Timer]  Wrong type"), false);
                        }
                        return 1;
                    })));
        });
    }
}
