package src.oop.cw1_2223.assignment;

import com.drew.imaging.ImageProcessingException;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ExifDate extends DateComputer implements DateFinder {
    public ExifDate() {
        super();
    }
    private DateComputer dateDestination = null;
    @Override
    public DateComputer getDate(File file) {
                {
                    try {
                        java.util.Date date = ExifUtils.getFirstDate(ExifUtils.getMetaData(file));
                        if (date != null) {
                            ZonedDateTime zdt = date.toInstant().atZone(ZoneId.systemDefault());//.toLocalDate();
                            year = zdt.getYear();
                            month = zdt.getMonthValue();
                            day = zdt.getDayOfMonth();
                            className = "Exif data";
                           dateDestination = new DateComputer(year,month,day,className);
                        }
                    } catch (ImageProcessingException | IOException e){
                                 System.out.println(e);}
                }
                return dateDestination;
        }

}
