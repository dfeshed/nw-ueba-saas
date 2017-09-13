package org.apache.flume.persistency.mongo;


import com.mongodb.*;
import org.apache.commons.net.util.Base64;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MongoUtils {

    public static MongoTemplate createMongoTemplate(String dbName, String host, int port, String username, String password) throws UnknownHostException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        final MongoClient mongoClient = createMongoClient(dbName, host, port, username, password);
        return new MongoTemplate(new SimpleMongoDbFactory(mongoClient, dbName));
    }

    private static MongoClient createMongoClient(String dbName, String host, int port, String username, String password) throws UnknownHostException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        MongoClient client;
        MongoClientOptions writeOptions = MongoClientOptions.builder()
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                .build();
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            ServerAddress address = new ServerAddress(host, port);
            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(
                    MongoCredential.createCredential(
                            username,
                            dbName,
                            decrypt(password).toCharArray()
                    )
            );

            client = new MongoClient(address, credentials, writeOptions);
        } else {
            client = new MongoClient(host, port);
        }
        return client;
    }

    private static String decrypt(String encrypted) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

        return new String(plainBytes);
    }

    private static Cipher getCipher(int cipherMode) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        String encryptionAlgorithm = "AES";
        String encryptionKey = "FortScale4Ever!!";
        SecretKeySpec keySpecification = new SecretKeySpec(
                encryptionKey.getBytes("UTF-8"), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }
}
