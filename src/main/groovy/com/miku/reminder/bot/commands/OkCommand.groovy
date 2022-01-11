package com.miku.reminder.bot.commands

import com.miku.reminder.entity.Reminder
import com.miku.reminder.service.ReminderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

import java.time.LocalDateTime

@Component
class OkCommand extends BotCommand {
    @Autowired
    private ReminderService reminderService
    OkCommand() {
        super("ok", "Убрать напоминание")
    }

    @Override
    void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        def msg = new SendMessage()
        msg.setChatId(chat.getId() as String)
        def date
        try {
            def id = Integer.parseInt(strings[0])
            def removed = false
            Map<LocalDateTime, List<Reminder>> map = reminderService.getMain()
            for (List<Reminder> list: map.values()){
                def size = list.size()
                list.removeIf(x -> {
                    date = x.date.format(reminderService.getDateFormatter())
                    x.id == id as long && x.userId == user.getId()})
                if (size != list.size())
                    removed = true
            }
            if (!removed)
                throw new Exception("no such element")
            //todo fix date from i + 1
            msg.setText("Убрал напоминание №${strings[0]} на ${date}.")
        }
        catch (Exception e) {
            msg.setText("Не нашел по этому номеру ничего. Возможно, опечатка)")
        }
        absSender.execute(msg)
    }
}
