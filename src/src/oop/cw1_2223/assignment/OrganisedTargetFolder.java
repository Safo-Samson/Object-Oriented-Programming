package src.oop.cw1_2223.assignment;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static src.oop.cw1_2223.assignment.MyMiscellaneous.*;

public class OrganisedTargetFolder {
    private File targetFolder;
    private Flag setFlags ;

    public OrganisedTargetFolder(File targetFolder, Flag setFlags) {
        this.targetFolder = targetFolder;
        this.setFlags = setFlags;
    }


    public void scanFolders(File outputFolder, final boolean simulation,FoldersDate fd,List<OrganisedTargetFolder> targetFolders) throws IOException {
        Set<File> duplicateDetector = new HashSet<>();
        for (OrganisedTargetFolder folder : targetFolders)
            scan(folder,fd, outputFolder, simulation, duplicateDetector);
    }

    private void scan(OrganisedTargetFolder folder, FoldersDate fd,File outputFolder, boolean simulation, Set<File> duplicateDetector) throws IOException {
        List<File> files = folder.getFiles();
        for (File file : files) {
            process(file, setFlags.useExifDate(),setFlags.useFilenameDate(),setFlags.useFileTimestamp(),fd,outputFolder, simulation, duplicateDetector);
        }
    }

    public List<File> getFiles() {
        List<File> list = new ArrayList<>();
        list.add(targetFolder);
        int position = 0;
        while (position < list.size()) {
            File current = list.get(position);
            if (current.isDirectory()) {
                list.remove(position); // remove directories from the list to be returned
                if (list.size() == 0 || setFlags.useRecurse()) {
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




}
