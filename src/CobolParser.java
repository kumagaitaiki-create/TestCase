
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * COBOL source parser using regular expressions.
 *
 * This class extracts:
 * - Program name
 * - CALL targets
 * - COPY books
 * - SELECT files
 * - FD files
 * - Operation counts (READ/WRITE/OPEN/CLOSE/CALL/PERFORM)
 */
public class CobolParser {

    private static final List<String> SUMMARY_START_KEYWORDS = List.of("変    更    履    歴", "変　　更　　履　　歴");
    private static final List<String> SUMMARY_END_KEYWORDS = List.of("入力", "出力", "更新履歴", "変更履歴", "処理内容", "注意事項");

    private static final Pattern PROGRAM_ID_PATTERN = Pattern.compile("(?im)^\\s*PROGRAM-ID\\.\\s+([A-Z0-9_-]+)");
    private static final Pattern CALL_PATTERN = Pattern.compile("(?im)\\bCALL\\s+['\"]?([A-Z0-9_-]+)['\"]?");
    private static final Pattern COPY_PATTERN = Pattern.compile("(?im)\\bCOPY\\s+([A-Z0-9_-]+)");
    private static final Pattern SELECT_PATTERN = Pattern.compile("(?im)^\\s*SELECT\\s+([A-Z0-9_-]+)");
    private static final Pattern FD_PATTERN = Pattern.compile("(?im)^\\s*FD\\s+([A-Z0-9_-]+)");

    public ParseResult parse(String source) {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("COBOLソースが空です。内容を確認してください。");
        }

        // 追加機能:
        // 概要抽出機能
        String extractedOverview = extractOverviewSection(source);
        String normalized = normalizeForParse(source);

        String programName = extractProgramName(normalized);
        List<String> callTargets = extractUnique(CALL_PATTERN, normalized);
        List<String> copyBooks = extractUnique(COPY_PATTERN, normalized);
        List<String> selectFiles = extractUnique(SELECT_PATTERN, normalized);
        List<String> fdFiles = extractUnique(FD_PATTERN, normalized);
        Map<String, Integer> operationCounts = countOperations(normalized);

        return new ParseResult(programName, callTargets, copyBooks, selectFiles, fdFiles, operationCounts, extractedOverview);
    }

    // 追加機能:
    // 概要抽出機能
    private String extractOverviewSection(String source) {
        List<String> lines = Arrays.asList(source.split("\\r?\\n"));
        int startIndex = findStartLineIndex(lines);
        if (startIndex < 0) {
            return "";
        }

        StringBuilder summary = new StringBuilder();
        for (int i = startIndex; i < lines.size(); i++) {
            String current = lines.get(i).trim();
            if (isSummaryEndLine(current, i > startIndex)) {
                break;
            }
            if (!current.isEmpty()) {
                summary.append(current).append(System.lineSeparator());
            }
        }
        return summary.toString().trim();
    }

    private int findStartLineIndex(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (containsAnyKeyword(line, SUMMARY_START_KEYWORDS)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isSummaryEndLine(String line, boolean allowEndCheck) {
        if (!allowEndCheck || line.isEmpty()) {
            return false;
        }
        return containsAnyKeyword(line, SUMMARY_END_KEYWORDS);
    }

    private boolean containsAnyKeyword(String line, List<String> keywords) {
        for (String keyword : keywords) {
            if (line.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeForParse(String source) {
        // Remove comment lines for simpler regex parsing.
        String[] lines = source.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.stripLeading();
            if (trimmed.startsWith("*") || trimmed.startsWith("/") || trimmed.startsWith("D ")) {
                continue;
            }
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString().toUpperCase(Locale.ROOT);
    }

    private String extractProgramName(String source) {
        Matcher matcher = PROGRAM_ID_PATTERN.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "UNKNOWN";
    }

    private List<String> extractUnique(Pattern pattern, String source) {
        Set<String> unique = new LinkedHashSet<>();
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            unique.add(matcher.group(1));
        }
        return new ArrayList<>(unique);
    }

    private Map<String, Integer> countOperations(String source) {
        return Map.of(
                "READ", countKeyword(source, "READ"),
                "WRITE", countKeyword(source, "WRITE"),
                "OPEN", countKeyword(source, "OPEN"),
                "CLOSE", countKeyword(source, "CLOSE"),
                "CALL", countKeyword(source, "CALL"),
                "PERFORM", countKeyword(source, "PERFORM")
        );
    }

    private int countKeyword(String source, String keyword) {
        Pattern pattern = Pattern.compile("(?im)\\b" + Pattern.quote(keyword) + "\\b");
        Matcher matcher = pattern.matcher(source);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Immutable parse output model.
     */
    public record ParseResult(
            String programName,
            List<String> callTargets,
            List<String> copyBooks,
            List<String> selectFiles,
            List<String> fdFiles,
            Map<String, Integer> operationCounts,
            String extractedOverview
    ) {
    }
}
