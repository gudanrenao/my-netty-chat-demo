package com.zwenit.chat.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wen.zhang
 * @date 2020/8/24 13:47
 **/
public class PrintUtils {

    public static void printWithTime(String msg){
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ": " + msg);
    }
}
