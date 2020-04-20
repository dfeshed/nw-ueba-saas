package com.rsa.netwitness.presidio.automation.file;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public enum TargetFileAppender {
    INSTANCE;

    private ConcurrentHashMap<String, FileWriter> fileWriters;

    TargetFileAppender() {
        fileWriters = new ConcurrentHashMap<>();
    }

    public FileWriter get(Path targetPath) {
        Objects.requireNonNull(targetPath);
        fileWriters.computeIfAbsent(targetPath.toString(),  e -> new FileWriter(true, targetPath));
        return fileWriters.get(targetPath.toString());
    }
}
