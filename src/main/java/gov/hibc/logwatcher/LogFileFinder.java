package gov.hibc.logwatcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class LogFileFinder {

    public static void main(String[] args) throws IOException {
        if (args.length == 2)
            printLogFiles(args[0], args[1]);
        else
            printLogFiles("/tlp/tlp2/logs", "glob:**/*.log");
    }

    public static void printLogFiles(String folder, String glob) throws IOException {
        Path start = Paths.get(folder);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(glob); // "glob:**/*.log");

        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (matcher.matches(file)) {
                    System.out.println(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return FileVisitResult.CONTINUE;
            }
        });
    }
}