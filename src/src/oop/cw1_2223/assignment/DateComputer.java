package src.oop.cw1_2223.assignment;

import java.io.File;

public class DateComputer {
    protected static int year;
    protected static int month;
    protected static int day;
    protected static String className;

    public DateComputer(int year, int month, int day, String className){
        DateComputer.year = year;
        DateComputer.month = month;
        DateComputer.day = day;
        DateComputer.className = className;
    }

    public static DateComputer getDate(File file){
        return new DateComputer(0,0,0,null);
    }

    public static int getYear() {
        return year;
    }

    public static int getMonth() {
        return month;
    }

    public static int getDay() {
        return day;
    }


}
