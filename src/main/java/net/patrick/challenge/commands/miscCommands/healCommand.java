package net.patrick.challenge.commands.miscCommands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class healCommand {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("heal")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    Text t = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text t2 = Text.literal("Heal").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
                    Text t3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
                    Text heal = Text.empty().append(t).append(t2).append(t3);

                    source.sendFeedback(() -> Text.empty().append(heal).append("Leben und Hunger auf maximum gesetzt"), false);
                    player.setHealth(player.getMaxHealth());
                    player.getHungerManager().setFoodLevel(20);
                    return 1;
            }));
        });
    }
}
