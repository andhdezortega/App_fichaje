package com.mycompany.controlfichaje.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }
    
    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }
    
    public static LocalDate parseDate(String date) {
        return date != null && !date.trim().isEmpty() ? LocalDate.parse(date) : null;
    }
    
    public static LocalTime parseTime(String time) {
        return time != null && !time.trim().isEmpty() ? LocalTime.parse(time) : null;
    }
}