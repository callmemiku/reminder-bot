package com.miku.reminder.service

import java.time.format.DateTimeFormatter

trait CouldFormatDate {
    DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm, EEEE, d MMMM")
    }
}