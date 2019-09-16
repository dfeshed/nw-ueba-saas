package com.rsa.netwitness.presidio.automation.file;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.TARGET_DIR;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.stream.Collectors.toList;

public class FileWriter {
    private static final long ROTATION_SIZE_BYTES = 2000000;
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(FileWriter.class);

    private final Path path;

    FileWriter(boolean deleteExisting, Path targetFilePath) {
        Objects.requireNonNull(targetFilePath);
        path = Paths.get(TARGET_DIR.toString(), targetFilePath.toString());
        if (deleteExisting) {
            LOGGER.info("Going to delete " + path);
            try {
                Files.deleteIfExists(path);
                Files.deleteIfExists(Paths.get(path.toString(), ".1"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Path append(String line) {
        return appendToTargetDirFile(Stream.of(line));
    }

    public Path append(Stream<String> lines) {
        return appendToTargetDirFile(lines);
    }

    public Path appendNotDelete(Stream<String> lines) {
        return appendToTargetDirFile(lines);
    }

    Path appendToTargetDirFile(Stream<String> lines) {
        return appendLines(path, lines);
    }


    private Path appendLines(Path path, Stream<String> lines) {
        if (path != null) {
            try {
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                return Files.write(path, lines.collect(toList()), CREATE, APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LOGGER.error("Failed append lines into " + Objects.requireNonNull(path).toString());
        return null;
    }

    private void rotateIfNeed() throws IOException {
        if (Files.exists(path)) {
            long bytesSize = Files.size(path);
            if (bytesSize > ROTATION_SIZE_BYTES) {
                Path nextPath = Paths.get(path.toString(), ".1");
                Files.move(path, nextPath, REPLACE_EXISTING);
            }
        }
    }


}