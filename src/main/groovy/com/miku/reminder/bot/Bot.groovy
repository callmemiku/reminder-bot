package com.miku.reminder.bot

import com.miku.reminder.bot.commands.AddReminderCommand
import com.miku.reminder.bot.commands.HelpCommand
import com.miku.reminder.bot.commands.MyRemindersCommand
import com.miku.reminder.bot.commands.OkCommand
import com.miku.reminder.utils.NonCommandMessage
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
@Slf4j
class Bot extends TelegramLongPollingCommandBot {

    @Value('${bot.token}')
    String BOT_TOKEN
    @Value('${bot.name}')
    String BOT_NAME

    @Autowired
    private AddReminderCommand addReminderCommand

    @Override
    void processNonCommandUpdate(Update update) {
        Message message = update.getMessage()
        def id = message.getChatId()
        def username = getUserName(message)
        answer(id, username, NonCommandMessage.handle())
    }

    def answer(Long id, String username, String text) {
        def answer = new SendMessage()
        answer.setChatId(id as String)
        answer.setText(text)
        try {
            execute(answer)
        } catch (TelegramApiException e) {
            log.debug(String.format("EXCEPTION: couldn't answer to $username @ ${new Date()}"))
        }
    }

    private String getUserName(Message msg) {
        def user = msg.getFrom()
        def userName = user.getUserName()
        return userName?: "${user.getLastName()} ${user.getFirstName()}"
    }

    @Autowired
    Bot(AddReminderCommand addReminderCommand, OkCommand echoCommand, MyRemindersCommand myRemindersCommand, HelpCommand helpCommand){
        register(addReminderCommand)
        register(echoCommand)
        register(myRemindersCommand)
        register(helpCommand)
    }

    @Override
    String getBotUsername() {
        return BOT_NAME
    }
    @Override
    String getBotToken() {
        return BOT_TOKEN
    }
}
