package md.leonis.dreambeam.model;

import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import md.leonis.dreambeam.model.enums.Style;

import java.util.Arrays;
import java.util.List;

public class ListViewHandler {

    /**
     * Amount of time to wait between key presses that typing a subsequent key is considered part of the same search, in milliseconds.
     */
    private static final int RESET_DELAY_MS = 800;

    private String searchString = "";
    private int mSearchSkip = 0;
    private long mLastTyped = System.currentTimeMillis();
    private final ListView<Triple<Style, String, Object>> listView;
    private final ListView<Triple<Style, String, Object>> rightListView;

    public ListViewHandler(final ListView<Triple<Style, String, Object>> listView, final ListView<Triple<Style, String, Object>> rightListView) {
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
        for (final Triple<Style, String, Object> item : listView.getItems()) {
            var string = item.getCenter().toUpperCase();

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

    private int limitScroll(ListView<Triple<Style, String, Object>> listView, int index) {
        if (index < 0) {
            index = 0;
        } else {
            if (index > listView.getItems().size() - 1) {
                index = listView.getItems().size() - 1;
            }
        }
        return index;
    }

    public void sync(Triple<Style, String, Object> row) {
        if (rightListView.getUserData() != null) {
            return;
        }
        listView.setUserData("!");
        String searchString = (row == null) ? null : row.getCenter();

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

    private boolean find(ListView<Triple<Style, String, Object>> listView, String searchString) {
        for (final Triple<Style, String, Object> item : listView.getItems()) {
            var string = item.getCenter().toUpperCase();

            if (string.startsWith(searchString)) {
                listView.getSelectionModel().select(item);
                return true;
            }
        }
        return false;
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
