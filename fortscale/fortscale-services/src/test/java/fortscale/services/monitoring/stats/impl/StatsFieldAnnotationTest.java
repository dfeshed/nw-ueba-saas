package fortscale.services.monitoring.stats.impl;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gaashh on 4/4/16.
 */



class FooException extends RuntimeException {
    FooException(String msg) {
        super(msg);
    }
}

class GooException extends RuntimeException {
    GooException(String msg, Throwable cause) {
        super(msg, cause);
    }
}




public class StatsFieldAnnotationTest {

    private static Logger logger = LoggerFactory.getLogger(StatsFieldAnnotationTest.class);


    public void func2 () {
        System.out.println("func2-enter");
        throw new FooException("AAAAA") ;
        //System.out.println("func2-exit");

    }
    public void func1() {
        System.out.println("func1-enter");
        try {
            func2();
        }
        catch (RuntimeException ex) {
            throw new GooException("BBBBB", ex);
        }
        System.out.println("func1-exit");
    }

    @Test
    public void ExceptionsTest()  {



        System.out.println("top-enter");
        try {
            func1();
        }
        catch (RuntimeException ex) {
            logger.warn("LOGGGGG", ex);
        }
        System.out.println("top-exit");


    }
}
