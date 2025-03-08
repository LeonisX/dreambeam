package md.leonis.dreambeam.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.BinaryUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.statik.Config.str;

public class ViewPaneController implements Closeable {

    public ListView<String> filesListView;
    public Label timeLabel;
    public Button scanButton;
    public Button backButton;
    public Button breakButton;
    public ProgressBar fileProgressBar;
    public ProgressBar totalProgressBar;
    public VBox progressVBox;
    public Label fileProgressLabel;
    public Label totalProgressLabel;
    public StackPane stackPane1;
    public StackPane stackPane2;

    private Timeline timeline;

    private volatile boolean breaked;
    private volatile boolean error;

    @FXML
    private void initialize() {
        progressVBox.setManaged(false);

        update();
    }

    private void update() {
        Storage.saveFiles = Storage.files.stream().map(Path::toString).collect(Collectors.toList());
        filesListView.setItems(FXCollections.observableList(Storage.saveFiles));
        filesListView.scrollTo(0);
    }

    public void backButtonClick() {
        JavaFxUtils.showPrimaryPanel();
    }

    public void scanButtonClick() {
        final Instant start = Instant.now();
        error = false;
        breaked = false;
        breakButton.setVisible(true);
        scanButton.setVisible(false);

        progressVBox.setManaged(true);
        fileProgressBar.setProgress(0); //todo обновлять прогресс файла когда научимся читать блоками
        totalProgressBar.setProgress(0);
        totalProgressLabel.setText("0%");
        AtomicBoolean tick = new AtomicBoolean(true);

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            long duration = java.time.Duration.between(start, Instant.now()).toMillis();
            String time = tick.getAndSet(!tick.get()) ? Utils.formatSeconds(duration) : Utils.formatSecondsNoTick(duration);
            timeLabel.setText(time);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        new Thread(() -> {
            long totalSize = 0;

            for (int i = 0; i < Storage.files.size(); i++) {
                if (breaked) {
                    break;
                }

                Path file = Storage.files.get(i);
                long size;
                try {
                    size = Files.size(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                totalSize += size;

                String currentFile = Storage.isDirectory
                        ? file.toString().replace(Storage.lastDirectory.getAbsolutePath(), "").substring(1).toLowerCase()
                        : file.subpath(0, file.getNameCount()).toString().toLowerCase();

                Platform.runLater(() -> fileProgressLabel.setText(currentFile));

                try {
                    //todo читать блоками а не целиком
                    byte[] bytes = Files.readAllBytes(Storage.files.get(i));

                    //сравнивать на всякий случай с size
                    if (bytes.length != size) {
                        throw new RuntimeException(String.format("%s: %s: %s != %s !", file, str("view.size.is.different.error"), bytes.length, size));
                    }

                    Storage.saveFiles.set(i, Utils.formatRecord(currentFile, bytes.length, BinaryUtils.crc32String(bytes)));

                    double percents = i * 1.0 / Storage.files.size();
                    Platform.runLater(() -> {
                        totalProgressLabel.setText(String.format("%.2f%%", percents * 100));
                        totalProgressBar.setProgress(percents);
                    });
                    refreshControls(i);

                } catch (Exception e) {
                    error = true;
                    Storage.saveFiles.set(i, Utils.formatRecord(currentFile, size, "Error!!!"));
                    refreshControls(i);
                    JavaFxUtils.log(String.format("%s: %s", file, str("view.read.error")));
                }
            }

            Platform.runLater(timeline::stop);

            if (!breaked) {
                long duration = java.time.Duration.between(start, Instant.now()).toMillis();
                JavaFxUtils.log(String.format("@%s: %s", str("view.scan.time"), Utils.formatSeconds(duration)));

                Storage.saveFiles.add(0, String.format("Total size: %s bytes.", totalSize));
                //Storage.saveFiles.add(""); // костыль конечно, но так работал код на Delphi :(

                Storage.error = error;
                Storage.crc32 = BinaryUtils.crc32String((String.join("\r\n", Storage.saveFiles) + "\r\n").getBytes());// костыль конечно, но так работал код на Delphi :(

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

                JavaFxUtils.showSavePanel();
            }
        }).start();
    }

    private void refreshControls(int i) {
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

    public void interruptButtonClick() {
        breaked = true;
        breakButton.setVisible(false);
        scanButton.setVisible(true);

        JavaFxUtils.log(String.format("!%s", str("view.interrupted")));
        JavaFxUtils.showPrimaryPanel();
    }

    @Override
    public void close() throws IOException {
        breaked = true;
        if (timeline != null) {
            timeline.stop();
        }
    }
}
