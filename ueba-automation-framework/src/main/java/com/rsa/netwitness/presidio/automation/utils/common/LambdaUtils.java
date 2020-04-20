package com.rsa.netwitness.presidio.automation.utils.common;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class LambdaUtils {

    public static <T, U> U mapWithFallback(T obj, Function<T, U> function, U fallback) {
        if (obj == null) {
            return fallback;
        } else {
            return function.apply(obj);
        }
    }

    public static <T, U> U getOrNull(T obj, Function<T, U> function) {
        return mapWithFallback(obj, function, null);
    }

    public static <E> E getListElementOrNull(List<E> list, int index) {
        if (list == null || list.size() < index-1) {
            return null;
        } else {
            return list.get(index);
        }
    }

    public static <R> Predicate<R> not(Predicate<R> predicate) {
        return predicate.negate();
    }

}
