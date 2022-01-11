package com.miku.reminder.reminder

import com.miku.reminder.bot.Bot
import com.miku.reminder.entity.Reminder
import com.miku.reminder.service.ReminderService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

@Component
@Slf4j
class RemindInvoker implements Runnable {

    @Autowired
    private ReminderService reminderService
    @Autowired
    private Bot bot
    def minute = {x -> {
        def source = x as String
        switch (source.substring(source.length() - 1)){
            case "1": {
                return "минута"
            }
                case "2" || "3" || "4": {
                return "минуты"
            }
                default: {
                return "минут"
            }
        }
    }}
    @Override
    void run() {
        log.debug("daemon invoker started!")
        while (true){
            def now = LocalDateTime.now()
            Map<LocalDateTime, List<Reminder>> map = reminderService.getMain()
            def valid = map.keySet().stream()
                    .filter(x -> x.isBefore(now.plusMinutes(30)))
                    .collect(Collectors.toSet())
            valid.forEach(x -> {
                map.get(x).forEach(value -> {
                    SendMessage sendMessage = new SendMessage()
                    def duration = Duration.between(now, value.date).get(ChronoUnit.SECONDS)/60 as Integer
                    if (duration == 0)
                        map.remove(value)
                    sendMessage.setText(duration < 5 ? "⚠⚠⚠ Вы просили напомнить о ${value.msg}, осталось: ${duration} ${minute.call(duration)}! ⚠⚠⚠" : "Просили напомнить? Напоминаю! До ${value.msg} осталось ${duration} ${minute.call(duration)}!")
                    sendMessage.setChatId(value.userId as String)
                    bot.execute(sendMessage)
                })
                Thread.sleep(1000 * 60 * 1)
            })
        }
    }
}
