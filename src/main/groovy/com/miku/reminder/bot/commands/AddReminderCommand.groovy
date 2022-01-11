package com.miku.reminder.bot.commands

import com.miku.reminder.service.ReminderService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Slf4j
@Component
class AddReminderCommand extends BotCommand {

    @Autowired
    private ReminderService reminderService

    AddReminderCommand(){
        super("add", "Добавить напоминание")
    }

    @Override
    void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        def username = user.getUserName()
        try {
            //todo hour > 24
            SendMessage message = new SendMessage()
            message.setChatId(chat.getId() as String)
            message.setText(reminderService.executeAddR(chat.getId(), strings) as String)
            absSender.execute(message)
        } catch (IllegalArgumentException e) {
            sendError(absSender, chat.getId(), username)
            e.printStackTrace()
        } catch (TelegramApiException e) {
            log.debug("EXCEPTION: couldn't answer to $username @ ${new Date()}")
        }
    }

    private void sendError(AbsSender absSender, Long chatId, String userName) {
        try {
            absSender.execute(new SendMessage(chatId.toString(), "Похоже, я сломался. Попробуйте позже"));
        } catch (TelegramApiException e) {
            log.debug("EXCEPTION: couldn't answer to $userName @ ${new Date()}")
        }
    }
}
