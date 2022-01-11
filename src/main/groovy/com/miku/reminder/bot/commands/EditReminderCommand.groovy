package com.miku.reminder.bot.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class EditReminderCommand extends BotCommand {
    @Override
    void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        
    }

    EditReminderCommand(){
        super("edit", "edit")
    }
}
