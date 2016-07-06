package fortscale.common.datastructures;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class UserActivityEntryHashMap extends HashMap<String, Double> {

    private static final String OTHERS_LABEL = "Others";

    private double totalCount = 0;
    private double filteredCount = 0;

    private Set<String> filteredKeys = new HashSet<>();


    public UserActivityEntryHashMap(Set<String> filteredKeys){
        if (filteredKeys!=null) {
            this.filteredKeys = filteredKeys;
        } else {
            this.filteredKeys = new HashSet<>();
        }
    }

    public Set<Entry<String, Double>> getTopEntries(int limit) {
        if (totalCount > 0) {
            Set<Entry<String, Double>> topEntries = this.entrySet()
                    .stream()
                    //sort them by count (reverse order - we want the bigger values in the beginning)
                    .sorted((entrySet, entrySet2) -> -Double.compare(entrySet.getValue(), entrySet2.getValue()))
                    .limit(limit)                   //take only the top 'limit-number' of entries
                    .collect(Collectors.toSet());   //of entries
            final double topCount = topEntries.stream().mapToDouble(Entry::getValue).sum();
            topEntries.add(new SimpleEntry<>(OTHERS_LABEL, totalCount + filteredCount - topCount));
            return topEntries;
        }
        else {
            // in case we don't have any displayable entry, return an empty set
            return Collections.emptySet();
        }
    }

    @Override
    public Double replace(String key, Double newValue) {
        final Double oldValue = get(key);
        totalCount += newValue - oldValue;
        return super.replace(key, newValue);
    }

    @Override
    public boolean replace(String key, Double oldValue, Double newValue) {
        if (get(key).equals(oldValue)) {
            replace(key, newValue);
            return true;
        }
        return false;
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Double, ? extends Double> function) {
        throw new UnsupportedOperationException(); // too much
    }

    @Override
    public void putAll(Map<? extends String, ? extends Double> mapToAdd) {
        for (String key : mapToAdd.keySet()) {
            put(key, get(key));
        }
    }

    @Override
    public Double putIfAbsent(String key, Double value) {
        if (get(key) == null) {
            return put(key, value);
        } else {
            return null;
        }
    }

    @Override
    public Double remove(Object key) {
        final Double removeReturnValue = super.remove(key);
        if (removeReturnValue != null) {
            totalCount -= get(key);
        }
        return removeReturnValue;
    }

    @Override
    public Double put(String key, Double count) {
        if (filteredKeys.contains(key)){
            filteredCount++;
            return filteredCount;
        }
        Double newCount = count;
        final Double currentCount = get(key);
        if (currentCount == null) {
            super.put(key, count);
            totalCount += count;
        }
        else {
            newCount = currentCount + count;
            replace(key, newCount);
        }
        return newCount;
    }

}