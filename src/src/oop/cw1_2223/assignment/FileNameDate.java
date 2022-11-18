package src.oop.cw1_2223.assignment;

import java.io.File;
import static src.oop.cw1_2223.assignment.MyMiscellaneous.parseDateFromFilename;



public class FileNameDate extends DateComputer {

    private static DateComputer dateDestination = null;

    public FileNameDate(int year, int month, int day, String className) {
        super(year, month, day, className);
    }

    public static DateComputer getDate(File file) {
        className = "FileName Date";
            dateDestination = parseDateFromFilename(file.getName());
            return dateDestination;
    }



}
