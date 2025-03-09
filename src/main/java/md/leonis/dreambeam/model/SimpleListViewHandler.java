package md.leonis.dreambeam.model;

import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import md.leonis.dreambeam.model.enums.Style;
import md.leonis.dreambeam.utils.Utils;

import java.util.Arrays;
import java.util.List;

//https://stackoverflow.com/questions/14357515/javafx-close-window-on-pressing-esc
public class SimpleListViewHandler<T> {

    /**
     * Amount of time to wait between key presses that typing a subsequent key is considered part of the same search, in milliseconds.
     */
    private static final int RESET_DELAY_MS = 800;

    private String searchString = "";
    private int mSearchSkip = 0;
    private long mLastTyped = System.currentTimeMillis();
    private final ListView<T> listView;
    private final ListView<T> rightListView;

    public SimpleListViewHandler(final ListView<T> listView, final ListView<T> rightListView) {
        this.listView = listView;
        this.rightListView = rightListView;
    }

    public void handle(final KeyEvent key) {
        listView.setUserData("!");
        if (rightListView != null) {
            rightListView.setUserData("!");
        }
        var ch = key.getText();

        if (ch == null || ch.isEmpty() ||
                key.getCode() == KeyCode.ESCAPE || key.getCode() == KeyCode.ENTER) {
            return;
        }

        ch = ch.toUpperCase();

        if (searchString.equals(ch)) {
            mSearchSkip++;
        } else {
            searchString = System.currentTimeMillis() - mLastTyped > RESET_DELAY_MS ? ch : searchString + ch;
        }

        mLastTyped = System.currentTimeMillis();

        boolean found = false;
        int skipped = 0;
        for (final T item : listView.getItems()) {
            var string = cleanString(item.toString().toUpperCase());

            if (string.startsWith(searchString)) {
                if (mSearchSkip > skipped) {
                    skipped++;
                    continue;
                }

                listView.getSelectionModel().select(item);
                int index = listView.getSelectionModel().getSelectedIndex();
                listView.getFocusModel().focus(index);
                listView.scrollTo(limitScroll(listView, index - getVisibleCount(listView) / 2));
                found = true;

                if (rightListView != null && find(rightListView, searchString)) {
                    index = rightListView.getSelectionModel().getSelectedIndex();
                    //rightListView.getFocusModel().focus(index);
                    rightListView.scrollTo(limitScroll(rightListView, index - getVisibleCount(listView) / 2));
                }
                break;
            }
        }

        if (!found) {
            mSearchSkip = 0;
        }

        listView.setUserData(null);
        if (rightListView != null) {
            rightListView.setUserData(null);
        }
    }

    private int limitScroll(ListView<T> listView, int index) {
        if (index < 0) {
            index = 0;
        } else {
            if (index > listView.getItems().size() - 1) {
                index = listView.getItems().size() - 1;
            }
        }
        return index;
    }

    public void sync(String searchString) {
        if (rightListView.getUserData() != null) {
            return;
        }
        listView.setUserData("!");

        while (true) {
            searchString = searchString == null ? "" : searchString.toUpperCase();
            if (find(rightListView, searchString)) {

                final int leftIndex = listView.getSelectionModel().getSelectedIndex();
                final int rightIndex = rightListView.getSelectionModel().getSelectedIndex();
                rightListView.getFocusModel().focus(rightIndex);

                var pair = getVisibleRange(listView);
                int mid = (pair.getLeft() + pair.getRight()) / 2;
                int diff = leftIndex - mid;

                rightListView.scrollTo(rightIndex - getVisibleCount(listView) / 2 - diff);
                break;
            }

            if (!searchString.contains(" ")) {
                break;
            }

            List<String> chunks = Arrays.stream(searchString.split(" ")).toList();
            searchString = String.join(" ", chunks.subList(0, chunks.size() - 1));
        }
        listView.setUserData(null);
    }

    private boolean find(ListView<T> listView, String searchString) {
        for (final T item : listView.getItems()) {
            var string = cleanString(item.toString().toUpperCase());

            if (string.startsWith(searchString)) {
                listView.getSelectionModel().select(item);
                return true;
            }
        }
        return false;
    }

    private String cleanString(String string) {
        for (String s : Style.prefs) {
            if (string.startsWith(s)) {
                string = string.substring(1);
            }
        }
        return string;
    }

    public int getVisibleCount(ListView<?> listView) {
        var pair = getVisibleRange(listView);
        return pair.getRight() - pair.getLeft();
    }

    public Pair<Integer, Integer> getVisibleRange(ListView<?> listView) {
        try {
            ListViewSkin<?> listViewSkin = (ListViewSkin<?>) listView.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) listViewSkin.getChildren().get(0);
            return Pair.of(vf.getFirstVisibleCell().getIndex(), vf.getLastVisibleCell().getIndex());
        } catch (Exception ignored) {
            return Pair.of(0, 0);
        }
    }
}
