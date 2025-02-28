package net.patrick.challenge.commands.challengeCommand;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

//formats challengeFeedback send back to the player
public class challengeFeedback {
    static Text c = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text c2 = Text.literal("Challenge").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
    static Text c3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));

    static Text challenge = Text.empty().append(c).append(c2).append(c3);

    public static void help(ServerCommandSource source){
        Text a1 = Text.literal("------------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("Challenge").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        source.sendFeedback(Text::empty, false);
        source.sendFeedback(() -> Text.empty().append(challengeFeedback.challenge).append(a1).append("  ").append(a2).append("  ").append(a1), false);
        source.sendFeedback(() -> Text.empty()
                        .append(challengeFeedback.challenge)
                        .append("  ")
                        .append("/challenge <challengeName> start um sie zu starten"),
                false);
        source.sendFeedback(() -> Text.empty()
                        .append(challengeFeedback.challenge)
                        .append("  ")
                        .append("/challenge <challengeName> end um sie abzubrechen"),
                false);
        source.sendFeedback(Text::empty, false);
    }

    public static void noFallActive(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty().append(challengeFeedback.challenge).append("Du musst zuerst die NoFallDamage Challenge deaktivieren!"), false);
        source.sendFeedback(() -> Text.empty().append(challengeFeedback.challenge).append("/challenge help for Syntax!"), false);
    }

    public static void threeHearthsActive(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty().append(challengeFeedback.challenge).append("Du musst zuerst die Three Hearths Challenge deaktivieren!"), false);
        source.sendFeedback(() -> Text.empty().append(challengeFeedback.challenge).append("/challenge help for Syntax!"), false);
    }
}
