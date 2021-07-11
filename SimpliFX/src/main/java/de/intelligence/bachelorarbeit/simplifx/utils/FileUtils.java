package de.intelligence.bachelorarbeit.simplifx.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtils {

    private FileUtils() {}

    public static List<Path> getFilesFromClasspathDirectory(String directory) {
        try {
            return getFilesFromClasspath(directory);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException(
                    String.format("An error occurred while trying to retrieve all files from classpath directory %s",
                            directory), e);
        }
    }

    private static List<Path> getFilesFromClasspath(String directory) throws URISyntaxException, IOException {
        final URL url = FileUtils.class.getResource(directory);
        if (url == null) {
            throw new IllegalArgumentException("Invalid directory specified!");
        }
        final URI uri = url.toURI();

        if ("jar".equals(uri.getScheme())) {
            try (FileSystem system = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                return getFilesFromPath(system.getPath(directory));
            }
        }
        return getFilesFromPath(Paths.get(uri));
    }

    private static List<Path> getFilesFromPath(Path path) throws IOException {
        try (Stream<Path> pathStream = Files.walk(path, 1)) {
            return pathStream.filter(Files::isRegularFile).collect(Collectors.toList());
        }
    }

    public static String getFileExtension(String file) {
        final String fileName = new File(file).getName();
        final int index = fileName.lastIndexOf('.');
        return (index == -1) ? "" : fileName.substring(index + 1);
    }

    public static String getNameWithoutExtension(String file) {
        final String fileName = new File(file).getName();
        final int index = fileName.lastIndexOf('.');
        return (index == -1) ? fileName : fileName.substring(0, index);
    }

}
