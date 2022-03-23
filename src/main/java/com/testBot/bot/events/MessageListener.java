package com.testBot.bot.events;

import com.testBot.bot.games.MathGame;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageListener {
    public final static String VERSION = "1.0.0";
    public Mono<Void> processCommand(Message eventMessage) {
        Mono<Void> mono = Mono.empty();

        if (eventMessage.getContent().toLowerCase().startsWith("!math start")) {
            // Explicit calls

            MathGame.numberOne = (int)(Math.random()*28)+ 2;
            MathGame.numberTwo = (int)(Math.random()*28)+ 2;
            final int operatorIndex = (int)(Math.random()*8);
            String operator;

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
            MathGame.operator = operator;
            MathGame.eventMessage = eventMessage;
            MathGame.startTimer();
            return Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("What is " + MathGame.numberOne + " " + MathGame.operator + " " + MathGame.numberTwo + " ?" + "\nAnswer using !answer <number> (Don't write the <>"))
                    .then();
        } else if (eventMessage.getContent().toLowerCase().startsWith("!answer ")) {
            if (MathGame.isNotStarted()) {
                return Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("Your first need to start a game using !math"))
                        .then();
            }
            final String messageString = eventMessage.getContent();
            final String answerString = messageString.substring(8, -1);
            final int answerInt;
            try {
                answerInt = Integer.parseInt(answerString);
            } catch (final Exception e) {
                return Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("That is not an integer!"))
                        .then();
            }
            if (answerInt == MathGame.calculate()) {
                MathGame.reset();
                return Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("You're right!!"))
                        .then();
            } else {
                return Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("Wrong! Think again (and fast)"))
                        .then();
            }
        } else if (eventMessage.getContent().equalsIgnoreCase("!math timer")) {
            return Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("The time you have to answer is " + MathGame.timeUntilFailed + " seconds"))
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
            MathGame.timeUntilFailed = newTimeout;

            return Mono.just(eventMessage)
                    .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                    .flatMap(Message::getChannel)
                    .flatMap(channel -> channel.createMessage("Set the timeout to "+MathGame.timeUntilFailed))
                    .then();
        }
        else {
            // Implicit calls

            if (eventMessage.getContent().toLowerCase().contains("kommt") ||
                    eventMessage.getContent().toLowerCase().contains("owo") ||
                    eventMessage.getContent().toLowerCase().contains("uwu")) {

                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("That's what she said"))
                        .then();
            }

            if (eventMessage.getContent().toLowerCase().contains("!help")) {
                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage(
                                "Commands start with a '!'. \n e.g. !why \n The bot also responds to keywords such as OwO/UwU or 'kommt' \n possible commands are: \n" +
                                        "!why " + "\n" +
                                        "!help " + "\n" +
                                        "!version " + "\n" +
                                        "!author " + "\n" +
                                        "!plan " + "\n" +
                                        "!purpose " + "\n" +
                                        "!repo " + "\n" +
                                        "!math " + "\n" +
                                        "!answer " + "\n" +
                                        "!math timer "
                        ))
                        .then();
            }

            if (eventMessage.getContent().toLowerCase().contains("!why")) {

                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("I was created to rid Micha of boredom at work and show Florian more complex parts of IT"))
                        .then();
            }

            if (eventMessage.getContent().toLowerCase().contains("!version")) {

                mono = Mono.just(eventMessage)
                        .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                        .flatMap(Message::getChannel)
                        .flatMap(channel -> channel.createMessage("I, Alita Bot, am Running v" + VERSION))
                        .then();
            }

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
}