package com.rsa.netwitness.presidio.automation.log_player;

import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Supplier;

public class PropertiesHolder {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(PropertiesHolder.class.getName());

    public static final String LOG_DECODER_IP = "log_decoder.ip";

    public Properties getEnvProperties() {
        return envProperties.get();
    }



    
    public static PropertiesHolder getInstance(){
        return SingletonHelper.INSTANCE;
    }

    private PropertiesHolder(){ }

    private static class SingletonHelper{
        private static final PropertiesHolder INSTANCE = new PropertiesHolder();
    }

    interface Lazy<T> extends Supplier<T> {
        Supplier<T> init();
        public default T get() { return init().get(); }
    }
    private static <U> Supplier<U> lazily(Lazy<U> lazy) { return lazy; }
    private static <T> Supplier<T> value(T value) { return ()->value; }
    private Supplier<Properties> envProperties = lazily(() -> envProperties =value(load("/home/presidio/env.properties")));


    private static Properties load(final String path) {
        Properties properties = new Properties();
        try {
            LOGGER.debug("Loading  " + path);
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
      return properties;
    }
}
