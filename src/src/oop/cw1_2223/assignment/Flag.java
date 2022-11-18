package src.oop.cw1_2223.assignment;

public class Flag {

    private boolean recurse;
    private boolean useExifDate;
    private boolean useFilenameDate;
    private boolean useFileTimestamp ;

    public Flag(boolean recurse, boolean useExifDate, boolean useFilenameDate, boolean useFileTimestamp) {
        this.recurse = recurse;
        this.useExifDate = useExifDate;
        this.useFilenameDate = useFilenameDate;
        this.useFileTimestamp = useFileTimestamp;
    }


    public  boolean useRecurse(){return recurse;}
    public  boolean useExifDate(){return useExifDate;}
    public  boolean useFilenameDate(){return useFilenameDate;}
    public  boolean useFileTimestamp(){return useFileTimestamp;}

}
