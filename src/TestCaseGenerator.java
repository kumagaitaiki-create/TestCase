import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates test viewpoints from COBOL parse result.
 *
 * Required viewpoints:
 * - Normal
 * - Abnormal
 * - Boundary
 * - File abnormal
 */
public class TestCaseGenerator {

    private static final String CATEGORY_NORMAL = "【正常系】";
    private static final String CATEGORY_ABNORMAL = "【異常系】";
    private static final String CATEGORY_BOUNDARY = "【境界値】";
    private static final String CATEGORY_FILE_ABNORMAL = "【ファイル異常】";
    private static final int NO_OPERATION_COUNT = 0;
    private static final int PERFORM_MANY_THRESHOLD = 3;

    public String generateViewpoints(CobolParser.ParseResult result) {
        // 追加機能:
        // 試験観点生成機能
        RuleContext context = new RuleContext(result);
        Map<String, List<String>> viewpoints = initializeCategories();

        applyReadRules(context, viewpoints);
        applyWriteRules(context, viewpoints);
        applyCallRules(context, viewpoints);
        applyOpenCloseRules(context, viewpoints);
        applyFileDefinitionRules(context, viewpoints);
        applyPerformRules(context, viewpoints);

        return buildOutput(viewpoints);
    }

    private Map<String, List<String>> initializeCategories() {
        Map<String, List<String>> categories = new LinkedHashMap<>();
        categories.put(CATEGORY_NORMAL, new ArrayList<>());
        categories.put(CATEGORY_ABNORMAL, new ArrayList<>());
        categories.put(CATEGORY_BOUNDARY, new ArrayList<>());
        categories.put(CATEGORY_FILE_ABNORMAL, new ArrayList<>());
        return categories;
    }

    private void applyReadRules(RuleContext context, Map<String, List<String>> viewpoints) {
        if (!context.hasOperation("READ")) {
            return;
        }
        add(viewpoints, CATEGORY_NORMAL, "入力ファイル正常");
        add(viewpoints, CATEGORY_NORMAL, "入力ファイル0件");
        add(viewpoints, CATEGORY_FILE_ABNORMAL, "入力ファイル未存在");
        add(viewpoints, CATEGORY_BOUNDARY, "入力件数境界値");
    }

    private void applyWriteRules(RuleContext context, Map<String, List<String>> viewpoints) {
        if (!context.hasOperation("WRITE")) {
            return;
        }
        add(viewpoints, CATEGORY_NORMAL, "出力ファイル作成確認");
        add(viewpoints, CATEGORY_NORMAL, "出力件数確認");
    }

    private void applyCallRules(RuleContext context, Map<String, List<String>> viewpoints) {
        if (!context.hasOperation("CALL")) {
            return;
        }
        add(viewpoints, CATEGORY_NORMAL, "CALL先正常終了");
        add(viewpoints, CATEGORY_ABNORMAL, "CALL先異常終了");
        add(viewpoints, CATEGORY_BOUNDARY, "戻り値確認");
    }

    private void applyOpenCloseRules(RuleContext context, Map<String, List<String>> viewpoints) {
        if (!context.hasOperation("OPEN") && !context.hasOperation("CLOSE")) {
            return;
        }
        add(viewpoints, CATEGORY_FILE_ABNORMAL, "オープン失敗");
        add(viewpoints, CATEGORY_FILE_ABNORMAL, "クローズ異常");
    }

    private void applyFileDefinitionRules(RuleContext context, Map<String, List<String>> viewpoints) {
        if (!context.hasFileDefinition()) {
            return;
        }
        add(viewpoints, CATEGORY_FILE_ABNORMAL, "ファイルレイアウト不一致");
        add(viewpoints, CATEGORY_FILE_ABNORMAL, "ファイル定義差異");
    }

    private void applyPerformRules(RuleContext context, Map<String, List<String>> viewpoints) {
        if (context.getOperationCount("PERFORM") <= PERFORM_MANY_THRESHOLD) {
            return;
        }
        add(viewpoints, CATEGORY_NORMAL, "分岐単位の確認");
        add(viewpoints, CATEGORY_BOUNDARY, "繰返し回数境界値");
    }

    //20260713 ADD START
    //private void applyAdditionalFunction(Rulecontext context, Map<String, List<String>> viewpoints){
    //  if (!context.hasOperation("CHG") || !context.hasOperation("CHANGE") || !context.hasOperation("MOD") || !context.hasOperation("UPDATE") || !context.hasOperation("ADD") || !context.hasOperation("MODIFY")  {
    //      return;
    //}
    //  add (viewpoints, CATEGORY_NORMAL, "追加機能正常");
    //  add (viewpoints, CATEGORY_ABNORMAL, "追加機能異常");
    //}
    //20260713 ADD END

    private void add(Map<String, List<String>> viewpoints, String category, String item) {
        List<String> items = viewpoints.get(category);
        if (!items.contains(item)) {
            items.add(item);
        }
    }

    private String buildOutput(Map<String, List<String>> viewpoints) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : viewpoints.entrySet()) {
            sb.append(entry.getKey()).append(System.lineSeparator());
            if (entry.getValue().isEmpty()) {
                sb.append("- 該当する観点なし").append(System.lineSeparator());
            } else {
                for (String item : entry.getValue()) {
                    sb.append("- ").append(item).append(System.lineSeparator());
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    // 将来の差分比較機能で TestRule / TestRuleEngine を導入しやすいよう、
    // 解析結果参照を RuleContext に集約する。
    private static class RuleContext {
        private final CobolParser.ParseResult result;

        private RuleContext(CobolParser.ParseResult result) {
            this.result = result;
        }

        private boolean hasOperation(String operation) {
            return getOperationCount(operation) > NO_OPERATION_COUNT;
        }

        private int getOperationCount(String operation) {
            return result.operationCounts().getOrDefault(operation, NO_OPERATION_COUNT);
        }

        private boolean hasFileDefinition() {
            return !result.selectFiles().isEmpty() || !result.fdFiles().isEmpty();
        }
    }
}
