package com.miku.reminder.controller

import com.miku.reminder.service.ReminderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    @Autowired
    private ReminderService reminderService;
    @PostMapping("/request")
    healthCheck(@RequestBody String request){
        return "{\"status\": \"ok\"}"
    }
}
