package net.patrick.challenge.commands.timerCommand;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

//formats the timerFeedback sent back to the player
public class timerFeedback {
    static Text s = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text s2 = Text.literal("Timer").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
    static Text s3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));

    static Text timer = Text.empty().append(s).append(s2).append(s3);

    public static void help(ServerCommandSource source){
        Text a1 = Text.literal("----------------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("Timer").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        source.sendFeedback(() -> Text.empty(), false);
        source.sendFeedback(() -> Text.empty().append(timerFeedback.timer).append(a1).append("  ").append(a2).append("  ").append(a1), false);
        source.sendFeedback(() -> Text.empty()
            .append(timerFeedback.timer)
            .append("  ")
            .append("/timer start um den Timer zu starten"),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(timerFeedback.timer)
            .append("  ")
            .append("/timer pause um den Timer zu pausieren"),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(timerFeedback.timer)
            .append("  ")
            .append("/timer resume um den Timer fortzusetzen"),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(timerFeedback.timer)
            .append("  ")
            .append("/timer reset um den Timer auf 0 zu setzen"),
                false);
        source.sendFeedback(() -> Text.empty()
            .append(timerFeedback.timer)
            .append("  ")
            .append("/timer set <sec> <min> <hours> <days> um den Timer auf einen bestimmten Wert zu setzen"),
                false);
        source.sendFeedback(() -> Text.empty(), false);
    }

    public static void reset(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
            .append(timerFeedback.timer)
            .append("Timer auf 0 gesetzt").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void start(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Timer gestartet").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void pause(ServerCommandSource source, int days, int hours, int minutes, int seconds){
        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Timer pausiert bei: ").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
             .append(styleFormatTimeMessage(formatTimeMessage)),
                false);
    }

        public static Text styleFormatTimeMessage(String formatTimeMessage){
            return Text.literal(formatTimeMessage).setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
        }

    public static void noPause(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Kein laufender Timer gefunden!").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void resume(ServerCommandSource source, int persistedTimerValue){
        String formatTimeMessage = TimerCommand.formatTimeMessage(persistedTimerValue / (24 * 3600), (persistedTimerValue % (24 * 3600)) / 3600, (persistedTimerValue % 3600) / 60, persistedTimerValue % 60);
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Timer fortgesetzt bei: ").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
             .append(styleFormatTimeMessage(formatTimeMessage)),
                false);
    }

    public static void noResume(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Kein Timer zum fortsetzen gefunden").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void invalidInput(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Falsche Zeiten übergeben! Bitte überprüfe deine Angaben").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void invalidDay(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Du kannst den Timer nicht auf mehr als 1000 Tage setzen").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                true);
    }

    public static void set(ServerCommandSource source, String timeMessage){
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Timer gesetzt auf: ").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
             .append(styleTimeMessage(timeMessage)),
                false);
    }

        public static Text styleTimeMessage(String timeMessage){
            return Text.literal(timeMessage).setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        }

    public static void isPlayer(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
             .append(timerFeedback.timer)
             .append("Dieser Command kann nur von einem Spieler verwendet werden").setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void welcome(ServerPlayerEntity player){
        Text a1 = Text.literal("----------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("Timer").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));

        player.sendMessage(Text.empty());
        player.sendMessage(Text.empty().append(timerFeedback.timer).append(a1).append("  ").append(a2).append("  ").append(a1));
        player.sendMessage(Text.empty().append(timerFeedback.timer).append("    ").append("/timer help für Syntax").append("    "));
        player.sendMessage(Text.empty());
    }
}