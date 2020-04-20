package presidio.data.generators.fileentity;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class SimplePathGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {
    private static final String[] DEFAULT_PATHS_ARRAY = {"/usr/someuser/somesubdir/1/", "/usr/someuser/somesubdir/2/"};

    public SimplePathGenerator() {
        super(DEFAULT_PATHS_ARRAY);
    }

    public SimplePathGenerator(String fileOfPathsList) {
        super(buildValues(fileOfPathsList));

    }

    public SimplePathGenerator(String[] paths) {
        super(paths);
    }


    public SimplePathGenerator(InputStream stream) {
        super(buildValues(stream));
    }

    private static String[] buildValues(File file){
        // read list of paths from file into array
        List<String> result = new ArrayList<String>();
        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.add(line);
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] resultArray = new String[result.size()];
        resultArray = result.toArray(resultArray);

        return resultArray;
    }

    private static String[] buildValues(InputStream stream){
        // read list of paths from file into array
        List<String> result = new ArrayList<String>();

        try (Scanner scanner = new Scanner(stream)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result.add(line);
        }}

        String[] resultArray = new String[result.size()];
        resultArray = result.toArray(resultArray);

        return resultArray;
    }

    private static String[] buildValues(String fileOfPathsList){
        File file = new File(fileOfPathsList);
        return buildValues(file);
    }
}
