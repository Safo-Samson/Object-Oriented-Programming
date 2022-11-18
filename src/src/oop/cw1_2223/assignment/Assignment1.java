package src.oop.cw1_2223.assignment;

import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.drew.imaging.ImageProcessingException;

public class Assignment1 {
  
  private static List<File> targetFolders = new ArrayList<>();
  private static List<Boolean> targetFolderRecursion = new ArrayList<>();
  private static List<Boolean> targetFolderUseExifDate = new ArrayList<>();
  private static List<Boolean> targetFolderUseFilenameDate = new ArrayList<>();
  private static List<Boolean> targetFolderUseFileTimestamp = new ArrayList<>();

  /**
   * Adds the given folder to the target folders list. Also adds entries to the 
   * target folder recursion list, target folder use EXIF list, target folder
   * use file name list, and the target folder use file timestamp list. This 
   * ensures that all these lists are the same length and have an entry for each
   * target folder.
   * 
   * @param targetFolder
   * @param recurse
   * @param useExifDate
   * @param useFilenameDate
   * @param useFileTimestamp
   */
  public static void addFolder(
      File targetFolder, 
      boolean recurse, 
      boolean useExifDate, boolean useFilenameDate, boolean useFileTimestamp) {
    targetFolders.add(targetFolder);
    targetFolderRecursion.add(recurse);
    targetFolderUseExifDate.add(useExifDate);
    targetFolderUseFilenameDate.add(useFilenameDate);
    targetFolderUseFileTimestamp.add(useFileTimestamp);
  }



  /**
   * Scans all the folders that have been added to the target folders list, determining
   * the appropriate destination date and moving the files to their new locations - unless
   * simulation is true, in which it just prints what moves would be made but does not
   * do it.
   * 
   * @param useYearFolders
   * @param useYearAndMonthFolders
   * @param useYearMonthDateFolders
   * @throws IOException
   */
  public static void scanFolders(
      File outputFolder, final boolean simulation,
      final boolean useYearFolders, final boolean useYearAndMonthFolders, final boolean useYearMonthDateFolders) throws IOException {   
    Set<File> duplicateDetector = new HashSet<>();
    for(int i = 0; i < targetFolders.size(); i++) {
      scan(i, useYearFolders, useYearAndMonthFolders, useYearMonthDateFolders, outputFolder, simulation, duplicateDetector);
    }
  }

  /**
   * Scans the files in the target folder identified by the index i in the target folder list.
   * 
   * @param i
   * @param useYearFolders
   * @param useYearAndMonthFolders
   * @param useYearMonthDateFolders
   * @param simulation
   * @param duplicateDetector
   * @throws IOException
   */
  private static void scan(
      int i, 
      final boolean useYearFolders, final boolean useYearAndMonthFolders, final boolean useYearMonthDateFolders,
      File outputFolder, boolean simulation, Set<File> duplicateDetector) throws IOException {
    List<File> files = getFiles(targetFolders.get(i), targetFolderRecursion.get(i));

    for(File file : files) {
      process(
          file, 
          targetFolderUseExifDate.get(i), targetFolderUseFilenameDate.get(i), targetFolderUseFileTimestamp.get(i), 
          useYearFolders, useYearAndMonthFolders, useYearMonthDateFolders,
          outputFolder, simulation, duplicateDetector);
    }
  }

  /**
   * Get a list of all files and folders in folder, recursing into subfolders if recurse is true.
   * 
   * @param folder
   * @param recurse
   * @return
   */
  public static List<File> getFiles(File folder, boolean recurse) {
    List<File> list = new ArrayList<File>();
    list.add(folder);
    int position = 0;
    while (position < list.size()) {
      File current = list.get(position);
      if (current.isDirectory()) {
        list.remove(position); // remove directories from the list to be returned
        if (list.size() == 0 || recurse) {
          File[] files = current.listFiles();
          if (files != null) {
            // files should not be null for a directory, but might be if we do not have read permission
            list.addAll(Arrays.asList(files)); // add contained files and directories to end of list
          }
        }
      } else {
        position++; // leave a file in the list and look at the next
      }
    }
    return list;
  }

  /**
   * Process and move the given file.
   * 
   * @param file
   * @param useExifDate
   * @param useFilenameDate
   * @param useFileTimestamp
   * @param useYearFolders
   * @param useYearAndMonthFolders
   * @param useYearMonthDateFolders
   * @param simulation
   * @param duplicateDetector
   * @throws IOException
   */
  private static void process(
      File file, boolean useExifDate, boolean useFilenameDate, boolean useFileTimestamp, 
      boolean useYearFolders, boolean useYearAndMonthFolders, boolean useYearMonthDateFolders,
      File outputFolder, boolean simulation, Set<File> duplicateDetector) throws IOException {

      Object[] dateDestination = computeDateDestination(file, useExifDate, useFilenameDate, useFileTimestamp);

    if (dateDestination != null) {
      System.out.println("Date determined for '" + file + "' " + dateDestination[0] + "-" + dateDestination[1] + "-" + dateDestination[2] + " (" + dateDestination[3] + ")");
      File destinationFolder = getDestinationFolder(dateDestination, useYearFolders, useYearAndMonthFolders, useYearMonthDateFolders);
      File outputDestinationFolder = new File(outputFolder, destinationFolder.getPath());
      File destinationFilename = new File(outputDestinationFolder, file.getName());

      if (simulation) {
        if (duplicateDetector.add(destinationFilename)) {
          System.out.println( "[SIMULATING] Renaming '" + file + "' to '" + destinationFilename + "'");          
        } else {
          System.out.println( "[SIMULATING] Did not rename '" + file + "' to '" + destinationFilename + "' as destination file exists.");           
        }
      } else {
        ensureFolderExists(outputDestinationFolder);
        if (!destinationFilename.exists()) {
          if (file.renameTo(destinationFilename)) {
            // issue confirmation of file move
            System.out.println( "Renamed '" + file + "' to '" + destinationFilename + "'");
          } else {
            // issue warning that file move failed
            System.out.println( "Failed to rename '" + file + "' to '" + destinationFilename + "'");
          }
        } else {
          // an attempt to move second file to same name in same date folder
          // issue warning and do not move
          System.out.println( "Did not rename '" + file + "' to '" + destinationFilename + "' as destination file exists.");        
        }
      }
    } else {
      // issue warning that no date could be determined for file        
      System.out.println( "Could not determine date for '" + file + "'.");
    }
  }

  /**
   * Attempts to determine the date to use for the given file, using some combination of looking 
   * at the EXIF data, parsing the file name and using the file timestamp. The Object array returned
   * will contain the year, month and day values as Integers and a String in the fourth element that
   * tells where the value were found. If no valid date can be determined, null is returned.
   * 
   * @param file
   * @param useExifDate
   * @param useFilenameDate
   * @param useFileTimestamp
   * @return
   */
  private static Object[] computeDateDestination(File file, boolean useExifDate, boolean useFilenameDate, boolean useFileTimestamp) {
    Object[] dateDestination = null;
    if (useExifDate) {
      try {
        java.util.Date date = ExifUtils.getFirstDate(ExifUtils.getMetaData(file));
        if (date != null) {
          ZonedDateTime zdt = date.toInstant().atZone(ZoneId.systemDefault());//.toLocalDate();
          dateDestination = new Object[] {zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), "EXIF data"};
        }
      } catch(IOException x) {
        System.out.println(x);
      } catch (ImageProcessingException x) {
        System.out.println(x);
      }
    }
    if (dateDestination == null && useFilenameDate) {
      dateDestination = parseDateFromFilename(file.getName());
    }
    if (dateDestination == null && useFileTimestamp) {
      long timestamp = file.lastModified();
      ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
      dateDestination = new Object[] { zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth(), "File timestamp" };
    }
    return dateDestination;
  }

  /**
   * Converts a YMD date held as Integer objects in the dateDestination array into a folder
   * path including levels of containing folders as specified by the other arguments.
   * For example, with all boolean arguments true, we might return the path 2020/2020-05/2020-05-02,
   * while with only useYearAndMonthFolders true we would just return 2020-05.
   * 
   * @param dateDestination
   * @param useYearFolders
   * @param useYearAndMonthFolders
   * @param useYearMonthDateFolders
   * @return
   */
  public static File getDestinationFolder( 
      final Object[] dateDestination,
      final boolean useYearFolders,
      final boolean useYearAndMonthFolders,
      final boolean useYearMonthDateFolders) {
    File parent = null;
    if (useYearFolders) {
      parent = new File(parent, String.format("%04d", (Integer)dateDestination[0]));
    }
    if (useYearAndMonthFolders) {
      parent = new File(parent, String.format("%04d", (Integer)dateDestination[0]) + "-" + String.format("%02d", (Integer)dateDestination[1]));
    }
    if (useYearMonthDateFolders) {
      parent = new File(parent, String.format("%04d", (Integer)dateDestination[0]) + "-" + String.format("%02d", (Integer)dateDestination[1])  + "-" + String.format("%02d", (Integer)dateDestination[2]));
    }
    return parent;
  }

  
  /**
   * Check a folder path exists and create it with all required folders if it does not.
   * <ul>
   * <li>If folder is an existing directory, returns.
   * <li>If folder does not exist, attempts to create it and throws an exception on failure.
   * <li>If folder is a file, throws an exception.
   * </ul>
   * If this method returns, folder exists and is a folder.
   *
   * @param folder
   * @throws IOException
   */
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

  /**
   * Try and find a single parseable date in the filename. If none can be found, 
   * or more than one can be found this returns null. Otherwise it returns an 
   * Object array containing the year, month and day as Integer objects in the 
   * first three elements, and the String "File name" in the fourth element, to
   * indicate the date value was found in the file name.
   *  
   * @param filename
   * @return
   */
  protected static Object[] parseDateFromFilename(final String filename) {
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
      return new Object[] { date.getYear(), date.getMonthValue(), date.getDayOfMonth(), "File name" };
    } else {
      return null;
    }
  }

  /**
   * The fragments string array is assumed to contain numerical strings, one representing
   * the year, one a month and one a day. Which is which is determined by the year, month
   * and day parameters (for example, if year = 0, then fragments[0] is used to determine
   * the year value). After discarding any leading zeros from the fragments an attempt is
   * made to construct a valid date from them, and if this succeeds that date will be 
   * returned. Otherwise, null will be returned. For example calling this with
   * ["2001", "05", "01"], 0, 1, 2 will return a date representing 01 May 2001.
   * On the other hand ["2001", "25", "01"], 0, 1, 2 will return null as 25 is not a valid
   * month.
   * 
   * @param fragments
   * @param year
   * @param month
   * @param day
   * @return
   */
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

  /**
   * If fragment is the name of a month or a three letter abbreviation of the
   * name of the month returns a number between 0 (for January) and 11 (for December).
   * If fragment is not recognised returns -1.
   * 
   * @param fragment
   * @return
   */
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
  
  /**
   * Generalised trim function allowing the removal of any number of an arbitrary set of chars from 
   * either the beginning or end - or both - of a given string.
   * 
   * @param target the String to trim
   * @param chars all characters contained in this String will be removed from the affected ends of {@code target}
   * @param leading if true the beginning of target is trimmed
   * @param trailing if true the end of target is trimmed
   * @return the trimmed result
   */
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


  /**
   * Splits {@code target} into fragments which are words, numbers or other chararcters and adds them 
   * to {@code toAddTo} in the order they occur in {@code target}.
   * <p>
   * Words are defined as contiguous sequences of characters for which {@code Character.isLetter()} returns true, 
   * and numbers are defined as contiguous sequences of characters for which {@code Character.isDigit()}
   * returns true. If {@code allowPointInNumbers} is true, the character '.' is treated exactly as if it was a
   * digit. Although this will allow a decimal number to be detected and extracted it will also extract 
   * individual or grouped '.' characters (that is ".34....32.122...4." would be extracted as a single token).
   * 
   * @param target string to split
   * @param toAddTo collection to add the extracted substrings to
   * @param allowPointInNumbers if true '.' is treated as a digit
   * @return number of words and numbers extracted.
   */
  public static int splitByWordsAndNumbers( final CharSequence target, final Collection<String> toAddTo, final boolean allowPointInNumbers) {
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

  public static void main(String[] args) throws IOException, ImageProcessingException {

    File commonParent = new File("C:\\Users\\safos\\OneDrive\\Desktop\\cwimages"); // change this to point at wherever your cwimages folder is
    File outputFolder = new File(commonParent, "PhotoOrganiserOutput");

    addFolder(new File(commonParent, "cwimages\\dsacw_images"), false, true, true, false);
    addFolder(new File(commonParent, "cwimages\\oopimages"), false, true, true, false);

    boolean simulation = true;
    boolean useYearFolders = true;
    boolean useYearAndMonthFolders = true;
    boolean useYearMonthDayFolders = true;

    scanFolders(outputFolder, simulation, useYearFolders, useYearAndMonthFolders, useYearMonthDayFolders);
    //scanFolders(outputFolder,simulation,targetFolderList);
  }
}
