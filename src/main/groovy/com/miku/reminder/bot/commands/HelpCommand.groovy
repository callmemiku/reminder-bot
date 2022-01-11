package com.miku.reminder.bot.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
@Component
class HelpCommand extends BotCommand {
    @Override
    void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage sendMessage = new SendMessage()
        sendMessage.setChatId(chat.getId() as String)
        def text = "Помощь по коммандам:\n\t\t\tКоманда add - добавить напоминалку в формате ДЕНЬ.МЕСЯЦ.ГОД ЧАС:МИНУТА:СЕКУНДА, сообщение, которое хочешь себе оставить. Дату можно указывать в произвольном формате, есть поддержка ключевых слов 'завтра', 'неделя', 'месяц'. Также возможно указать время боту через предлог 'на' и время. Разделение даты и сообщения запятой ОБЯЗАТЕЛЬНО.\n" +
                "\t\t\tКоманда ok - удалить напоминалку по её ID.\n\t\t\tКоманда my - получить список ваших напоминалок."
        sendMessage.setText(text)
        absSender.execute(sendMessage)
    }
    HelpCommand() {
        super("help", "Показать помощь.")
    }
}
