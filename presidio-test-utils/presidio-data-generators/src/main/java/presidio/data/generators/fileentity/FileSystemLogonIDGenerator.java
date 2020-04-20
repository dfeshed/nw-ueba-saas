package presidio.data.generators.fileentity;


import presidio.data.generators.common.CustomStringGenerator;

public class FileSystemLogonIDGenerator extends CustomStringGenerator {

    private final String username;

    public FileSystemLogonIDGenerator(String username) {
        this.username = username;
    }

    public String convertUserNameToHex() {
        String dictionary = "0123456789abcdef";
        StringBuilder build = new StringBuilder("0x0,0x");
        for(int i = 0 ; i < username.length() ; i++){
            build.append(dictionary.charAt((int)username.charAt(i)%16));
        }

        return build.toString();
    }

    @Override
    public String getNext() {
        return convertUserNameToHex();
    }
}
