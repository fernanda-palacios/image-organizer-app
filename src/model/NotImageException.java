package model;

import java.nio.file.FileSystemException;

/**
 * Thrown when a file that is not an image is passed in as such
 *
 */
public class NotImageException extends FileSystemException {
	private static final long serialVersionUID = 5239490740907226196L;

	public NotImageException(String file) {
		super(file);
	}

	public NotImageException(String file, String other, String reason) {
		super(file, other, reason);
	}

}
