package src.oop.cw1_2223.assignment;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FileTimestamp extends DateComputer {

    static DateComputer dateDestination = null;

    public FileTimestamp(int year, int month, int day, String className) {
        super(year, month, day, className);
    }

    public static DateComputer getDate(File file) {
    long timestamp = file.lastModified();
    ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.systemDefault());
        year = zdt.getYear();
        month = zdt.getMonthValue();
        day = zdt.getDayOfMonth();
        className = "File TimeStamp CREATED";
    dateDestination =  new FileNameDate(year,month,day,className);
    return dateDestination;
    }

}

