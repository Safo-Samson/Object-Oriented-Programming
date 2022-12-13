package src.oop.cw1_2223.assignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {

    File commonParent = new File("C:\\Users\\safos\\OneDrive\\Desktop\\cwimages");
    File outputFolder = new File(commonParent, "PhotoOrganiserOutput");

    File targetFolder = new File(commonParent, "cwimages\\dsacw_images");
    File targetFolder2 = new File(commonParent,"cwimages\\oopimages");

    // creating Flag object
    Flag setBooleans = new Flag(false,true,true,true);

    //creating OrganisedTargetFolder objects
    OrganisedTargetFolder tf = new OrganisedTargetFolder(targetFolder,setBooleans);
    OrganisedTargetFolder tf2 = new OrganisedTargetFolder(targetFolder2,setBooleans);

    // creating list of OrganisedTargetFolder objects and adding two objects
    List<OrganisedTargetFolder> targetFolderList = new ArrayList<>();
    targetFolderList.add(tf);
    targetFolderList.add(tf2);

    // creating FoldersDate object
     FoldersDate foldersDate = new FoldersDate(true,true,true);
     boolean simulation = true;

     // passing arguments to scanFolders
     tf.scanFolders(outputFolder,simulation,foldersDate,targetFolderList);
    }
}
