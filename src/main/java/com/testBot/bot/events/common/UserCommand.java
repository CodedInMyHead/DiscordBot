package com.testBot.bot.events.common;

import org.springframework.lang.Nullable;

public class UserCommand {

    private final String command;

    public UserCommand(final String command) {
        this.command = command;
    }

    public static UserCommand of(@Nullable final String command) {
        if(command == null) {
            return UserCommand.empty();
        } else {
            return new UserCommand(command);
        }
    }

    private static UserCommand empty() {
        return new UserCommand("");
    }

    @Override
    public String toString() {
        return this.command;
    }
}
