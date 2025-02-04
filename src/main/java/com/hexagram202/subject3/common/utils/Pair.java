package com.hexagram202.subject3.common.utils;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Pair<K, V> {
    public static<Q, W> Pair<Q, W> of(Q q, W w) {
        return new Pair<>(q, w);
    }

    private final K left;
    private final V right;

    public K left() {
        return left;
    }

    public V right() {
        return right;
    }

    public Pair(K k, V v) {
        this.left = k;
        this.right = v;
    }
    
}
