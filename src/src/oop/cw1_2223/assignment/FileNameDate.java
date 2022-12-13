package src.oop.cw1_2223.assignment;

import java.io.File;
import static src.oop.cw1_2223.assignment.MyMiscellaneous.parseDateFromFilename;

public class FileNameDate extends DateComputer implements DateFinder {
    public FileNameDate() {
        super();
    }
    private DateComputer dateDestination = null;
    @Override
    public DateComputer getDate(File file) {
            className = "Filename Date";
            dateDestination = parseDateFromFilename(file.getName());
            return dateDestination;
    }

}
