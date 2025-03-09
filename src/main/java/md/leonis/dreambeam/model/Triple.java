package md.leonis.dreambeam.model;

import java.util.Objects;

public class Triple<L, C, R> {

    @SuppressWarnings("rawtypes")
    private static final Triple NULL = new Triple<>(null, null, null);

    @SuppressWarnings("unchecked")
    public static <L, C, R> Triple<L, C, R> nullTriple() {
        return NULL;
    }

    public static <L, C, R> Triple<L, C, R> of(final L left, final C center, final R right) {
        return left != null || right != null ? new Triple<>(left, center, right) : nullTriple();
    }

    public final L left;

    public final C center;

    public final R right;

    public Triple(final L left, final C center, final R right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public C getCenter() {
        return center;
    }

    public R getRight() {
        return right;
    }

    public final L getKey() {
        return getLeft();
    }

    public C getMiddle() {
        return getCenter();
    }
    public R getValue() {
        return getRight();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Triple<?, ?, ?> other) {
            return Objects.equals(getKey(), other.getKey())
                    && Objects.equals(getValue(), other.getValue())
                    && Objects.equals(getCenter(), other.getCenter());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue()) ^ Objects.hashCode(getCenter());
    }

    @Override
    public String toString() {
        return "(" + getLeft() + ',' + getCenter() + ',' + getRight() + ')';
    }
}
