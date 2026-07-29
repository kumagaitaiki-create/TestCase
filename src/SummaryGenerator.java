
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates a readable process summary from parse result.
 */
public class SummaryGenerator {

    private static final int NO_OPERATION_COUNT = 0;

    public String generateSummary(CobolParser.ParseResult result) {
        // 追加機能:
        // 概要抽出機能
        String extractedOverview = result.extractedOverview();
        if (extractedOverview != null && !extractedOverview.isBlank()) {
            return extractedOverview;
        }

        return generateSummaryFromParseResult(result);
    }

    private String generateSummaryFromParseResult(CobolParser.ParseResult result) { 
        Map<String, Integer> ops = result.operationCounts();
        List<String> elements = new ArrayList<>();

        if (hasOperation(ops, "READ")) {
            elements.add("入力データを読み込む");
        }
        if (hasOperation(ops, "WRITE")) {
            elements.add("出力処理を行う");
        }
        if (hasOperation(ops, "CALL")) {
            elements.add("外部プログラムを呼び出す");
        }
        if (!result.selectFiles().isEmpty() || !result.fdFiles().isEmpty()) {
            elements.add("ファイル入出力を行う");
        }
        if (!result.copyBooks().isEmpty()) {
            elements.add("共通定義を利用する");
        }

        StringBuilder sb = new StringBuilder(buildNaturalSentence(elements));
        sb.append(System.lineSeparator());
        sb.append(buildOperationSummary(ops));

        //20260729 CHG-START
        //if (question != null && !question.isBlank()) {
        //    sb.append(System.lineSeparator());
        //    sb.append("質問への補足: ").append(question).append(" の観点で上記要素を重点確認してください。");
        //}
        //20260729 CHG-END

        return sb.toString();
    }

    private boolean hasOperation(Map<String, Integer> ops, String key) {
        return ops.getOrDefault(key, NO_OPERATION_COUNT) > NO_OPERATION_COUNT;
    }

    private String buildNaturalSentence(List<String> elements) {
        if (elements.isEmpty()) {
            return "有効な処理要素を検出できないため、処理概要を特定できません。";
        }
        return "このプログラムは「" + String.join("、", elements) + "」処理です。";
    }

    private String buildOperationSummary(Map<String, Integer> ops) {
        return "操作回数: "
                + "READ=" + ops.getOrDefault("READ", NO_OPERATION_COUNT) + ", "
                + "WRITE=" + ops.getOrDefault("WRITE", NO_OPERATION_COUNT) + ", "
                + "OPEN=" + ops.getOrDefault("OPEN", NO_OPERATION_COUNT) + ", "
                + "CLOSE=" + ops.getOrDefault("CLOSE", NO_OPERATION_COUNT) + ", "
                + "CALL=" + ops.getOrDefault("CALL", NO_OPERATION_COUNT) + ", "
                + "PERFORM=" + ops.getOrDefault("PERFORM", NO_OPERATION_COUNT);
    }
}
