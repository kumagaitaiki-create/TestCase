public class DesigneDocumentReader {
    public String readDesignDocument(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString();
    }
}