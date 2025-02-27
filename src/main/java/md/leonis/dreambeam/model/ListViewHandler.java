package md.leonis.dreambeam.model;

import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

//https://stackoverflow.com/questions/14357515/javafx-close-window-on-pressing-esc
public class ListViewHandler<T> {

    /**
     * Amount of time to wait between key presses that typing a subsequent key is considered part of the same search, in milliseconds.
     */
    private static final int RESET_DELAY_MS = 800;

    private String searchString = "";
    private int mSearchSkip = 0;
    private long mLastTyped = System.currentTimeMillis();
    private final ListView<T> listView;

    public ListViewHandler(final ListView<T> listView) {
        this.listView = listView;
    }

    public void handle(final KeyEvent key) {
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
            final var straw = item.toString().toUpperCase();

            if (straw.startsWith(searchString)) {
                if (mSearchSkip > skipped) {
                    skipped++;
                    continue;
                }

                listView.getSelectionModel().select(item);
                final int index = listView.getSelectionModel().getSelectedIndex();
                listView.getFocusModel().focus(index);
                listView.scrollTo(index - getVisibleCount(listView) / 2);
                found = true;
                break;
            }
        }

        if (!found) {
            mSearchSkip = 0;
        }
    }

    public int getVisibleCount(ListView<?> t) {
        try {
            ListViewSkin<?> ts = (ListViewSkin<?>) t.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            return vf.getLastVisibleCell().getIndex() - vf.getFirstVisibleCell().getIndex();
        } catch (Exception ignored) {
            return 0;
        }
    }

}
