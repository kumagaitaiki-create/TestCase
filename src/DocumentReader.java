import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class DocumentReader {

    public static List<File> findFiles(File dir,String programName) {
        List<File> result = new ArrayList<>();
        search(dir, programName, result);
        return result;
    }
    private static void search(File dir, String programName, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
            }
            
            for (File file : files) {
                if (file.isDirectory()) {
                    search(file, programName, result);
                }
        else if (file.getName().contains(programName)) {
                    result.add(file);
                }
            }
        }
    }
