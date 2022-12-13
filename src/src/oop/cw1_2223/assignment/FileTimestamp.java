package src.oop.cw1_2223.assignment;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class FileTimestamp extends DateComputer implements DateFinder {
    public FileTimestamp() {
        super();
    }
    private DateComputer dateDestination = null;
    @Override
    public DateComputer getDate(File file) {
    long timestamp = file.lastModified();
    ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.systemDefault());
        year = zdt.getYear();
        month = zdt.getMonthValue();
        day = zdt.getDayOfMonth();
        className = "FileTimeStamp";
    dateDestination =  new DateComputer(year,month,day,className);
    return dateDestination;
    }

}

