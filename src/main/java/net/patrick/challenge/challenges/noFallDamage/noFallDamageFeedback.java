package net.patrick.challenge.challenges.noFallDamage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.patrick.challenge.commands.challengeCommand.challengeFeedback;
import net.patrick.challenge.commands.timer.TimerCommand;

public class noFallDamageFeedback {
    static Text t = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text t2 = Text.literal("Tod").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
    static Text t3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text c = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text c2 = Text.literal("NoFallDamage").setStyle(Style.EMPTY.withColor(Formatting.GOLD));
    static Text c3 = Text.literal("] ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
    static Text challenge = Text.empty().append(c).append(c2).append(c3);
    static Text tod = Text.empty().append(t).append(t2).append(t3);

    public static void challengeStart(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty(), false);
        source.sendFeedback(() -> Text.empty()
                        .append(noFallDamageFeedback.challenge)
                        .append("Challenge wurde gestartet!")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
                        .append(noFallDamageFeedback.challenge)
                        .append("Der Timer wurde automatisch gestartet")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty(), false);
    }

    public static void challengeEnd(ServerCommandSource source, int timerValue){
        int days = timerValue / (24 * 3600);
        int hours = (timerValue % (24 * 3600)) / 3600;
        int minutes = (timerValue % 3600) / 60;
        int seconds = timerValue % 60;
        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);

        source.sendFeedback(() -> Text.empty(), false);
        source.sendFeedback(() -> Text.empty()
                        .append(noFallDamageFeedback.challenge)
                        .append("Challenge wurde abgebrochen!")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty()
                        .append(noFallDamageFeedback.challenge)
                        .append("Zeit verschwendet: ")
                        .append(styleFormatTimeMessage(formatTimeMessage))
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
        source.sendFeedback(() -> Text.empty(), false);
    }

    public static void noFallFailed(LivingEntity player, int timerValue){
        Text a1 = Text.literal("----------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("NoFallDamage").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
        Text playerFailed = Text.literal(player.getName().getString())
                .setStyle(Style.EMPTY
                        .withColor(Formatting.GREEN)
                        .withBold(true));
        int days = timerValue / (24 * 3600);
        int hours = (timerValue % (24 * 3600)) / 3600;
        int minutes = (timerValue % 3600) / 60;
        int seconds = timerValue % 60;

        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);
        player.sendMessage(Text.literal(""));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append(a1).append(a2).append(a1));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append(playerFailed).append(" hat Fallschaden genommen!"));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append("Challenge nicht erfolgreich beendet"));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append("Zeit verschwendet: ").append(styleFormatTimeMessage(formatTimeMessage)));
        player.sendMessage(Text.literal(""));
    }

    public static void noFallEndError(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
                        .append(noFallDamageFeedback.challenge)
                        .append("Die NoFallDamage Challenge ist aktuell nicht aktiv!")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void noFallStartError(ServerCommandSource source){
        source.sendFeedback(() -> Text.empty()
                        .append(noFallDamageFeedback.challenge)
                        .append("Die NoFallDamage Challenge ist schon aktiv!")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY)),
                false);
    }

    public static void completionMessage(ServerPlayerEntity player, int timerValue, ServerPlayerEntity playerKilledDragon){
        Text a1 = Text.literal("----------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("NoFallDamage").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
        int days = timerValue / (24 * 3600);
        int hours = (timerValue % (24 * 3600)) / 3600;
        int minutes = (timerValue % 3600) / 60;
        int seconds = timerValue % 60;
        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);
        Text playerKiller = (Text.literal(playerKilledDragon.getName().getString())
                .setStyle(Style.EMPTY
                        .withColor(Formatting.GREEN)
                        .withBold(true)));

        player.sendMessage(Text.empty());
        player.sendMessage(Text.empty()
                .append(noFallDamageFeedback.challenge)
                .append(a1)
                .append(" ")
                .append(a2)
                .append(" ")
                .append(a1));
        player.sendMessage(Text.empty()
                .append(noFallDamageFeedback.challenge)
                .append(playerKiller)
                .append(" hat den EnderDragon besiegt!"));
        player.sendMessage(Text.empty()
                .append(noFallDamageFeedback.challenge)
                .append("Challenge erfolgreich beendet!"));
        player.sendMessage(Text.empty()
                .append(noFallDamageFeedback.challenge)
                .append("Benötigte Zeit: ")
                .append(styleFormatTimeMessage(formatTimeMessage)));
        player.sendMessage(Text.empty());
    }

    public static void noFallFailedN(ServerPlayerEntity player, int timerValue){
        Text a1 = Text.literal("----------").setStyle(Style.EMPTY.withColor(Formatting.GRAY));
        Text a2 = Text.literal("NoFallDamage").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
        Text playerFailed = Text.literal(player.getName().getString())
                .setStyle(Style.EMPTY
                        .withColor(Formatting.GREEN)
                        .withBold(true));
        int days = timerValue / (24 * 3600);
        int hours = (timerValue % (24 * 3600)) / 3600;
        int minutes = (timerValue % 3600) / 60;
        int seconds = timerValue % 60;

        String formatTimeMessage = TimerCommand.formatTimeMessage(days, hours, minutes, seconds);
        player.sendMessage(Text.literal(""));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append(a1).append(a2).append(a1));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append(playerFailed).append(" ist gestorben!"));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append("Challenge nicht erfolgreich beendet!"));
        player.sendMessage(Text.empty().append(noFallDamageFeedback.tod).append("Zeit verschwendet: ").append(styleFormatTimeMessage(formatTimeMessage)));
        player.sendMessage(Text.literal(""));

    }

    public static Text styleFormatTimeMessage(String formatTimeMessage){
        return Text.literal(formatTimeMessage).setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true));
    }
}
