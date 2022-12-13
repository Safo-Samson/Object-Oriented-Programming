package src.oop.cw1_2223.assignment;

import java.io.File;

public class DateComputer{
    protected int year;
    protected int month;
    protected int day;
    protected String className;

    public DateComputer(int year, int month, int day, String className){
        this.year = year;
        this.month = month;
        this.day = day;
        this.className = className;
    }
    public DateComputer() {}

    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public String getClassName(){
        return className;
    }

}
