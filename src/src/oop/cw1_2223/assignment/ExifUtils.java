package src.oop.cw1_2223.assignment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * Tiny API to access Drew Noake's metadata extractor library.
 * I needed an easy way to rotate pictures based on their EXIF orientation code.
 * This class provides a very small API to do so easily using Drew Noake's
 * metadata extractor library.
 * <p>
 * http://drewnoakes.com/code/exif/<br>
 * http://code.google.com/p/metadata-extractor/
 * <p>
 * It is compiled together with his unchanged sources extracted from 
 * metadata-extractor-2.5.0-RC2-src, with the java core files for 
 * adobe.xmp which some image files may need. Basically I think you
 * get an exception on any file which contains XMP metadata, and without
 * changing Drew's sources, that exception would prevent you getting the
 * other metadata.
 * <p>
 * To use this class most effectively with a jarred application, place the
 * exifutil.jar adjacent to the application jar, and add it to the application
 * jar's class path attribute in the manifest. The following usage paradigm
 * allows the application to survive a missing exifutil.jar at runtime:
 * <p>
 * In a convenient class:
 * <pre>
 * public static boolean exifSupport = false;
 * static {
 *   try {
 *     Class.forName( "exifutil.ExifUtils");
 *     exifSupport = true;
 *   } catch( final NoClassDefFoundError x) {
 *     System.out.println( "EXIF metadata library not found: no EXIF rotation support for images (" + x + ")");      
 *   } catch( final ClassNotFoundException e) {
 *     System.out.println( "EXIF metadata library not found: no EXIF rotation support for images (" + e + ")");
 *   }
 * }
 * </pre>
 * And in code executed after the class above loaded:
 * <pre>
 * if ( exifSupport) {
 *   int exifOrientationCode = ExifUtils.getExifOrientationCode( filename);
 * }
 * </pre>
 *
 * @version Mike Child, 21 Jul 2011
 */
public class ExifUtils {

  /**
   * Returns the exif orientation code contained in {@code file} or 1 if it could
   * not be read for any reason.
   */
  public static int getExifOrientationCode( final String file) {
    return ExifUtils.getExifOrientationCode( new File( file), new Exception[1]);
  }

  /**
   * Returns the exif orientation code contained in {@code file} or 1 if it could
   * not be read for any reason. Any exception that occurs will be caught and set as
   * element zero of {@code exceptionInfo}.
   */
  public static int getExifOrientationCode( final String file, final Exception[] exceptionInfo) {
    return ExifUtils.getExifOrientationCode( new File( file), exceptionInfo);
  }

  /**
   * Returns the exif orientation code contained in {@code file} or 1 if it could
   * not be read for any reason.
   */
  public static int getExifOrientationCode( final File file) {
    return ExifUtils.getExifOrientationCode( file, new Exception[1]);
  }

  /**
   * Returns the exif orientation code contained in {@code file} or 1 if it could
   * not be read for any reason. Any exception that occurs will be caught and set as
   * element zero of {@code exceptionInfo}.
   */
  public static int getExifOrientationCode( final File file, final Exception[] exceptionInfo) {
    try {
      return ExifUtils.getExifOrientationCode( ImageMetadataReader.readMetadata( file), exceptionInfo);
    } catch( final ImageProcessingException e) {
      exceptionInfo[0] = e;
    } catch( final IOException e) {
      exceptionInfo[0] = e;
    }
    return 1;
  }

  /**
   * Returns the exif orientation code read from in {@code stream} or 1 if it could
   * not be read for any reason. Any exception that occurs will be caught and set as
   * element zero of {@code exceptionInfo}.
   */
  public static int getExifOrientationCode( final BufferedInputStream stream, final Exception[] exceptionInfo) {
    try {
      return ExifUtils.getExifOrientationCode( ImageMetadataReader.readMetadata( stream, false), exceptionInfo);
    } catch( final ImageProcessingException e) {
      exceptionInfo[0] = e;
    } catch( final IOException e) {
      exceptionInfo[0] = e;
    }
    return 1;
  }

  /**
   * Returns the exif orientation code contained in {@code metadata} or 1 if it could
   * not be read for any reason. Any exception that occurs will be caught and set as
   * element zero of {@code exceptionInfo}.
   */
  public static int getExifOrientationCode( final Metadata metadata, final Exception[] exceptionInfo) {
    final Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
    if ( directory != null) {
      if ( directory.containsTag( ExifIFD0Directory.TAG_ORIENTATION)) {
        try {
          return directory.getInt( ExifIFD0Directory.TAG_ORIENTATION);
        } catch( final MetadataException e) {
          exceptionInfo[0] = e;
        }
      } else {
        exceptionInfo[0] = new RuntimeException( "No orientation tag found in EXIF data");
      }
    } else {
      exceptionInfo[0] = new RuntimeException( "No EXIF data found in metadata");
    }
    return 1;
  }

  /**
   * Returns the exif date/time  code contained in {@code metadata} or null if it could
   * not be read for any reason. Any exception that occurs will be caught and set as
   * element zero of {@code exceptionInfo}. Value is converted in the local timezone.
   */
  public static Date getExifDateTime( final Metadata metadata, final Exception[] exceptionInfo) {
    final Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
    if ( directory != null) {
      return directory.getDate( ExifIFD0Directory.TAG_DATETIME);    
    } else {
      exceptionInfo[0] = new RuntimeException( "No EXIF directory ExifIFD0Directory found in metadata");
    }
    return null;
  }

  /**
   * Returns the exif date/time original contained in {@code metadata} or null if it could
   * not be read for any reason. Any exception that occurs will be caught and set as
   * element zero of {@code exceptionInfo}. Value is converted in the local timezone.
   */
  public static Date getExifDateTimeOriginal( final Metadata metadata, final Exception[] exceptionInfo) {
    final Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
    if ( directory != null) {
      return directory.getDate( ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    } else {
      exceptionInfo[0] = new RuntimeException( "No EXIF directory ExifSubIFDDirectory found in metadata");
    }
    return null;
  }

  /**
   * Returns the earlier of "date/time" and "date/time original" if both are present, else whichever
   * is not null, else null if neither are present.
   * 
   * @param metadata
   * @return
   */
  public static Date getFirstDate( final Metadata metadata) {
    Date basic = getExifDateTime(metadata, new Exception[1]);
    Date original = getExifDateTimeOriginal(metadata, new Exception[1]);
    if (basic == null) {
      return original;
    } else if (original == null) {
      return basic;
    } else {
      if (original.before( basic)) {
        return original;
      } else {
        return basic;
      }
    }
  }
  
  public static Metadata getMetaData( File file) throws ImageProcessingException, IOException {
    return ImageMetadataReader.readMetadata( file);
  }
  
//  public TreeMap<String> getNameToTagCodeMap() {
//    ExifIFD0Directory.
//  }
  
  /**
   * Lists all the tags, of all types, that can be read from metadata into the given
   * output lists. {@code human} is loaded with human readable versions, {@code machine} with the raw values.
   * Note that {@code human} must be examined for the tag names, {@code machine} contains only the
   * raw values, but the elements of the lists are guaranteed to correspond to each other.
   */
  public static void listMetadata( final Metadata metadata, final List<String> human, final List<Object> machine) {
    for( final Directory directory : metadata.getDirectories()) {
      for( final Tag tag : directory.getTags()) {
        human.add( tag.getTagName() + ": " + tag.toString());
        machine.add( directory.getObject( tag.getTagType()));
      }
    }
  }

  /**
   * Gets a map of the case-insensitive names of all tags to the value(s) present in the various
   * directories (categories of tag). The returned Value object identifies the category and the 
   * value of the tag, and has a link to another Value object if the same tag name is present in 
   * more than one category.
   * 
   * @param metadata
   * @return
   */
  public static TreeMap<String,Value> getNameMap( final Metadata metadata) {
    TreeMap<String,Value> map = new TreeMap<String,Value>(String.CASE_INSENSITIVE_ORDER);
    for( final Directory directory : metadata.getDirectories()) {
      for( final Tag tag : directory.getTags()) {
        Value value = new Value( tag.getDirectoryName(), directory.getObject( tag.getTagType()));
        Value existing = map.get(tag.getTagName());
        if ( existing != null) {
          existing.append(value);
        } else {
          map.put( tag.getTagName(), value);
        }
      }
    }
    return map;
  }

  public static void listNameMap(final Metadata metadata) {
    TreeMap<String,Value> map = getNameMap(metadata);
    for( Map.Entry<String, Value> me : map.entrySet()) {
      System.out.println( me.getKey() + " = " + me.getValue());
    }
  }
  
  /**
   * Class that represents a value for a named tag as an arbitrary object
   * together with the name of the directory (category) to which that tag
   * belongs. This class also forms a linked list of Value objects, the 
   * intention being that each Value in the list is the value of the same
   * tag name in different directories.
   */
  public static class Value {
    public final String directoryName;
    public final Object value;
    public Value next;
    public Value(String directoryName, Object value) {
      super();
      this.directoryName = directoryName;
      this.value = value;
    }  
    public Value next() {
      return this.next;
    }
    /**
     * Appends another Value to the end of the linked list of which this
     * Value is the head.
     * 
     * @param other
     */
    public void append(Value other) {
      Value last = this;
      while( last.next != null) {
        last = last.next;
      }
      last.next = other;
    }
    /** 
     * Searches the linked list this Value is the head of for a Value whose
     * directory name matches the given one, and returns the value object from
     * it, or null if no Value matches the given name.
     * 
     * @param directoryName
     * @return
     */
    public Object value( String directoryName) {
      Value last = this;
      do {
        if ( last.directoryName.equals(directoryName)) {
          return last.value;
        }
        last = last.next;
      } while( last.next != null);
      return null;      
    }
    /**
     * Lists the Values in the linked list of which this Value is the head,
     * enclosing the list in brackets if it contains more than one Value.
     */
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      Value last = this;
      int count = 0;
      do {
        if (count > 0) { builder.append(", "); }
        builder.append(last.directoryName);
        builder.append(": ");
        builder.append(last.value.toString());
        last = last.next;
        count++;
      } while( last != null);
      if (count > 1) { builder.append("]"); builder.insert(0, "["); }
      return builder.toString();
    }
  }
  
  /**
   * Lists all the tags, of all types, that can be read from metadata to standard out.
   * Each line has the human readable version followed by the machine version enclosed in curly
   * brackets, as produced by {@link #listMetadata(Metadata, List, List)}
   */
  public static void printMetadata( final Metadata metadata) {
    final List<String> human = new ArrayList<String>();
    final List<Object> machine = new ArrayList<Object>();
    ExifUtils.listMetadata( metadata, human, machine);
    for( int i = 0; i < human.size(); i++) {
      System.out.println( human.get(i) + " {" + machine.get( i) + "}");
    }
  }

  /**
   * Opens a file chooser and dumps the metadata from the selected file, or all files
   * in a selected folder, to standard out.
   */
  public static void main( final String[] args) throws ImageProcessingException, IOException, MetadataException {
//    File file = new File("C:/Documents and Settings/childm/00_mwc/personal/pics/camilla-flat/P7121698.jpg");
//    File jpegFile = new File("C:/Documents and Settings/childm/00_mwc/personal/pics/macwedding/P1010008_6.jpg");

//    printMetadata( ImageMetadataReader.readMetadata( file));

//    Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
//    for (Directory directory : metadata.getDirectories()) {
//      for (Tag tag : directory.getTags()) {
//          System.out.println(tag);
//      }
//    }
//
//    System.out.println("-----------------");
//    Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
//    ExifIFD0Descriptor d = new ExifIFD0Descriptor( (ExifIFD0Directory)directory);
//    final int orientation = directory.getInt( ExifIFD0Directory.TAG_ORIENTATION);
//    final String s = d.getOrientationDescription();
//    System.out.println( "Orientation: " + orientation + " " + s);
//
    final JFrame dummy = new JFrame();
    final JFileChooser chooser = new JFileChooser( "Select image file or folder");
    chooser.setMultiSelectionEnabled( false);
    chooser.showOpenDialog( dummy);
    final File f = chooser.getSelectedFile();
    if ( f.isDirectory()) {
      final File[] fs = f.listFiles();
      for( int i = 0; i < fs.length; i++) {
        System.out.println( "------------------------------------");
        System.out.println( fs[i]);
        System.out.println( "------------------------------------");
        ExifUtils.printMetadata( ImageMetadataReader.readMetadata( f));
        System.out.println( "------------------------------------");
      }
    } else {
      System.out.println( "------------------------------------");
      System.out.println( f);
      System.out.println( "------------------------------------");
      Metadata metadata = ImageMetadataReader.readMetadata( f);
      ExifUtils.printMetadata( metadata);
      System.out.println( "------------------------------------");
      listNameMap(metadata);
    }
  }

}
