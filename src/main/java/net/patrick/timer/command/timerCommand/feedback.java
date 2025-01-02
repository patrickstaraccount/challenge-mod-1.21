package net.patrick.timer.command.timerCommand;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.Normalizer;

//formats the feedback sent back to the player
public class feedback {
    static Text s = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text s2 = Text.literal("Timer").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
    static Text s3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));

    static Text timer = Text.empty().append(s).append(s2).append(s3);

    public static void help(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
            .append(feedback.timer)
            .append("/timer start um den Timer zu starten").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(feedback.timer)
            .append("/timer pause um den Timer zu pausieren").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(feedback.timer)
            .append("/timer resume um den Timer fortzusetzen").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(feedback.timer)
            .append("/timer reset um den Timer auf 0 zu setzen").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(feedback.timer)
            .append("/timer set <sec> <min> <hours> <days> um den Timer auf einen bestimmten Wert zu setzen").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void reset(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
            .append(feedback.timer)
            .append("Timer auf 0 gesetzt").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void start(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Timer gestartet").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void pause(ServerCommandSource source, int days, int hours, int minutes, int seconds){
        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Timer pausiert bei: ").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
             .append(styleFormatTimeMessage(formatTimeMessage)),
                false);
    }

        public static Text styleFormatTimeMessage(String formatTimeMessage){
            return Text.literal(formatTimeMessage).setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
        }

    public static void noPause(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Kein laufender Timer gefunden!").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void resume(ServerCommandSource source, int persistedTimerValue){
        String formatTimeMessage = TimerCommand.formatTimeMessage(persistedTimerValue / (24 * 3600), (persistedTimerValue % (24 * 3600)) / 3600, (persistedTimerValue % 3600) / 60, persistedTimerValue % 60);
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Timer fortgesetzt bei: ").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
             .append(styleFormatTimeMessage(formatTimeMessage)),
                false);
    }

    public static void noResume(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Kein Timer zum fortsetzen gefunden").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void invalidInput(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Falsche Zeiten übergeben! Bitte überprüfe deine Angaben").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void invalidDay(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Du kannst den Timer nicht auf mehr als 1000 Tage setzen").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                true);
    }

    public static void set(ServerCommandSource source, String timeMessage){
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Timer gesetzt auf: ").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
             .append(styleTimeMessage(timeMessage)),
                false);
    }

        public static Text styleTimeMessage(String timeMessage){
            return Text.literal(timeMessage).setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        }

    public static void isPlayer(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(feedback.timer)
             .append("Dieser Command kann nur von einem Spieler verwendet werden").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void welcome(ServerPlayerEntity player){
        Text a1 = Text.literal("----------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("Timer").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        player.sendMessage(Text.empty());
        player.sendMessage(Text.empty().append(feedback.timer).append(a1).append("  ").append(a2).append("  ").append(a1));
        player.sendMessage(Text.empty().append(feedback.timer).append("    ").append("/timer help für Syntax").append("    "));
        player.sendMessage(Text.empty());
    }
}