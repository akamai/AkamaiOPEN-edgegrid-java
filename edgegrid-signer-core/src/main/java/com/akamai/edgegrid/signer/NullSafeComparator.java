/*
 * Copyright 2019 Akamai Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
