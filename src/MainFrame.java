import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.awt.CardLayout;
import java.awt.Color;

import java.util.List;

public class MainFrame extends JFrame{
    public boolean difFlag = false;
    private final JTextField oldFileField;
    private final JTextField newFileField;
    private final JTextField questionField;
    private final JTextArea memoArea;

    private final JTextArea parseArea;
    private final JTextArea diffArea;
    private final JTextArea testArea;

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final CobolParser cobolParser;
    private final SummaryGenerator summaryGenerator;
    private final TestCaseGenerator testCaseGenerator;
    public MainFrame(){
        this.cobolParser = new CobolParser();
        this.summaryGenerator = new SummaryGenerator();
        this.testCaseGenerator = new TestCaseGenerator();


        //タイトルや画面サイズなどの初期設定
        setTitle("COBOL Assistant(新規プログラム用)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        //タイトルラベルの位置
        JLabel titleLabel = new JLabel("COBOL Assistant", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        //ボタン等の配置を決める事前準備
        //画面切り替えルール
        this.cardLayout = new CardLayout();
        //CardLayoutを使う箱
        this.cardPanel = new JPanel(cardLayout);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        //解析結果画面
        this.parseArea = new JTextArea();
        JPanel parsePanel = new JPanel(new BorderLayout());
        parsePanel.add(new JScrollPane(this.parseArea),BorderLayout.CENTER);

        //差分画面
        this.diffArea = new JTextArea();
        JPanel diffPanel = new JPanel(new BorderLayout());
        diffPanel.add(new JScrollPane(this.diffArea),BorderLayout.CENTER);

        //試験観点画面
        this.testArea = new JTextArea();
        JPanel testPanel = new JPanel(new BorderLayout());
        testPanel.add(new JScrollPane(this.testArea),BorderLayout.CENTER);

        //CardLayoutに登録
        cardPanel.add(parsePanel,"PARSE");
        cardPanel.add(diffPanel, "DIFF");
        cardPanel.add(testPanel,"TEST");

        //切り替えボタン
        JButton parseButton =
            new JButton("解析結果");
        JButton diffButton =
            new JButton("差分結果");
        JButton testButton =
            new JButton("試験観点");

        //ボタンクリック時
        parseButton.addActionListener(e -> cardLayout.show(cardPanel,"PARSE"));
        diffButton.addActionListener(e -> cardLayout.show(cardPanel,"DIFF"));
        testButton.addActionListener(e -> cardLayout.show(cardPanel,"TEST"));


        JPanel topPanel = new JPanel();
        topPanel.setLayout(
            new javax.swing.BoxLayout(
            topPanel,
            javax.swing.BoxLayout.Y_AXIS
            )
        );



        JPanel oldFilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel newFilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton oldButton = new JButton("旧ファイル選択");
        JButton newButton = new JButton("新ファイル選択");
        oldFileField = new JTextField(56);
        oldFileField.setEditable(false);
        newFileField = new JTextField(56);
        newFileField.setEditable(false);
        oldFilePanel.add(oldButton);
        oldFilePanel.add(oldFileField);
        newFilePanel.add(newButton);
        newFilePanel.add(newFileField);

        JPanel questionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel questionLabel = new JLabel("質問入力:");
        questionField = new JTextField(64);
        questionField.setText("このCOBOLの処理概要を教えてください");
        questionPanel.add(questionLabel);
        questionPanel.add(questionField);


        JPanel memoPanel = new JPanel(new BorderLayout());
        JLabel memoLabel = new JLabel("メモ帳");

        //20260717 ADD START
        memoArea = new JTextArea(5,50);

        memoArea.setRows(8);
        memoArea.setColumns(50);
        memoArea.setLineWrap(true);
        memoArea.setWrapStyleWord(true);

        JScrollPane memoScroll = new JScrollPane(memoArea);

        //memoField.setText("メモ帳としてご利用ください");
        //memoPanel.add(memoLabel);
        //memoPanel.add(memoField);
        memoPanel.add(memoLabel, BorderLayout.NORTH);
        memoPanel.add(memoScroll, BorderLayout.CENTER);
        //20260717 ADD END

        topPanel.add(oldFilePanel);
        topPanel.add(newFilePanel);
        topPanel.add(questionPanel);
        topPanel.add(memoPanel);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton analyzeButton = new JButton("解析実行");
        
        //20260717 ADD START
        analyzeButton.setBackground(new Color(255,120,120));
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        //20260717 ADD END

        actionPanel.add(analyzeButton);
        actionPanel.add(parseButton);
        actionPanel.add(diffButton);
        actionPanel.add(testButton);

        JPanel inputPanel = new JPanel(new BorderLayout(8,8));
        inputPanel.add(topPanel,BorderLayout.NORTH);
        inputPanel.add(actionPanel,BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(8,8));
        centerPanel.add(inputPanel,BorderLayout.NORTH);
        centerPanel.add(cardPanel,BorderLayout.CENTER);

        add(centerPanel,BorderLayout.CENTER);

        oldButton.addActionListener(e -> onSelectFileO());
        newButton.addActionListener(e -> onSelectFileN());
        analyzeButton.addActionListener(e -> onAnalyze());
    }

    private void onSelectFileO() {
        JFileChooser chooser_old = new JFileChooser();
        chooser_old.setDialogTitle("COBOLファイルを選択してください");
        chooser_old.setFileFilter(new FileNameExtensionFilter("COBOL Files (*.cbl, *.cob, *.cpy)", "cbl", "cob", "cpy"));

        int result = chooser_old.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser_old.getSelectedFile();
            oldFileField.setText(file.getAbsolutePath());
        }
    }
    private void onSelectFileN() {
        JFileChooser chooser_new = new JFileChooser();
        chooser_new.setDialogTitle("COBOLファイルを選択してください");
        chooser_new.setFileFilter(new FileNameExtensionFilter("COBOL Files (*.cbl, *.cob, *.cpy)", "cbl", "cob", "cpy"));

        int result = chooser_new.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser_new.getSelectedFile();
            newFileField.setText(file.getAbsolutePath());
        }
    }
    private String compFiles(File oldFile, File newFile) throws IOException {
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

    private void onAnalyze() {
        String oldPath = oldFileField.getText();
        String newPath = newFileField.getText();
        String question = questionField.getText();

        //20260727 CHG-START
        //if (oldPath == null || oldPath.isBlank()) {
        //    JOptionPane.showMessageDialog(this, "解析する旧ファイルを選択してください。", "入力エラー", JOptionPane.WARNING_MESSAGE);
        //    return;
        //}

        //20260727 CHG-END  
        if (newPath == null || newPath.isBlank()){
            JOptionPane.showMessageDialog(this, "解析する新ファイルを選択してください。", "入力エラー", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            //20260727 CHG-START
            //File oldFile = new File(oldPath);
            File newFile = new File(newPath);

            String diffResult;


            if (!oldPath.isBlank()){
            File oldFile = new File(oldPath);
            diffResult = compFiles(oldFile,newFile);
        }
            else {
            diffResult = "旧ファイルが選択されていないため、差分比較はスキップされました。";
        }
            //String diffResult = compFiles(oldFile,newFile);
            //20260727 CHG-END

            //新ファイルのみ解析結果が表示される。旧ファイルも表示するか検討。
            String source = FileUtil.readFile(newPath);
            CobolParser.ParseResult parseResult = cobolParser.parse(source);
            
            String summary = summaryGenerator.generateSummary(parseResult, question);
            String testViewpoints = testCaseGenerator.generateViewpoints(parseResult);

            //20260716 CHG-START
            //String output = buildOutput(parseResult,summary,diffResult,testViewpoints);
            String parseOutput = buildOutput1(parseResult,summary);
            parseArea.setText(parseOutput);
            String diffOutput = buildOutput2(diffResult);
            diffArea.setText(diffOutput);
            String testOutput = buildOutput3(testViewpoints);
            testArea.setText(testOutput);
            cardLayout.show(cardPanel,"PARSE");

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "入力エラー", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "ファイル読み込みに失敗しました: " + ex.getMessage(), "I/Oエラー", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "予期しないエラーが発生しました: " + ex.getMessage(), "システムエラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    //2026-07-16 CHG-START
    //private String buildOutput(CobolParser.ParseResult result, String summary, String diffResult,String testViewpoints) {
    private String buildOutput1(CobolParser.ParseResult result, String summary) {
    //2026-07-16 CHG-END
        StringBuilder sb = new StringBuilder();
        sb.append("========== COBOL解析結果 ==========").append(System.lineSeparator());
        sb.append("1. （新）プログラム名").append(System.lineSeparator());
        sb.append("- ").append(result.programName()).append(System.lineSeparator()).append(System.lineSeparator());

        sb.append("2. CALL先一覧").append(System.lineSeparator());
        if (result.callTargets().isEmpty()) {
            sb.append("- なし").append(System.lineSeparator());
        } else {
            for (String call : result.callTargets()) {
                sb.append("- ").append(call).append(System.lineSeparator());
            }
        }
        sb.append(System.lineSeparator());

        sb.append("3. COPY句一覧").append(System.lineSeparator());
        if (result.copyBooks().isEmpty()) {
            sb.append("- なし").append(System.lineSeparator());
        } else {
            for (String copy : result.copyBooks()) {
                sb.append("- ").append(copy).append(System.lineSeparator());
            }
        }
        sb.append(System.lineSeparator());

        sb.append("4. ファイル定義一覧 (SELECT / FD)").append(System.lineSeparator());
        sb.append("- SELECT句").append(System.lineSeparator());
        if (result.selectFiles().isEmpty()) {
            sb.append("  - なし").append(System.lineSeparator());
        } else {
            for (String select : result.selectFiles()) {
                sb.append("  - ").append(select).append(System.lineSeparator());
            }
        }
        sb.append("- FD句").append(System.lineSeparator());
        if (result.fdFiles().isEmpty()) {
            sb.append("  - なし").append(System.lineSeparator());
        } else {
            for (String fd : result.fdFiles()) {
                sb.append("  - ").append(fd).append(System.lineSeparator());
            }
        }
        sb.append(System.lineSeparator());

        sb.append("5. 処理概要").append(System.lineSeparator());
        sb.append(summary).append(System.lineSeparator()).append(System.lineSeparator());

        return sb.toString();
    }
    //2026-0716 ADD-START
    private String buildOutput2(String diffResult) {
        StringBuilder sb = new StringBuilder();
    //2026-0716 ADD-END
        sb.append("1. 差分比較結果").append(System.lineSeparator());
        sb.append(diffResult)
          .append(System.lineSeparator())
          .append(System.lineSeparator());

        return sb.toString();
    }

    //2026-0716 ADD-START
    private String buildOutput3(String testViewpoints) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== 試験観点 ==========").append(System.lineSeparator());
    //2026-0716 ADD-END
        sb.append("1. 試験観点").append(System.lineSeparator());
        sb.append(testViewpoints).append(System.lineSeparator());

        return sb.toString();
    }
}