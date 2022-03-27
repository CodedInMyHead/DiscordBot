package com.testBot.bot.utility;

import com.testBot.bot.events.MessageListener;
import com.testBot.bot.games.MathData;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CacheService {

    private final Map<Snowflake, MathData> mathGameCache = new ConcurrentHashMap<>();

    public boolean addMathsGame(final Message eventMessage, final int numberOne, final int numberTwo, final String operator, final MessageListener messageListener) {
        final Snowflake channelId = eventMessage.getChannelId();
        if (mathGameCache.get(channelId) == null) {
            log.info("Created new game in channel " + channelId);
            mathGameCache.put(channelId, new MathData(eventMessage, numberOne, numberTwo, operator, messageListener));
            return true;
        } else {
            return false;
        }

    }

    public MathData getMathGameData(final Snowflake channelId) {
        return mathGameCache.get(channelId);
    }

    public void deleteMathGame(final Snowflake channelId) {
        log.info("Deleted Game in Channel " + channelId);
        mathGameCache.remove(channelId);
    }
}
