package com.example.tourmatebatch14.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class EventUtils {
    public static final String WEATHER_ICON_URL_PREFIX = "http://openweathermap.org/img/wn/";
    public static String getCurrentDateTime(){

        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    public static String getFormatedDate(Long dt){
        Date date = new Date(dt*1000);
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    }
}
