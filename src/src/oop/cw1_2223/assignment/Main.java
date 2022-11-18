package src.oop.cw1_2223.assignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {


    Flag setAllTrue = new Flag(true,true,true,true);

    OrganisedTargetFolder tf1 = new OrganisedTargetFolderBuilder().setTargetFolder(new File("C:\\Users\\safos\\OneDrive\\Desktop\\cwimages")).setSetFlags(setAllTrue).createOrganisedTargetFolder();
   // OrganisedTargetFolder tf2 = new OrganisedTargetFolderBuilder().setTargetFolder(new File("cwimages\\dsacw_images")).setSetFlags(setAllTrue).createOrganisedTargetFolder();
    OrganisedTargetFolder tf3 = new OrganisedTargetFolderBuilder().setTargetFolder(new File("C:\\Users\\safos\\OneDrive\\Desktop\\cwimages")).setSetFlags(setAllTrue).createOrganisedTargetFolder();
    OrganisedTargetFolder tf4 = new OrganisedTargetFolderBuilder().setTargetFolder(new File("cwimages\\dsacw_images")).setSetFlags(setAllTrue).createOrganisedTargetFolder();

    OrganisedTargetFolder t5 = new OrganisedTargetFolder(new File("C:\\Users\\safos\\OneDrive\\Desktop\\cwimages"),setAllTrue );
//    OrganisedTargetFolder tf = new OrganisedTargetFolderBuilder().setTargetFolder(targetFolder).setSetFlags(new Flag(false, true, true, false)).createOrganisedTargetFolder();








    File commonParent = new File("C:\\Users\\safos\\OneDrive\\Desktop\\cwimages");
    File outputFolder = new File(commonParent, "PhotoOrganiserOutput");

    File targetFolder = new File(commonParent, "cwimages\\dsacw_images");
    File targetFolder2 = new File(commonParent,"cwimages\\oopimages");


    OrganisedTargetFolder tf = new OrganisedTargetFolder(targetFolder,new Flag(false,true,true,true));
    OrganisedTargetFolder tf2 = new OrganisedTargetFolder(targetFolder2,new Flag(false,true,true,true));
    List<OrganisedTargetFolder> targetFolderList = new ArrayList<>();
    targetFolderList.add(tf);
    targetFolderList.add(tf2);

    boolean simulation = true;
    boolean useYearFolders = true;
    boolean useYearAndMonthFolders = true;
    boolean useYearMonthDayFolders = true;
    tf.scanFolders(outputFolder,simulation,useYearFolders,useYearAndMonthFolders,useYearMonthDayFolders,targetFolderList);
    }
}
