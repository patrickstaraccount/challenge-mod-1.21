package net.patrick.challenge.commands.challengeCommand;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.patrick.challenge.commands.timerCommand.TimerCommand;


public class challengeFeedback {
    static Text c = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text c2 = Text.literal("Challenge").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
    static Text c3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text t = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text t2 = Text.literal("Tod").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
    static Text t3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text challenge = Text.empty().append(c).append(c2).append(c3);
    static Text tod = Text.empty().append(t).append(t2).append(t3);

    public static void welcome(ServerPlayerEntity player){
        Text a1 = Text.literal("----------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("Challenge").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        player.sendMessage(Text.empty());
        player.sendMessage(Text.empty().append(challengeFeedback.challenge).append(a1).append("  ").append(a2).append("  ").append(a1));
        player.sendMessage(Text.empty().append(challengeFeedback.challenge).append("    ").append("/challenge help für Syntax").append("    "));
        player.sendMessage(Text.empty());
    }

    public static void noFallEnd(ServerCommandSource source, int timerValue){
        int days = timerValue / (24 * 3600);
        int hours = (timerValue % (24 * 3600)) / 3600;
        int minutes = (timerValue % 3600) / 60;
        int seconds = timerValue % 60;
        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);

        source.sendFeedback(() -> Text.empty(), false);
        source.sendFeedback(() -> Text.empty()
                .append(challengeFeedback.challenge)
                .append("Challenge wurde abgebrochen!")
                    .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
                .append(challengeFeedback.challenge)
                .append("Der Timer wurde automatisch pausiert bei: ")
                .append(styleFormatTimeMessage(formatTimeMessage))
                    .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty(), false);
    }

    public static void noFallStart(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty(), false);
        source.sendFeedback(() -> Text.empty()
                .append(challengeFeedback.challenge)
                .append("Challenge wurde gestartet!")
                    .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
                .append(challengeFeedback.challenge)
                .append("Der Timer wurde automatisch gestartet")
                    .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty(), false);
    }

    public static void help(ServerCommandSource source){
        Text a1 = Text.literal("------------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("Challenge").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        source.sendFeedback(() -> Text.empty(), false);
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
        source.sendFeedback(() -> Text.empty(), false);
    }

    public static void noFallFailed(LivingEntity player, int timerValue){
        Text a1 = Text.literal("----------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("NoFallDamage").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
        int days = timerValue / (24 * 3600);
        int hours = (timerValue % (24 * 3600)) / 3600;
        int minutes = (timerValue % 3600) / 60;
        int seconds = timerValue % 60;

        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);
        player.sendMessage(Text.literal(""));
        player.sendMessage(Text.empty().append(a1).append(a2).append(a1));
        player.sendMessage(Text.empty().append(challengeFeedback.tod).append("Challenge nicht erfolgreich beendet"));
        player.sendMessage(Text.empty().append(challengeFeedback.tod).append("Zeit verschwendet: ").append(styleFormatTimeMessage(formatTimeMessage)));
        player.sendMessage(Text.literal(""));
    }

        public static Text styleFormatTimeMessage(String formatTimeMessage){
            return Text.literal(formatTimeMessage).setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
        }

    public static void noFallEndError(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
                .append(challengeFeedback.challenge)
                .append("Diese Challenge ist aktuell nicht aktiv!")
                    .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void noFallStartError(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
                        .append(challengeFeedback.challenge)
                        .append("Die NoFallDamage Challenge ist schon aktiv!")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }
}
