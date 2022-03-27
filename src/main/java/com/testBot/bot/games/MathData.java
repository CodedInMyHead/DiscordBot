package com.testBot.bot.games;

import com.testBot.bot.events.MessageListener;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
public class MathData {

    public final int numberOne;
    public final int numberTwo;
    public String operator;
    private final Message eventMessage;
    public static int timeUntilFailed = 30;
    private int result;
    private final MessageListener messageListener;

    public MathData(final Message eventMessage, final int numberOne, final int numberTwo, final String operator, final MessageListener messageListener) {
        this.numberOne = numberOne;
        this.numberTwo = numberTwo;
        this.operator = operator;
        this.eventMessage = eventMessage;
        this.messageListener = messageListener;
    }


    public int timeLeft = timeUntilFailed;

    public int calculate() {
        switch (operator) {
            case "+":
                result = numberOne + numberTwo;
                break;
            case "-":
                result = numberOne - numberTwo;
                break;
            case "*":
                result = numberOne * numberTwo;
                break;
            case "/":
                result = numberOne / numberTwo;
                break;
        }
        return result;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    private void timer() {
        log.info("Timer ticked in channel " + eventMessage.getChannelId());
        if(timeLeft <= 0) {
            log.info("Timeout in channel " + eventMessage.getChannelId());
            Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("Time is up! \n"+
                            "Try gain by using !math"))
                    .then();

            reset();
        } else {
            timeLeft--;
        }
    }

    public void reset() {
        messageListener.deleteMathGame(eventMessage.getChannelId());
    }
}
