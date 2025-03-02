package md.leonis.dreambeam.utils;

import javafx.scene.control.Alert;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceUtils {

    public static void calculateBaseHashes(boolean reportDuplicates) {
        try {
            var pair = Utils.calculateHashes(Config.getBaseGamesDir());
            Config.baseHashes = pair.getLeft();
            Config.baseDuplicates = pair.getRight();
            if (reportDuplicates) {
                reportBaseDuplicates();
            }
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать файлы базы данных!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void reportBaseDuplicates() {
        if (Config.baseDuplicates != null && !Config.baseDuplicates.isEmpty()) {
            Config.baseDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).forEach(System.out::println);
            JavaFxUtils.showAlert("Ошибка!", "В базе данных есть дубликаты!",
                    Config.baseDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void calculateUserHashes(boolean reportDuplicates) {
        try {
            var pair = Utils.calculateHashes(Config.getUserDir());
            Config.userHashes = pair.getLeft();
            Config.userDuplicates = pair.getRight();
            if (reportDuplicates) {
                reportUserDuplicates();
            }
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать файлы пользовательской базы!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void reportUserDuplicates() {
        if (!Config.userDuplicates.isEmpty()) {
            Config.userDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).forEach(System.out::println);
            JavaFxUtils.showAlert("Ошибка!", "В вашей базе данных есть дубликаты!",
                    Config.userDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void recalculateHashes() {
        try {
            Instant start = Instant.now();
            calculateShortList();
            FileUtils.writeToFile(Config.getBaseGamesDatFile(), Config.baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> e.getValue() + " - " + e.getKey()).toList());
            String time = Utils.formatSeconds(Duration.between(start, Instant.now()).toMillis());
            JavaFxUtils.showAlert("DreamBeam", "Создание краткого списка завершено!", String.format("Время выполнения: %s s", time), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось пересчитать краткий список!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void readGamesDat() {
        Config.baseHashes = new HashMap<>();

        Path path = Config.getBaseGamesDatFile().normalize().toAbsolutePath();
        if (FileUtils.exists(path)) {
            try {
                var pair = Utils.loadShortListHashes(path);
                Config.baseHashes = pair.getLeft();
                Config.baseDuplicates = pair.getRight();
                reportBaseDuplicates();
            } catch (Exception e) {
                JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать файл " + path + "; Он будет пересоздан.", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
                FileUtils.deleteSilently(path);
                recalculateHashes();
            }
        } else {
            recalculateHashes();
        }
    }

    public static void calculateShortList() throws IOException {
        var pair = Utils.calculateHashes(Config.getBaseGamesDir());
        Config.baseHashes = pair.getLeft();
        reportBaseDuplicates();
    }

    public static void loadTexts() {
        try {
            Config.textMap = Utils.loadTexts(Config.getTextsDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать описания!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @SuppressWarnings("all")
    public static String getVersion() {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model;
            if (Files.exists(Paths.get("pom.xml"))) {
                return reader.read(new FileReader("pom.xml")).getVersion();
            } else {
                return reader.read(new InputStreamReader(ServiceUtils.class.getResourceAsStream("pom.xml"))).getVersion();
            }
        } catch (Exception ignored) {
            return "@!#?@!";
        }
    }
}
