package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public interface Accesor {

	public boolean exists(Path path);

	public Path toRealPath(Path path) throws IOException;

	public void move(Path source, Path dest) throws IOException;

	public boolean isDirectory(Path path);

	public InputStream newInputStream(Path path) throws IOException;

	public void createDirectory(Path path) throws IOException;

	public OutputStream newOutputStream(Path path) throws IOException;

	public List<Path> getChildPaths(Path path) throws IOException;

}
