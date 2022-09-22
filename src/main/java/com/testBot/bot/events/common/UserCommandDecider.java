package com.testBot.bot.events.common;

import org.springframework.stereotype.Service;

import java.util.List;


// Service to calculate how many letters of a given input are inside a string.
// If the fitness score is equal to the amount of letters in the command, the right command is found

@Service
public class UserCommandDecider {

    private boolean seeIfCommandIsInInput(final String input, final String command) {
        return input.contains(command);
    }

    public UserCommand getCommandTheUserWants(final String input, final List<String> commands) {
        for (String command : commands) {
            if (seeIfCommandIsInInput(input, command)) {
                return UserCommand.of(command);
            }
        }
        return UserCommand.of(null);
    }
}
