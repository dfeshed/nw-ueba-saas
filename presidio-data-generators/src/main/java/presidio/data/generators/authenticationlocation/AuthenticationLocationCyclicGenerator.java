package presidio.data.generators.authenticationlocation;

import presidio.data.domain.Location;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.ILocationGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationLocationCyclicGenerator extends CyclicValuesGenerator<Location> implements ILocationGenerator {

    private static final Location[] LOCATIONS = getDefaultAuthenticationLocation();

    private static Location[] getDefaultAuthenticationLocation(){
        List<Location> locationList = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("src/main/resources/data/Location.txt"));
            String line;
            while((line=br.readLine()) != null){

                String str[] = line.split(",");
                if (str.length < 3) continue; // bad row format
                locationList.add(new Location(str[0], str[1], str[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationList.toArray(new Location[locationList.size()]);
    }

    public AuthenticationLocationCyclicGenerator() { super(LOCATIONS); }
    public AuthenticationLocationCyclicGenerator(Location[] customList) { super(customList); }
    public AuthenticationLocationCyclicGenerator(Location customLocation) { super(customLocation); }
}
