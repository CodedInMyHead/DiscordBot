package com.testBot.bot.events;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageListener {
    public final static String VERSION = "1.0.0";
    public Mono<Void> processCommand(Message eventMessage) {
        Mono<Void> mono = Mono.empty();

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
                            "!repo " + "\n" ))
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
                    .flatMap(channel -> channel.createMessage("Add more keywords to message event listener"))
                    .then();
        }

        return mono;
    }
}