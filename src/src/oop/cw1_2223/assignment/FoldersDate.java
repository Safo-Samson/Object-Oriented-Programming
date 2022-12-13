package src.oop.cw1_2223.assignment;

import java.io.File;

public class FoldersDate {
    private boolean useYearFolders ;
    private boolean useYearAndMonthFolders;
    private boolean useYearMonthDayFolders;

    public FoldersDate(boolean useYearFolders, boolean useYearAndMonthFolders, boolean useYearMonthDayFolders) {
        this.useYearFolders = useYearFolders;
        this.useYearAndMonthFolders = useYearAndMonthFolders;
        this.useYearMonthDayFolders = useYearMonthDayFolders;
    }

    public File getDestinationFolder(DateComputer date) {
        File parent = null;

        if (useYearFolders) {
            parent = new File(parent, String.format("%04d", date.getYear()));
        }
        if (useYearAndMonthFolders) {
            parent = new File(parent, String.format("%04d", date.getYear()) + "-" + String.format("%02d", date.getMonth()));
        }
        if (useYearMonthDayFolders) {
            parent = new File(parent, String.format("%04d", date.getYear()) + "-" + String.format("%02d", date.getMonth())
                    + "-" + String.format("%02d", date.getDay()));
        }

        return parent;
    }

}
