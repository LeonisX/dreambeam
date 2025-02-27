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
    private boolean error;

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
        error = false;
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

                if (!breaked) {
                    try {
                        //todo кнопка паузы
                        //todo читать блоками а не целиком,
                        //todo строка прогресса
                        //todo время сканирования

                        byte[] bytes = Files.readAllBytes(Config.files.get(i));

                        //сравнивать на всякий случай с size
                        if (bytes.length != size) {
                            throw new RuntimeException(String.format("%s size is different: %s != %s !", file, bytes.length, size));
                        }

                        int crc32 = BinaryUtils.crc32(bytes);
                        Config.saveFiles.set(i, String.format("%s [%s bytes] - %s", fileName, bytes.length, String.format("%08X", crc32)));

                        refreshListView(i);

                    } catch (Exception e) {
                        error = true;
                        Config.saveFiles.set(i, String.format("%s [%s bytes] - Error!!!", fileName, size));
                        refreshListView(i);
                        JavaFxUtils.log(file + "   - ошибка чтения!");
                    }
                }
            }

            Config.saveFiles.add(0, String.format("Total size: %s bytes.", totalSize));
            //Config.saveFiles.add(""); // костыль конечно, но так работал код на Delphi :(

            if (!breaked) {
                Config.crc32 = BinaryUtils.crc32String((String.join("\r\n", Config.saveFiles) + "\r\n").getBytes());// костыль конечно, но так работал код на Delphi :(

                //todo время сканирования - двоеточие мигает если что
                //@Время сканирования: 00:17

                if (error) {
                    //todo если были ошибки - получить размер файла и поискать похожие
                    // на самом деле, лучше дождаться сравнения по файликам.
                    //По размеру образ совпадает с:
                    //Dreamsoft (Rus) (RGR)
                    //В базе данных не удалось найти похожий образ диска

                    //if getfilesize(Dir+SearchRec.name)=j then
                    //  begin
                    //   journal.memo2.SelAttributes.Color:= clgreen;journal.memo2.Lines.add('@По размеру образ совпадает с:');
                    //   journal.memo2.SelAttributes.Color:= clgreen;journal.memo2.Lines.add(@searchrec.name);memo1.perform(wm_vscroll, sb_linedown,0);flu:=true;
                    //  end;
                    // until FindNext(SearchRec)<>0;
                    // FindClose(SearchRec);
                    // if flu=false then journal.memo2.SelAttributes.Color:= clred;journal.memo2.Lines.add('!В базе данных не удалось найти похожий образ диска');memo1.perform(wm_vscroll, sb_linedown,0);
                    //e
                }

                String name = Config.hashes.get(Config.crc32);

                if (name != null) {
                    JavaFxUtils.log("@Диск распознан как: " + name);
                } else {
                    JavaFxUtils.log("#Этого диска нет в базе данных!");
                }

                JavaFxUtils.showPane("SavePane.fxml");
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

        JavaFxUtils.log("!Операция прервана!");
        update();
    }
}
