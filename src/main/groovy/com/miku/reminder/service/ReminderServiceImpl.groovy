package com.miku.reminder.service

import com.miku.reminder.entity.Reminder
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap

@Service
class ReminderServiceImpl implements ReminderService {

    private final ConcurrentHashMap<LocalDateTime, List<Reminder>> main = new ConcurrentHashMap<>()
    private count = 0
    private def dayDDMMMMYYYY = "dd.MM.yyyy"
    private def fullPattern = "dd.MM.yy"

    private def patternTime = "dd.MM HH:mm:ss"
    private def fullPatternTime = "dd.MM.yy HH:mm:ss"
    private def timeHHMMSS = "HH:mm:ss"
    private def timeHHmm = "HH:mm"
    private def timeHH = "HH"

    def timeMatch = {String time -> {
        return time.matches('[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}?') || time.matches('[0-9]{1,2}:[0-9]{1,2}?') || time.matches('[0-9]{1,2}?')
    }}
    def dayMatch = {String time -> {
        return time.matches('[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,4}?') || time.matches('[0-9]{1,2}\\.[0-9]{1,2}?') || time.matches('[0-9]{1,2}?')
    }}

    def executeAddR(Long id, String[] strings) {
        Reminder result = new Reminder()
        result.setId(genId())
        result.setUserId(id)
        def buildAString = {x -> def str = ""; for (String s: x) str = str + s + " "; return str}
        String tempData = buildAString.call(strings)
        result.date = findDate(tempData.split(",")[0])
        if (result.date.isBefore(LocalDateTime.now())){
            return "Твоя дата в прошлом. Я бы и рад напомнить, но уже ничего не поделать."
        }
        if (result.date.year == 9488){
            return "Неверный формат даты, попробуй ещё раз."
        }
        result.msg = buildAString.call({ def temp = tempData.split(",")
                    temp[0] = null
                    temp = temp.findAll({x -> x != null})
                    return temp}.call()).trim()

        main.compute(result.date, (_, v) -> {
            v?.add(result)?:new ArrayList<Reminder>(Arrays.asList(result))
        })
        return "ID: ${result.id}, напомню в ${result.date.format(getDateFormatter())}"
    }

    def dateByKey(time) {
        if (timeMatch.call(time)){
            return parseTime(time)
        } else {
            LocalDateTime localDateTime = LocalDateTime.now()
            def hour = getHourByKeyWord(time)
            return localDateTime.withHour(hour).withMinute(0).withSecond(0)
        }
    }

    def parseTime(String time) {
        def temp = time.split(":")
        Date date
        switch (temp.length) {
            case 1: {
                date = Date.parse(timeHH, time)
                break
            }
                case 2: {
                date = Date.parse(timeHHmm, time)
                break
            }
                case 3: {
                date = Date.parse(timeHHMMSS, time)
                break
            }
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
    }

    def parseDay(String time) {
        def temp = time.split("\\.")
        Date date
        def tempTime = LocalDateTime.now()
        switch (temp.length) {
            case 1: {
                time = "$time.${tempTime.monthValue}.${tempTime.year}"
                date = Date.parse(dayDDMMMMYYYY, time)
                break
            }
            case 2: {
                date = Date.parse(dayDDMMMMYYYY, "$time.${tempTime.year}")
                break
            }
            case 3: {
                date = Date.parse(dayDDMMMMYYYY, time)
                break
            }
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
    }

    def parseDate(String date) {
        def arr = date.split("\\s")
        LocalDateTime dayC, timeC
        if (arr.length == 2) {
            def day = arr[0]
            def time = arr[1]
            if (timeMatch.call(time))
                timeC = parseTime(time)
            if (dayMatch.call(day))
                dayC = parseDay(day)
        } else if (arr.length == 1){
            if (date.matches('[0-9]{1,2}')) {
                dayC = parseDay(date)
                timeC = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0)
            } else if (timeMatch.call(date)) {
                timeC = parseTime(date)
                dayC = LocalDateTime.now()
            } else if (dayMatch.call(date)){
                dayC = parseDay(date)
                timeC = LocalDateTime.now().withHour(8).withMinute(0).withSecond(0)
            } else return LocalDateTime.now().withYear(9488)
        }
        return dayC?.withHour(timeC?.hour?:8)?.withMinute(timeC?.minute?:0)?.withSecond(timeC?.second?:0)?:
                LocalDateTime.now().withHour(timeC?.hour?:8).withMinute(timeC?.minute?:0).withSecond(timeC?.second?:0)
    }

    def findDate(String source) {
        def time = source?.toLowerCase()
        def date
        switch (time) {
            case ~/.*завтра.*/: {
                date = LocalDateTime.now().plusDays(1)
                break
            }
            case ~/.*недел.*/: {
                date = LocalDateTime.now().plusWeeks(1)
                break
            }
            case ~/.*месяц.*/: {
                date = LocalDateTime.now().plusMonths(1)
                break
            }
            case ~/.*через.*/:{
                if (!time.contains("минут")) {
                    date = LocalDateTime.now().plusHours(getDateThrough(time.split("\\s")[1].toLowerCase()))
                } else {
                    def temp = time.replaceAll('^[0-9]', "")
                    date = LocalDateTime.now().plusMinutes(Integer.parseInt(temp?:"0"))
                }
                break
            }
            case ~/.*к.*/:
            case ~/.*на.*/: {
                def stime = LocalDateTime.now()
                def temp = dateByKey(time.split("\\s")[1].toLowerCase())
                def atime = stime.withHour(temp.hour).withMinute(temp.minute?:0).withSecond(temp.second?:0)
                date = atime.isBefore(stime) ? atime.plusDays(1) : atime
                break
            }
            default: {
                date = parseDate(time)
            }
        }
        return date
    }

    @Override
    Map<LocalDateTime, List<Reminder>> getMain() {
        return main
    }

    Long genId(){
        return ++count
    }

    def getHourByKeyWord(time){
        def hour = 0
        switch (time) {
            case "два": {
                hour = 14
                break
            }
            case "три": {
                hour = 15
                break
            }
            case "четыре": {
                hour = 16
                break
            }
            case "пять": {
                hour = 17
                break
            }
            case "час": {
                hour = 13
                break
            }
            case "шесть": {
                hour = 18
                break
            }
            case "семь": {
                hour = 19
                break
            }
            case "восемь": {
                hour = 20
                break
            }
            case "девять": {
                hour = 21
                break
            }
            case "десять": {
                hour = 22
                break
            }
            case "одиннадцать": {
                hour = 23
                break
            }
            case "двенадцать": {
                hour = 12
                break
            }
            default: hour = 0
        }
        return hour
    }

    def getDateThrough(String time) {
        def hour
        switch (time) {
            case "два": {
                hour = 2
                break
            }
            case "три": {
                hour = 3
                break
            }
            case "четыре": {
                hour = 4
                break
            }
            case "пять": {
                hour = 5
                break
            }
            case "час": {
                hour = 1
                break
            }
            case "шесть": {
                hour = 6
                break
            }
            case "семь": {
                hour = 7
                break
            }
            case "восемь": {
                hour = 8
                break
            }
            case "девять": {
                hour = 9
                break
            }
            case "десять": {
                hour = 10
                break
            }
            case "одиннадцать": {
                hour = 11
                break
            }
            case "двенадцать": {
                hour = 12
                break
            }
            default: hour = 0
        }
        return hour
    }
}
