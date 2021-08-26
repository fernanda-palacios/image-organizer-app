package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class FileAccesor implements Accesor {

	@Override
	public boolean exists(Path path) {
		return Files.exists(path);
	}

	@Override
	public Path toRealPath(Path path) throws IOException {
		return path.toRealPath();
	}

	@Override
	public void move(Path source, Path dest) throws IOException {
		Files.move(source, dest);

	}

	@Override
	public InputStream newInputStream(Path path) throws IOException {
		return Files.newInputStream(path);
	}

	@Override
	public boolean isDirectory(Path path) {
		return Files.isDirectory(path);
	}

	@Override
	public void createDirectory(Path path) throws IOException {
		Files.createDirectory(path);

	}

	@Override
	public OutputStream newOutputStream(Path path) throws IOException {
		return Files.newOutputStream(path);
	}

	@Override
	public List<Path> getChildPaths(Path dir) throws IOException {
		LinkedList<Path> lst = new LinkedList<>();
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				lst.add(path);
				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(dir, EnumSet.noneOf(FileVisitOption.class), 1, visitor);
		return lst;
	}
}
