package com.rsa.netwitness.presidio.automation.converter.producers.stream_converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;


public class GzipStreamConverter implements ProducerStreamConverter<byte[], String> {

    @Override
    public byte[] convert(List<String> lines) {
        byte[] bytesToWrite;
        bytesToWrite = String.join("", lines).getBytes();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut;

        try {
            gzipOut = new GZIPOutputStream(byteOut);
            gzipOut.write(bytesToWrite, 0, bytesToWrite.length);
            gzipOut.flush();
            gzipOut.close();
            byteOut.close();
            return byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
