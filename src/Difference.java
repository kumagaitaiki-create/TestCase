import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Difference {
    //20260729 ADD START
    public static boolean difFlag;
    //20260729 ADD END
    public String compFiles(File oldFile, File newFile) throws IOException {
        List<String> oldLines = Files.readAllLines(oldFile.toPath());
        List<String> newLines = Files.readAllLines(newFile.toPath());

        StringBuilder result = new StringBuilder();

        int maxLine = Math.max(oldLines.size(),newLines.size());

        difFlag = false;

        for (int i = 0; i < maxLine; i++) {

            String oldLine =
                i < oldLines.size()
                ? oldLines.get(i)
                : "";

            String newLine =
                i < newLines.size()
                ? newLines.get(i)
                : "";

            if (!oldLine.equals(newLine)) {

                result.append("【差分あり】")
                  .append(System.lineSeparator());

                result.append("行番号 : ")
                  .append(i + 1)
                  .append(System.lineSeparator());

                result.append("旧 : ")
                  .append(oldLine)
                  .append(System.lineSeparator());

                result.append("新 : ")
                  .append(newLine)
                  .append(System.lineSeparator());

                result.append(System.lineSeparator());

                difFlag = true;
            }
        }
        if (result.length() == 0) {
            result.append("差分はありません。");
        }

        return result.toString();
    }
}