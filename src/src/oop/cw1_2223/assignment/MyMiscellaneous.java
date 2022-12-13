package src.oop.cw1_2223.assignment;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


public class MyMiscellaneous {

    public static DateComputer computeDateDestination(File file, boolean useExifolderDate, boolean useFilenameDate, boolean useFileTimestamp) {
        DateComputer dateDestination = null;
        if (useExifolderDate) {
             ExifDate ex = new ExifDate();
             dateDestination = ex.getDate(file);
        }

        if (dateDestination == null && useFilenameDate) {
            FileNameDate fn = new FileNameDate();
            dateDestination = fn.getDate(file);
        }
        if (dateDestination == null && useFileTimestamp) {
            FileTimestamp ft = new FileTimestamp();
            dateDestination = ft.getDate(file);
        }
        return dateDestination;
    }

    public static void process(File file,boolean useExiffolderDate, boolean useFilenameDate, boolean useFileTimestamp, FoldersDate folderDate,
                               File outputFolder, boolean simulation, Set<File> duplicateDetector) throws IOException {
        DateComputer dateDestination = computeDateDestination(file,useExiffolderDate, useFilenameDate,useFileTimestamp);
        if (dateDestination != null) {
            //System.out.println("Date determined for '" + file + "' " + DateComputer.year + "-" + DateComputer.month + "-" + DateComputer.day + " (" + DateComputer.className + ")");
            System.out.println("Date determined for '" + file + "' " + dateDestination.getYear() + "-" + dateDestination.getMonth() + "-" + dateDestination.getDay() + " (" + dateDestination.getClassName() + ")");

            File destinationFolder = folderDate.getDestinationFolder(dateDestination);

            File outputDestinationFolder = new File(outputFolder, destinationFolder.getPath());
            File destinationFilename = new File(outputDestinationFolder, file.getName());

            if (simulation) {
                if (duplicateDetector.add(destinationFilename)) {
                    System.out.println("[SIMULATING] Renaming '" + file + "' to '" + destinationFilename + "'");
                } else {
                    System.out.println("[SIMULATING] Did not rename '" + file + "' to '" + destinationFilename + "' as destination file exists.");
                }
            } else {
                ensureFolderExists(outputDestinationFolder);
                if (!destinationFilename.exists()) {
                    if (file.renameTo(destinationFilename)) {
                        // issue confirmation of file move
                        System.out.println("Renamed '" + file + "' to '" + destinationFilename + "'");
                    } else {
                        // issue warning that file move failed
                        System.out.println("Failed to rename '" + file + "' to '" + destinationFilename + "'");
                    }
                } else {
                    // an attempt to move second file to same name in same date folder
                    // issue warning and do not move
                    System.out.println("Did not rename '" + file + "' to '" + destinationFilename + "' as destination file exists.");
                }
            }
        } else {
            // issue warning that no date could be determined for file
            System.out.println("Could not determine date for '" + file + "'.");
        }
    }


    protected static DateComputer parseDateFromFilename(final String filename) {
        final List<LocalDate> possibles = new ArrayList<>();
        final ArrayList<String> fragments = new ArrayList<String>();
        splitByWordsAndNumbers( filename, fragments, false);
        for (int i = 0; i < fragments.size(); i++) {
            final String fragment = fragments.get(i);
            final char ch = fragment.charAt(0);
            boolean keep = true;
            if (Character.isLetter(ch)) {
                final int mi = identifyMonthName(fragment);
                if (mi < 0) {
                    // not a month, discard fragment
                    keep = false;
                } else {
                    // month name identified; replace with number
                    fragments.set(i, String.valueOf(mi + 1));
                }
            } else if (!Character.isDigit(ch)) {
                keep = false;
            } else if (ch == '0') {
                // discard strings consisting only of zeroes
                keep = trim( fragment, "0", true, false).length() != 0;
            }
            if (!keep) {
                fragments.remove(i); i--;
            }
        }
        // we now only have digit fragments
        // first, look for eight digit fragments
        for (int i = 0; i < fragments.size(); i++) {
            final String fragment = fragments.get(i);
            if (fragment.length() == 8) {
                try {
                    LocalDate date = LocalDate.parse(fragment, DateTimeFormatter.BASIC_ISO_DATE);
                    possibles.add(date);
                } catch( final DateTimeParseException x) {}
            }
        }
        // now look for triplets that form valid dates
        String[] temp = new String[3];
        for (int i = 0; i < fragments.size() - 2; i++) {
            fragments.subList(i, i + 3).toArray(temp);
            LocalDate date = tryDate(temp, 0, 1, 2); // year month day
            if (date != null) {
                possibles.add(date);
            }
            date = tryDate(temp, 2, 1, 0); // day month year
            if (date != null) {
                possibles.add(date);
            }
        }
        if (possibles.size() == 1) {
            LocalDate date = possibles.get(0);
            return new DateComputer(date.getYear(), date.getMonthValue(),
                    date.getDayOfMonth(),"FileName Date");
        } else {
            return null;
        }
    }

    public static int identifyMonthName(String fragment) {
        fragment = fragment.toLowerCase();
        String[] names = new String[] {
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December" };
        for (int i = 0; i < names.length; i++) {
            String name = names[i].toLowerCase();
            if (fragment.length() == 3) {
                name = name.substring(0,3);
            }
            if (name.equals(fragment)) {
                return i;
            }
        }
        return -1;
    }

    public static String trim( final CharSequence target, final String chars, final boolean leading, final boolean trailing) {
        final StringBuilder builder = new StringBuilder( target);
        if ( leading) {
            while( (builder.length() > 0) && (chars.indexOf( builder.charAt(0)) != -1)) { builder.delete(0, 1); }
        }
        if ( trailing) {
            while( (builder.length() > 0) && (chars.indexOf( builder.charAt(builder.length() - 1)) != -1)) { builder.delete(builder.length() - 1, builder.length()); }
        }
        return builder.toString();
    }

    public static int splitByWordsAndNumbers(final CharSequence target, final Collection<String> toAddTo, final boolean allowPointInNumbers) {
        final StringBuilder builder = new StringBuilder();
        final int LETTERS = 2;
        final int DIGITS = 1;
        final int OTHER = 0;
        int gatheringMode = OTHER;
        int count = 0;
        int index = 0;
        while( index < target.length()) {
            final char c = target.charAt( index++);
            if ( Character.isLetter( c)) {
                switch ( gatheringMode) {
                    case LETTERS:
                        builder.append(c);
                        break;
                    case DIGITS:
                    case OTHER:
                        if ( builder.length() > 0) {
                            toAddTo.add( builder.toString());
                            count++;
                        }
                        builder.setLength( 0);
                        builder.append( c);
                        gatheringMode = LETTERS;
                        break;
                    default:
                        assert( false);
                        break;
                }
            } else if ( Character.isDigit( c) ||( allowPointInNumbers && ( c == '.'))) {
                switch ( gatheringMode) {
                    case DIGITS:
                        builder.append(c);
                        break;
                    case LETTERS:
                    case OTHER:
                        if ( builder.length() > 0) {
                            toAddTo.add( builder.toString());
                            count++;
                        }
                        builder.setLength( 0);
                        builder.append( c);
                        gatheringMode = DIGITS;
                        break;
                    default:
                        assert( false);
                        break;
                }
            } else {
                switch ( gatheringMode) {
                    case DIGITS:
                    case LETTERS:
                        if ( builder.length() > 0) {
                            toAddTo.add( builder.toString());
                            count++;
                        }
                        builder.setLength( 0);
                        builder.append( c);
                        gatheringMode = OTHER;
                        break;
                    case OTHER:
                        builder.append(c);
                        break;
                    default:
                        assert( false);
                        break;
                }
            }
        }
        if ( builder.length() > 0) {
            toAddTo.add( builder.toString());
            count++;
        }
        return count;
    }

    private static LocalDate tryDate(String[] fragments, int year, int month, int day) {
        for (int j = 0; j < fragments.length; j++) {
            fragments[j] = trim( fragments[j], "0", true, false);
        }
        if (fragments[year].length() == 4 && fragments[month].length() <= 2 && fragments[day].length() <= 2) {
            final int[] n = new int[3];
            for(int j = 0; j < 3; j++) {
                n[j] = Integer.parseInt( fragments[j]);
            }
            try {
                return LocalDate.of(n[year], n[month], n[day]);
            } catch(final DateTimeException x) {
                return null;
            }
        }
        return null;
    }
    public static void ensureFolderExists( final File folder) throws IOException {
        if (folder.exists()) {
            if (folder.isDirectory()) {
                return;
            } else {
                throw new IOException( "Folder is actually a file: " + folder);
            }
        } else {
            ensureFolderExists(folder.getParentFile());
            if (folder.mkdir()) {
                return;
            } else {
                throw new IOException( "Could not create folder: " + folder);
            }
        }
    }
}
