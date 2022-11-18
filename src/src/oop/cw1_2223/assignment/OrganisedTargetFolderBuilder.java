package src.oop.cw1_2223.assignment;

import java.io.File;

public class OrganisedTargetFolderBuilder {
    private File targetFolder;
    private Flag setFlags;

    public OrganisedTargetFolderBuilder setTargetFolder(File targetFolder) {
        this.targetFolder = targetFolder;
        return this;
    }

    public OrganisedTargetFolderBuilder setSetFlags(Flag setFlags) {
        this.setFlags = setFlags;
        return this;
    }

    public OrganisedTargetFolder createOrganisedTargetFolder() {
        return new OrganisedTargetFolder(targetFolder, setFlags);
    }
}