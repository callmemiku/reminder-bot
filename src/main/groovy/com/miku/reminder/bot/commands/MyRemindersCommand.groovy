package com.miku.reminder.bot.commands

import com.miku.reminder.entity.Reminder
import com.miku.reminder.service.ReminderService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

import java.time.LocalDateTime
import java.util.stream.Collectors

@Slf4j
@Component
class MyRemindersCommand extends BotCommand {
    @Autowired
    private ReminderService reminderService
    @Override
    void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage sendMessage = new SendMessage()
        sendMessage.setChatId(user.getId() as String)
        def reminderList = ""
        reminderService.getMain()
                .values()
                .flatten()
                .findAll(rem  -> (rem as Reminder).userId == user.getId())
                .forEach(x -> reminderList = reminderList + toStr.call(x))
        if (reminderList.isEmpty())
            sendMessage.setText("Напоминаний не найдено!")
        else sendMessage.setText("Ваши напоминания:\n" + reminderList)
        absSender.execute(sendMessage)
    }
    MyRemindersCommand() {
        super("my", "Список ваших напоминаний.")
    }


    def toStr = {x -> return "\tНомер напоминания: ${x.id}\n" +
                "\t\t\tТекст напоминания: \t\t${x.msg}\n" +
                "\t\t\tДата: \t\t${x.date.format(reminderService.getDateFormatter())}\n\n"}

}
