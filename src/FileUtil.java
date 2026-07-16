import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File utility class for reading source files.
 */
public final class FileUtil {

    private FileUtil() {
    }

    /**
     * Reads text file with UTF-8 first, then retries with MS932 (common in legacy environments).
     */
    public static String readFile(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("ファイルパスが空です。");
        }

        Path path = Path.of(filePath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalArgumentException("指定ファイルが存在しません: " + filePath);
        }

        String lower = path.getFileName().toString().toLowerCase();
        if (!(lower.endsWith(".cbl") || lower.endsWith(".cob") || lower.endsWith(".cpy"))) {
            throw new IllegalArgumentException("拡張子は .cbl / .cob / .cpy のみ対応です。");
        }

        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException utf8Error) {
            Charset ms932 = Charset.forName("MS932");
            return Files.readString(path, ms932);
        }
    }
}
