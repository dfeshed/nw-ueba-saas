package fortscale.utils.hierarchy;

import java.util.Iterator;

public class HierarchyIterator implements Iterable<String> {

    private final String path;

    public HierarchyIterator(String path) {
        this.path = path;
    }

    @Override
    public Iterator<String> iterator() {
        return new HierarchyNestedIterator(path);
    }

    private static class HierarchyNestedIterator implements Iterator<String> {
        private static final String FIELD_NAME_DELIMITING_REGEX = "\\.";
        private int currentIndex = 0;
        private String[] objects;

        HierarchyNestedIterator(String path) {
            this.objects = path.split(FIELD_NAME_DELIMITING_REGEX);
        }

        @Override
        public boolean hasNext() {
            return currentIndex < objects.length;
        }

        @Override
        public String next() {
            return objects[currentIndex++];
        }

    }
}
