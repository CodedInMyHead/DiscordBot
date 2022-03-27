package com.testBot.bot.events;

import com.testBot.bot.events.common.UserCommandDecider;
import com.testBot.bot.events.common.UserCommand;
import com.testBot.bot.games.MathData;
import com.testBot.bot.utility.CacheService;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class MessageListener {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private UserCommandDecider userCommandDecider;

    private Mono<Void> mono = Mono.empty();

    public final String VERSION = "1.0.0";
    private final String THATSWHATSHESAID_MESSAGE = "That's what she said";
    private final String WHY_MESSAGE = "I was created to rid Micha of boredom at work and show Florian more complex parts of IT";
    private final String VERSION_MESSAGE = "I, Alita Bot, am Running v" + VERSION;
    private final String HELP_MESSAGE =
            "Commands start with a '!'. \n " +
            "e.g. !why \n " +
            "The bot also responds to keywords such as OwO/UwU or 'kommt' \n " +
            "possible commands are: \n" +
            "!why " + "\n" +
            "!help " + "\n" +
            "!version " + "\n" +
            "!author " + "\n" +
            "!plan " + "\n" +
            "!purpose " + "\n" +
            "!repo " + "\n" +
            "!math " + "\n" +
            "!answer " + "\n" +
            "!math timer ";


    private void doAnswer(final Message eventMessage) {
        if (cacheService.getMathGameData(eventMessage.getChannelId()) == null) {
            log.info("game needs to be created first in channel " + eventMessage.getChannelId());
            this.mono = Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("Your first need to start a game using !math"))
                    .then();
            return;
        }
        final String messageString = eventMessage.getContent();
        final String answerString = messageString.substring(8, messageString.length());
        final int answerInt;
        try {
            answerInt = Integer.parseInt(answerString);
        } catch (final Exception e) {
            this.mono = Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("That is not an integer!"))
                    .then();
            return;
        }
        final MathData mathData = cacheService.getMathGameData(eventMessage.getChannelId());
        if (answerInt == mathData.calculate()) {
            mathData.reset();
            this.mono = Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("You're right!!"))
                    .then();
        } else {
            this.mono = Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("Wrong! Think again (and fast)"))
                    .then();
        }
    }

    private void doMathStart(final Message eventMessage) {
        final int operatorIndex = (int)(Math.random()*8);
        final String operator;

        switch (operatorIndex) {
            case 0:
            case 1:
                operator = "+";
                break;
            case 2:
            case 3:
                operator = "-";
                break;
            case 4:
            case 5:
                operator = "*";
                break;
            case 6:
            case 7:
                operator = "%";
                break;
            default:
                operator = "error, pls report";
        }
        if (cacheService.addMathsGame(eventMessage, (int)(Math.random()*28)+ 2, (int)(Math.random()*28)+ 2, operator, this)) {
            final MathData mathData = cacheService.getMathGameData(eventMessage.getChannelId());
            this.mono = Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("What is " + mathData.numberOne + " " + mathData.operator + " " + mathData.numberTwo + " ?" + "\nAnswer using !answer <number> (Don't write the <>)"))
                    .then();
        } else {
            log.info("Already a game running in channel " + eventMessage.getChannelId());
            this.mono = Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("There is still a MathGame running in this channel!"))
                    .then();
        }
    }

    private void doSay(final Message eventMessage, final String sentence) {
        this.mono = Mono.just(eventMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(sentence))
                .then();
    }

    public Mono<Void> processCommand(final Message eventMessage) {
        Map<String, Runnable> commands = new HashMap<>();
        commands.put("!math start", () -> doMathStart(eventMessage));
        commands.put("!answer", () -> doAnswer(eventMessage));
        commands.put("kommt", () -> doSay(eventMessage, THATSWHATSHESAID_MESSAGE));
        commands.put("owo", () -> doSay(eventMessage, THATSWHATSHESAID_MESSAGE));
        commands.put("uwu", () -> doSay(eventMessage, THATSWHATSHESAID_MESSAGE));
        commands.put("!help", () -> doSay(eventMessage, HELP_MESSAGE));
        commands.put("!why", () -> doSay(eventMessage, WHY_MESSAGE));
        commands.put("!version", () -> doSay(eventMessage, VERSION_MESSAGE));
        List<String> commandList = new ArrayList<>();
        commands.forEach((k,v) -> commandList.add(k));
        final UserCommand userCommand = userCommandDecider.getCommandTheUserWants(eventMessage.getContent().toLowerCase(), commandList);

        commands.forEach((k,v) -> {
            if (k.equals(userCommand.toString())) {
                v.run();
            }
        });

        return this.mono;
        // END HERE

        if (eventMessage.getContent().equalsIgnoreCase("!math timer")) {
            return Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("The time you have to answer is " + MathData.timeUntilFailed + " seconds"))
                    .then();
        }
        else if (eventMessage.getContent().toLowerCase().startsWith("!math timer ")) {
            final String newTimeoutString = eventMessage.getContent().substring(11, -1);

            int newTimeout = 30;
            try {
                newTimeout = Integer.parseInt(newTimeoutString);
            } catch (final Exception e) {
                return Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("Not a number!\nTimeout reset to default (30) \ne.g. !math timer 30"))
                        .then();
            }
            MathData.timeUntilFailed = newTimeout;

            return Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("Set the timeout to "+ MathData.timeUntilFailed))
                    .then();
        }
        else {
            // Implicit calls
            if (eventMessage.getContent().toLowerCase().contains("!author")) {

                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("I was created by Micha and Florian"))
                        .then();
            }

            if (eventMessage.getContent().toLowerCase().contains("!purpose")) {

                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("I mainly say 'That's what she said'"))
                        .then();
            }

            if (eventMessage.getContent().toLowerCase().contains("!repo")) {

                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("My Source Code can be found at https://github.com/CodedInMyHead/DiscordBot \n "+
                                "If you want to create your own bot feel free to ask Micha"))
                        .then();
            }

            if (eventMessage.getContent().toLowerCase().contains("!plan")) {

                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("Add more keywords to message event listener" + "\n" +
                                "Make little games (guessing?) based on user input after command" + "\n" +
                                "add command to tell  random quote from #zitate" + "\n" +
                                "Add ability to play music from youtube" + "\n"
                        ))
                        .then();
            }
        }
        return mono;
    }

    public void deleteMathGame(final Snowflake snowflake) {
        cacheService.deleteMathGame(snowflake);
    }
}