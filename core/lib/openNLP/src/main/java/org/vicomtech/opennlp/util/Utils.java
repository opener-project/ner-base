package org.vicomtech.opennlp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

	public static final int ERROR_STATUS = -1;
	
	public static Path getPath(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		if (!exists(path)) {
			throw new IOException(String.format("file '%s' not exists", filePath));
		}
		return path;
	}
	
	public static boolean exists(Path filePath) {
		return filePath.toFile().exists();
	}
	
	public static boolean exists(String filePath) {
		return Paths.get(filePath).toFile().exists();
	}
	
	public static InputStream path2Stream(String model_path) throws FileNotFoundException {
		return new FileInputStream(model_path);
	}

}
