package presidio.data.generators.common.perf.lists;

import presidio.data.domain.Location;
import presidio.data.generators.common.list.content.Locations;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static presidio.data.generators.common.list.content.Ipv4.indexToIpv4;

public class PerfGenListMappers {
    private static  List<Location> distinctCountries = Locations.LOCATIONS.parallelStream().collect(groupingBy(Location::getCountry))
            .values().stream()
            .map(locationList -> locationList.get(0)).collect(toList());

    public static LimitedListMapper<String> sslSubjectMapper(int size) {
        return new LimitedListMapper<>(size, e ->  "International Business Machines Corporation " + e);
    }

    public static LimitedListMapper<String> dstOrgMapper(int size) {
        return new LimitedListMapper<>(size, e ->  "international business machines org. " + e);
    }

    public static LimitedListMapper<String> hostnameMapper(int size) {
        return new LimitedListMapper<>(size, e ->  "xxx.google" + e + ".com");
    }

    public static LimitedListMapper<String> ja3Mapper(int size) {
        return new LimitedListMapper<>(size, e ->  "6e1932bea4b34db0b3cb4fsdfsd-" + e);
    }

    public static LimitedListMapper<Location> countriesMapper(int size) {
        return new LimitedListMapper<>(size, distinctCountries::get);
    }

    public static  LimitedListMapper<String>  ipv4Mapper(int size) {
        return new LimitedListMapper<>(size, e -> indexToIpv4.apply(e));
    }
}
