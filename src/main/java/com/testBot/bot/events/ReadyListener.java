package com.testBot.bot.events;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReadyListener implements EventListener<ReadyEvent> {

    @Override
    public Class<ReadyEvent> getEventType() {
        return ReadyEvent.class;
    }

    @Override
    public Mono<Void> execute(ReadyEvent event) {
        return Mono.fromRunnable(() -> System.out.print("I have arrived"));
    }
}