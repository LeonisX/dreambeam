package md.leonis.dreambeam.model;

import java.util.Map;
import java.util.Objects;

// Копия кода из  org.apache.commons.lang3.tuple.ImmutablePair
// Копия кода из  org.apache.commons.lang3.tuple.Pair
// Нужна для уменьшения размера исполняемого файла.
public class Pair<L, R> {

    @SuppressWarnings("rawtypes")
    private static final Pair NULL = new Pair<>(null, null);

    @SuppressWarnings("unchecked")
    public static <L, R> Pair<L, R> nullPair() {
        return NULL;
    }

    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return left != null || right != null ? new Pair<>(left, right) : nullPair();
    }

    public static <L, R> Pair<L, R> of(final Map.Entry<L, R> pair) {
        return pair != null ? new Pair<>(pair.getKey(), pair.getValue()) : nullPair();
    }

    public final L left;

    public final R right;

    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public final L getKey() {
        return getLeft();
    }

    public R getValue() {
        return getRight();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry<?, ?> other) {
            return Objects.equals(getKey(), other.getKey())
                    && Objects.equals(getValue(), other.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        // see Map.Entry API specification
        return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return "(" + getLeft() + ',' + getRight() + ')';
    }
}
