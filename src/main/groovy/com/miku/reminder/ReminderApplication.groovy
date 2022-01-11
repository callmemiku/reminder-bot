package com.miku.reminder

import com.miku.reminder.bot.Bot
import com.miku.reminder.reminder.RemindInvoker
import com.miku.reminder.service.ReminderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


@SpringBootApplication
class ReminderApplication {
    @Autowired
    private Bot bot
    @Autowired
    ReminderService reminderService
    @Autowired
    RemindInvoker remindInvoker
    static void main(String[] args) {
        SpringApplication.run(ReminderApplication, args)
    }

    @EventListener(ContextRefreshedEvent.class)
    initBot(){
        try {
            Thread thread = new Thread(remindInvoker)
            thread.setDaemon(true)
            thread.start()
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class)
            botsApi.registerBot(bot)
        } catch (TelegramApiException e) {
            e.printStackTrace()
        }
    }
}
