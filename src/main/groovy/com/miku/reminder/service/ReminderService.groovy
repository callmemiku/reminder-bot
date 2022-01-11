package com.miku.reminder.service

import com.miku.reminder.entity.Reminder
import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
interface ReminderService extends CouldFormatDate {
    executeAddR(Long id, String[] strings)
    Map<LocalDateTime, List<Reminder>> getMain()
}