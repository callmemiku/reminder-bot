package com.miku.reminder.bot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

import java.util.function.BiConsumer

@Component
class BotExt extends TelegramLongPollingBot implements ICommandRegistry {

    private final CommandRegistry commandRegistry

    @Value('${bot.token}')
    String BOT_TOKEN
    @Value('${bot.name}')
    String BOT_NAME

    private Message lastMessage

    @Autowired
    List<BotCommand> commandList

    BotExt() {
        this(new DefaultBotOptions())
    }

    BotExt(DefaultBotOptions options) {
        this(options, true)
    }

    BotExt(DefaultBotOptions options, boolean allowCommandsWithUsername) {
        super(options)
        this.commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername)
    }

    @Override
    String getBotToken() {
        return BOT_TOKEN
    }

    @Override
    void onUpdateReceived(Update update) {
        lastMessage = update.getMessage()

    }

    @Override
    String getBotUsername() {
        return BOT_NAME
    }

    @Override
    void registerDefaultAction(BiConsumer<AbsSender, Message> biConsumer) {

    }

    @Override
    boolean register(IBotCommand iBotCommand) {
        return false
    }

    @Override
    Map<IBotCommand, Boolean> registerAll(IBotCommand... iBotCommands) {
        return null
    }

    @Override
    boolean deregister(IBotCommand iBotCommand) {
        return false
    }

    @Override
    Map<IBotCommand, Boolean> deregisterAll(IBotCommand... iBotCommands) {
        return null
    }

    @Override
    Collection<IBotCommand> getRegisteredCommands() {
        return null
    }

    @Override
    IBotCommand getRegisteredCommand(String s) {
        return null
    }
}
