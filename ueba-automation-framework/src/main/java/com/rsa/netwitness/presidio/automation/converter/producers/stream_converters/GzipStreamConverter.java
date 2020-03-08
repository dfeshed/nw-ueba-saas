package com.rsa.netwitness.presidio.automation.converter.producers.stream_converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class GzipStreamConverter implements ProducerStreamConverter<byte[], String> {

    @Override
    public byte[] convert(List<String> lines) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);

            for (String line : lines) {
                gzipOut.write(line.getBytes());
            }

            gzipOut.flush();
            gzipOut.close();
            byteOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return byteOut.toByteArray();
    }
}
