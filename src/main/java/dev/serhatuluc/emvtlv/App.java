package dev.serhatuluc.emvtlv;

import dev.serhatuluc.emvtlv.tlv.TlvNode;
import dev.serhatuluc.emvtlv.tlv.TlvParseException;
import dev.serhatuluc.emvtlv.tlv.TlvParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class App extends Application {

    private static final String SAMPLE_HEX =
            "820219808407A0000000041010950500800080019A032607109C01005F2A0209499F02060000000001009F03060000000000009F"
            + "420209491F5004360040009F3704B8B371CE9F1A0207929F3303E0F0C85F3401019F090200019F1E0854323533353235389F"
            + "3501229F0607A00000000410109F26080868D1D439B460F69F360201429F10120111A04013220A00000000000000000000"
            + "FF9F2701808E0E000000000000000042031E031F03";

    private static final Pattern NON_HEX = Pattern.compile("[^0-9A-Fa-f]");

    private TextArea hexInput;
    private TableView<TlvRow> table;
    private Label statusLabel;
    private Label tagSourceLabel;
    private TagRegistry tagRegistry;

    @Override
    public void start(Stage stage) {
        CustomTagLoader.LoadResult customTags = CustomTagLoader.load();
        tagRegistry = new TagRegistry(customTags.tags());

        tagSourceLabel = new Label();
        tagSourceLabel.setWrapText(true);
        if (customTags.hasError()) {
            tagSourceLabel.setText("⚠ " + customTags.error());
        } else {
            tagSourceLabel.setText(EmvTagDictionary.NAMES.size() + " built-in EMVCo tags + "
                    + customTags.tags().size() + " custom tag(s) loaded from "
                    + customTags.filePath().getFileName()
                    + " (in the folder you launched this app from) — to add or edit tags, open and edit this file manually.");
        }

        HBox tagSourceBox = new HBox(10, tagSourceLabel);
        tagSourceBox.setAlignment(Pos.CENTER_LEFT);
        tagSourceBox.getStyleClass().add("tag-source-box");
        HBox.setHgrow(tagSourceLabel, Priority.ALWAYS);

        hexInput = new TextArea();
        hexInput.setPromptText("Paste EMV TLV hex data here (e.g. Field 55 content)...");
        hexInput.setWrapText(false);
        hexInput.setPrefRowCount(4);
        hexInput.getStyleClass().add("mono");

        Button parseButton = new Button("Parse");
        parseButton.setDefaultButton(true);
        parseButton.setOnAction(e -> parse());

        Button sampleButton = new Button("Load Sample");
        sampleButton.setOnAction(e -> hexInput.setText(SAMPLE_HEX));

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            hexInput.clear();
            table.getItems().clear();
            statusLabel.setText("");
        });

        HBox buttonRow = new HBox(10, parseButton, sampleButton, clearButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        VBox top = new VBox(8,
                tagSourceBox,
                new Label("EMV TLV Hex Data:"),
                hexInput,
                buttonRow,
                statusLabel);
        top.setPadding(new Insets(12));

        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(table);
        BorderPane.setMargin(table, new Insets(0, 12, 12, 12));

        Scene scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/dev/serhatuluc/emvtlv/style.css")).toExternalForm());

        stage.setTitle("EMV TLV Parser");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(550);
        stage.show();
    }

    private TableView<TlvRow> buildTable() {
        TableView<TlvRow> tv = new TableView<>();
        tv.getStyleClass().add("mono");

        TableColumn<TlvRow, String> tagCol = column("Tag", 110, r -> r.getTag());
        TableColumn<TlvRow, String> nameCol = column("Name", 300, r -> r.getName());
        TableColumn<TlvRow, String> classCol = column("Class", 130, r -> r.getTlvClass());
        TableColumn<TlvRow, String> typeCol = column("Type", 100, r -> r.getType());
        TableColumn<TlvRow, String> lenCol = column("Length", 70, r -> String.valueOf(r.getLength()));
        TableColumn<TlvRow, String> hexCol = column("Value (Hex)", 280, r -> r.getValueHex());
        TableColumn<TlvRow, String> asciiCol = column("ASCII", 160, r -> r.getValueAscii());

        tv.getColumns().addAll(List.of(tagCol, nameCol, classCol, typeCol, lenCol, hexCol, asciiCol));
        tv.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tv.setRowFactory(table -> {
            TableRow<TlvRow> row = new TableRow<>() {
                @Override
                protected void updateItem(TlvRow item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().removeAll("row-unknown", "row-constructed");
                    if (!empty && item != null) {
                        if (!item.isKnown()) {
                            getStyleClass().add("row-unknown");
                        } else if (item.isConstructed()) {
                            getStyleClass().add("row-constructed");
                        }
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    showRowDetail(row.getItem());
                }
            });
            return row;
        });

        return tv;
    }

    /** Shows a small popup with every field of the clicked row, so unknown/proprietary tags
     * (whose name/hex value can get truncated in the table) are still fully readable. */
    private void showRowDetail(TlvRow row) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("TLV Tag Detail");
        dialog.setHeaderText(row.getTag().trim() + "  —  " + row.getName());
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/dev/serhatuluc/emvtlv/style.css")).toExternalForm());

        TextArea details = new TextArea(formatRowDetail(row));
        details.setEditable(false);
        details.setWrapText(true);
        details.getStyleClass().add("mono");
        details.setPrefSize(480, 220);

        dialog.getDialogPane().setContent(details);
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    private String formatRowDetail(TlvRow row) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tag: ").append(row.getTag().trim()).append('\n');
        sb.append("Name: ").append(row.getName()).append('\n');
        sb.append("Class: ").append(row.getTlvClass()).append('\n');
        sb.append("Type: ").append(row.getType()).append('\n');
        sb.append("Length: ").append(row.getLength()).append('\n');
        if (!row.getValueHex().isEmpty()) {
            sb.append("Value (Hex): ").append(row.getValueHex()).append('\n');
        }
        if (!row.getValueAscii().isEmpty()) {
            sb.append("Value (ASCII): ").append(row.getValueAscii()).append('\n');
        }
        sb.append("Known tag: ").append(row.isKnown() ? "Yes" : "No (unknown / proprietary)");
        return sb.toString();
    }

    private TableColumn<TlvRow, String> column(String title, double width, java.util.function.Function<TlvRow, String> extractor) {
        TableColumn<TlvRow, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(extractor.apply(cellData.getValue())));
        return col;
    }

    private void parse() {
        table.getItems().clear();
        statusLabel.getStyleClass().removeAll("status-ok", "status-error");
        statusLabel.getStyleClass().add("status-error");

        String cleaned = NON_HEX.matcher(hexInput.getText()).replaceAll("");
        if (cleaned.isEmpty()) {
            statusLabel.setText("Hex data is empty.");
            return;
        }
        if (cleaned.length() % 2 != 0) {
            statusLabel.setText("Invalid hex length (" + cleaned.length() + " characters, odd number).");
            return;
        }

        byte[] data;
        try {
            data = HexFormat.of().parseHex(cleaned);
        } catch (IllegalArgumentException ex) {
            statusLabel.setText("Hex parse error: " + ex.getMessage());
            return;
        }

        List<TlvNode> nodes;
        try {
            nodes = TlvParser.parse(data);
        } catch (TlvParseException ex) {
            statusLabel.setText("TLV parse error: " + ex.getMessage());
            return;
        }

        int unknownCount = 0;
        for (TlvNode node : TlvParser.flatten(nodes)) {
            TlvRow row = new TlvRow(node, tagRegistry);
            table.getItems().add(row);
            if (!row.isKnown()) {
                unknownCount++;
            }
        }

        if (unknownCount > 0) {
            statusLabel.getStyleClass().removeAll("status-ok");
            statusLabel.getStyleClass().add("status-error");
            statusLabel.setText(unknownCount + " tag(s) are not in the standard EMVCo list (red rows).");
        } else {
            statusLabel.getStyleClass().removeAll("status-error");
            statusLabel.getStyleClass().add("status-ok");
            statusLabel.setText("All tags are recognized in the standard EMVCo list.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
