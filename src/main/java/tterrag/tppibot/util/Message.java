package tterrag.tppibot.util;

import java.util.function.Consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.pircbotx.PircBotX;

@RequiredArgsConstructor
public class Message {

    @RequiredArgsConstructor
    public enum MessageType {
        MESSAGE(m -> m.bot.sendIRC().message(m.to, m.message)),
        NOTICE(m -> m.bot.sendIRC().message(m.to, m.message)),
        ACTION(m -> m.bot.sendIRC().message(m.to, m.message));

        public final Consumer<Message> function;
    }

    private final PircBotX bot;
    private final String to, message;
    private final MessageType type;

    @Getter
    private boolean sent = false;

    public void send() {
        type.function.accept(this);
        sent = true;
    }
}
