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
                if (source.length() > 1){
                    if (source.substring(source.length() - 2) == "11")
                        return "минут"
                }
                return "минута"
            }
                case "3":
                case "4":
                case "2" :{
                    if (source.length() > 1){
                        if (source.substring(source.length() - 2) == "12" || source.substring(source.length() - 2) == "13" || source.substring(source.length() - 2) == "14")
                            return "минут"
                    }
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
                List<Long> remindersIds = new ArrayList<>();
                map.get(x).stream().forEach(value -> {
                    SendMessage sendMessage = new SendMessage()
                    sendMessage.setChatId(value.userId as String)
                    def duration = Duration.between(now, value.date).get(ChronoUnit.SECONDS)/60 as Integer
                    if (duration == 0) {
                        remindersIds.add(value.id)
                        sendMessage.setText("Срок для ${value.msg} пришёл!")
                    } else {
                        sendMessage.setText(duration < 5 ?
                                "⚠⚠⚠ Вы просили напомнить о ${value.msg}, осталось ${duration} ${minute.call(duration)}! ⚠⚠⚠" :
                                "Просили напомнить? Напоминаю! До ${value.msg} осталось ${duration} ${minute.call(duration)}!")
                    }
                    bot.execute(sendMessage)
                })
                remindersIds.stream().forEach(id -> {
                    map.put(x, map.get(x).stream().filter(reminder -> !remindersIds.contains(reminder.id)).collect(Collectors.toList()))
                })
                Thread.sleep(1000 * 60 * 1)
            })
        }
    }
}
