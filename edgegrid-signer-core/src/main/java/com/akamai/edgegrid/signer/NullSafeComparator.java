package com.akamai.edgegrid.signer;

import java.util.Comparator;

/**
 * This is a null-safe {@link Comparator} implementation to make the classes {@link Comparable}
 * implementation simpler.
 *
 * @param <T> any type that implements {@link Comparable}
 */
public class NullSafeComparator<T extends Comparable<T>> implements Comparator<T> {

    @Override
    public int compare(final T o1, final T o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return o1.compareTo(o2);
    }

}
