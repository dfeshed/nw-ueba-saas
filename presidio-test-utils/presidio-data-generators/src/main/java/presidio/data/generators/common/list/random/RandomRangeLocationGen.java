package presidio.data.generators.common.list.random;

import com.google.common.collect.ImmutableList;
import presidio.data.domain.Location;
import presidio.data.generators.common.list.ListBasedGen;

import static presidio.data.generators.common.list.content.Locations.LOCATIONS;


public class RandomRangeLocationGen extends ListBasedGen<Location> {

    public RandomRangeLocationGen(int fromIndex, int size) {
        super(0, size, LOCATIONS.size());
        String concatIndex = " " + fromIndex;
        formatter = e -> new Location(e.getState().concat(concatIndex),
                e.getCountry().concat(concatIndex), e.getCity().concat(concatIndex));
    }

    @Override
    public Location getNext() {
        return getNextRandom();
    }

    @Override
    protected ImmutableList<Location> getList() {
        return LOCATIONS;
    }
}
