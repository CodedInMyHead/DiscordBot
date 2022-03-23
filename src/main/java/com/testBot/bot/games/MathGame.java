package com.testBot.bot.games;

import discord4j.core.object.entity.Message;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class MathGame {

    public static int timeUntilFailed = 30;
    public static int numberOne = -1;
    public static int numberTwo = -1;
    public static String operator = "";
    public static int result = -1;
    public static int timeLeft = timeUntilFailed;
    private static boolean timerIsActive = false;
    public static Message eventMessage;

    public static int calculate() {
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

    public static void reset() {
        stopTimer();
        timeLeft = timeUntilFailed;
        numberTwo = -1;
        numberOne = -1;
        operator = "";
        result = -1;
    }

    public static boolean isNotStarted() {
        return numberTwo == -1 &&
                numberOne == -1 &&
                operator.equals("") &&
                result == -1;
    }

    public static void startTimer() {
        timerIsActive = true;
    }

    public static void stopTimer() {
        timerIsActive = false;
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    public static void timer() {
        if (timerIsActive) {
            if(timeLeft <= 0) {
                // TODO: MAKE SURE IT WORKS BECAUSE NO FUCKING CLUE
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
    }
}
