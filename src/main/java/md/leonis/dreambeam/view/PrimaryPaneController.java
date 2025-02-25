package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class PrimaryPaneController {

    public Button scanCdButton;
    public Button scanFsButton;
    public Button scanGdiButton;

    @FXML
    private void initialize() throws IOException {
        //todo вычитать список приводов, вывести под них кнопки.
        //отдельная кнопка для директории.
        //разбирать gdi
        //drag&drop
        //archives support
        FileSystemView fsv = FileSystemView.getFileSystemView();

        for (File path : File.listRoots()) {
            try {
                System.out.println("Drive Name: " + path);
                System.out.println("getSystemTypeDescription: " + fsv.getSystemTypeDescription(path));

                String volumeLabel = fsv.getSystemDisplayName(path);
                if (!StringUtils.isBlank(volumeLabel)) {
                    int index = volumeLabel.indexOf(") ");
                    if (index == -1) {
                        index = volumeLabel.lastIndexOf(" (");
                        if (index > 0) {
                            volumeLabel = volumeLabel.substring(0, index).trim();
                        }
                    } else {
                        volumeLabel = volumeLabel.substring(index + 2).trim();
                    }
                }

                System.out.println("getSystemDisplayName: " + volumeLabel);
                System.out.println("isFileSystem: " + fsv.isFileSystem(path));
                System.out.println("isDrive: " + fsv.isDrive(path));
                //System.out.println("isComputerNode: " + fsv.isComputerNode(path)); // false
                //System.out.println("isLink: " + fsv.isLink(path)); // false
                //System.out.println("isFileSystemRoot: " + fsv.isFileSystemRoot(path)); // true
                System.out.println("isFloppyDrive: " + fsv.isFloppyDrive(path));
                //System.out.println("isRoot: " + fsv.isRoot(path)); // false
                //System.out.println("isTraversable: " + fsv.isTraversable(path)); // true
                //System.out.println("isHiddenFile: " + fsv.isHiddenFile(path)); // false
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        System.out.println(FileSystems.getDefault().supportedFileAttributeViews());

        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            System.out.println(root.toString());
            try {
                FileStore fileStore = Files.getFileStore(root);

                System.out.println("getTotalSpace: " + fileStore.getTotalSpace());
                System.out.println("getUsableSpace: " + fileStore.getUsableSpace());
                System.out.println("getUnallocatedSpace: " + fileStore.getUnallocatedSpace());
                System.out.println("bytesPerSector: " + fileStore.getBlockSize());
                System.out.println("getTotalSpace: " + fileStore.getTotalSpace());
                System.out.println("volume:vsn: " + fileStore.getAttribute("volume:vsn"));
                System.out.println("volume:isRemovable: " + fileStore.getAttribute("volume:isRemovable"));
                System.out.println("volume:isCdrom: " + fileStore.getAttribute("volume:isCdrom"));
                //System.out.println("readonly: " + fileStore.getAttribute("readonly"));
                //System.out.println("hidden: " + fileStore.getAttribute("hidden"));
                //System.out.println("system: " + fileStore.getAttribute("system"));
                //System.out.println("archive: " + fileStore.getAttribute("archive"));
                //System.out.println("lastModifiedTime: " + fileStore.getAttribute("lastModifiedTime")); // FileTime
                //System.out.println("lastAccessTime: " + fileStore.getAttribute("lastAccessTime")); // FileTime
                //System.out.println("creationTime: " + fileStore.getAttribute("creationTime")); // FileTime
                //System.out.println("size: " + fileStore.getAttribute("size"));
                //System.out.println("isRegularFile: " + fileStore.getAttribute("isRegularFile"));
                //System.out.println("isDirectory: " + fileStore.getAttribute("isDirectory"));
                //System.out.println("isSymbolicLink: " + fileStore.getAttribute("isSymbolicLink"));
                //System.out.println("isOther: " + fileStore.getAttribute("isOther"));
                //System.out.println("fileKey: " + fileStore.getAttribute("fileKey")); // Object
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public void scanCdButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        if (Config.lastPath != null) {
            fileChooser.setInitialDirectory(Config.lastPath.getParentFile());
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All saves", "*.sav", "*.ssm", "*.ss0", "*.ss1", "*.s00", "*.s01"),
                new FileChooser.ExtensionFilter("SRAM saves", "*.ssm", "*.sav"),
                new FileChooser.ExtensionFilter("RAM snapshots", "*.ss0", "*.ss1", "*.s00", "*.s01"),
                new FileChooser.ExtensionFilter("Kega Fusion saves", "*.ssm", "*.ss0", "*.ss1", "*.ss2", "*.ss3", "*.ss4", "*.ss5", "*.ss6", "*.ss7", "*.ss8", "*.ss9"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        Config.lastPath = fileChooser.showOpenDialog(scanCdButton.getScene().getWindow());

        if (Config.lastPath != null && Config.lastPath.exists()) {
            JavaFxUtils.showPane("ScanPane.fxml");
        }
    }
}
