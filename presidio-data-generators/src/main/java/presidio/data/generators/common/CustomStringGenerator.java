package presidio.data.generators.common;

public class CustomStringGenerator implements IStringGenerator {
    private final String customString;

    public CustomStringGenerator(){
        customString = "Default Custom String";
    }

    public CustomStringGenerator(String customString){
        this.customString = customString;
    }

    public String getNext(){
        return customString;
    }
}
