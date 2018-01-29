package br.com.think.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public abstract class ListUtils {
    public static List<List<String>> divideList(List<String> list, int quantity) {
        return divideList(list, quantity, false);
    }

    public static List<List<String>> divideList(List<String> list, int quantity, boolean isDivisionByNumberOfLists) {
        if (quantity <= 0)
            quantity = 1;

        if (isDivisionByNumberOfLists) {
            List<List<String>> result = new ArrayList<>();

            int iterations = list.size() / quantity;
            int begin = 0;
            int end = quantity;

            for (int i = 1; i <= iterations; i++) {
                if (i < iterations) {
                    result.add(list.subList(begin, end));

                    begin = end;
                    end += quantity;
                } else {
                    result.add(list.subList(begin, list.size()));
                }
            }

            return result;
        } else {
            return Lists.partition(list, quantity);
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();

        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <T> List<T> removeDuplicates(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }
}