package presidio.data.generators.fileentity;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class FileSystemLogonIDGenerator extends AbstractCyclicValuesGenerator implements IStringGenerator {

    public FileSystemLogonIDGenerator(String userName) {
        super(convertUserNameToHex(userName));
    }

    public static String convertUserNameToHex(String userName) {
        String dictionary = "0123456789abcdef";
//        StringBuilder build = new StringBuilder("0x0,0x");
        String build = "0x0,0x";
        for(int i = 0 ; i < userName.length() ; i++){
//            build.append(dictionary.charAt((int)userName.charAt(i)%16));
            build += dictionary.charAt((int)userName.charAt(i)%16);
        }

        return build;
    }
}
