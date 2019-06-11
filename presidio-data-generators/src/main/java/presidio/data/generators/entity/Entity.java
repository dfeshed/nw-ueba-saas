package presidio.data.generators.entity;

import org.springframework.util.DigestUtils;
import java.util.function.Supplier;

public class Entity {

    public String ja3;
    public String sslSubject;

    public Entity(String ja3, String sslSubject) {
        this.ja3 = ja3;
        this.sslSubject = sslSubject;
    }


    public Supplier<String> MD5 = () -> DigestUtils.md5DigestAsHex(ja3.concat(sslSubject).getBytes());

}
