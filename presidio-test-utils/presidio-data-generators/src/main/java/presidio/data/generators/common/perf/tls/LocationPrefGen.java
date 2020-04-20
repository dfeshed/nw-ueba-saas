package presidio.data.generators.common.perf.tls;

import presidio.data.domain.Location;
import presidio.data.generators.common.list.content.Locations;
import presidio.data.generators.common.perf.ConstantPrefGen;

import java.util.List;

public class LocationPrefGen extends ConstantPrefGen<Location> {

    public LocationPrefGen(int uniqueId, int amount) {
        super(uniqueId, amount);
    }

    @Override
    protected List<Location> getConstantCollection() {
        return Locations.LOCATIONS;
    }

}
