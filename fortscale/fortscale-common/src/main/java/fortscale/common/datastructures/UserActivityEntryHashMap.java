package fortscale.common.datastructures;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class UserActivityEntryHashMap extends HashMap<String, Integer> {

    private static final String OTHER_NAME = "other";

    private int totalCount = 0;

    public Set<Entry<String, Integer>> getTopEntries(int limit) {
        Set<Entry<String, Integer>> topEntries = this.entrySet()
                .stream()
                .sorted((entrySet, entrySet2) -> -Integer.compare(entrySet.getValue(), entrySet2.getValue())) //sort them by count (reverse order - we want the bigger values in the beginning)
                .limit(limit)                   //take only the top 'limit-number' of entries
                .collect(Collectors.toSet());   //of entries


        final int topCount = topEntries.stream().mapToInt(Entry::getValue).sum();
        topEntries.add(new SimpleEntry<>(OTHER_NAME, totalCount - topCount));

        return topEntries;
    }

    @Override
    public Integer put(String key, Integer count) {
        Integer newCount = count;
        final Integer currentCount = get(key);
        if (currentCount == null) {
            super.put(key, count);
        }
        else {
            newCount = currentCount + count;
            replace(key, newCount);
        }

        totalCount += count;
        return newCount;
    }
}
