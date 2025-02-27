package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import md.leonis.dreambeam.utils.BinaryUtils;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class ViewPaneController {

    public ListView<String> filesListView;
    public Label timeLabel;
    public Button scanButton;
    public Button backButton;
    public Button breakButton;

    private boolean breaked;

    @FXML
    private void initialize() {
        update();
    }

    private void update() {
        Config.saveFiles = Config.files.stream().map(Path::toString).collect(Collectors.toList());
        filesListView.setItems(FXCollections.observableList(Config.saveFiles));
        filesListView.scrollTo(0);
    }

    public void backButtonClick() {
        JavaFxUtils.showPane("PrimaryPane.fxml");
    }

    public void scanButtonClick() {
        breaked = false;
        breakButton.setVisible(true);
        scanButton.setVisible(false);

        new Thread(() -> {
            long totalSize = 0;

            for (int i = 0; i < Config.files.size(); i++) {
                Path file = Config.files.get(i);
                long size;
                try {
                    size = Files.size(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                totalSize += size;

                String fileName = Config.isDirectory
                        ? file.toString().replace(Config.lastDirectory.getAbsolutePath(), "").substring(1).toLowerCase()
                        : file.subpath(0, file.getNameCount()).toString().toLowerCase();

                System.out.println(fileName);
                if (!breaked) {
                    try {
                        //todo кнопка паузы
                        //todo читать блоками а не целиком,
                        //todo строка прогресса

                        byte[] bytes = Files.readAllBytes(Config.files.get(i));

                        //сравнивать на всякий случай с size
                        if (bytes.length != size) {
                            throw new RuntimeException(String.format("%s size is different: %s != %s !", file, bytes.length, size));
                        }

                        int crc32 = BinaryUtils.crc32(bytes);
                        Config.saveFiles.set(i, String.format("%s [%s bytes] - %s", fileName, bytes.length, String.format("%08X", crc32)));

                        refreshListView(i);

                    } catch (Exception e) {
                        Config.saveFiles.set(i, String.format("%s [%s bytes] - Error!!!", fileName, size));
                        refreshListView(i);
                    }
                }
            }

            Config.saveFiles.add(0, String.format("Total size: %s bytes.", totalSize));
            //Config.saveFiles.add(""); // костыль конечно, но так работал код на Delphi :(

            if (!breaked) {
                Config.crc32 = BinaryUtils.crc32String((String.join("\r\n", Config.saveFiles) + "\r\n").getBytes());// костыль конечно, но так работал код на Delphi :(
                Platform.runLater(() -> JavaFxUtils.showPane("SavePane.fxml"));
            }
        }).start();
    }

    private void refreshListView(int i) {
        Platform.runLater(() -> {
            getFirstAndLast(filesListView);
            if (i >= last) {
                filesListView.scrollTo(first + 2);
            }
            filesListView.refresh();
        });
    }

    private int first = 0;
    private int last = 0;

    public void getFirstAndLast(ListView<?> t) {
        try {
            ListViewSkin<?> ts = (ListViewSkin<?>) t.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            first = vf.getFirstVisibleCell().getIndex();
            last = vf.getLastVisibleCell().getIndex();
        } catch (Exception ignored) {
        }
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public void breakButtonClick() {
        breaked = true;
        breakButton.setVisible(false);
        scanButton.setVisible(true);
        update();
    }
}
