package GUI;

import Cipher.CaesarCipher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Controller {
    
    public TextField keyField;
    public TextField inputFileField;
    public TextField outputDirectoryField;
    public TextArea logs;
    public RadioButton rb_encrypt;
    public RadioButton rb_decrypt;
    public RadioButton rb_brute;
    public Label keyLabel;
    public CheckBox autoCheckBox;
    public Button fileChooserButton;
    public Button directoryChooserButton;

    private static File fileInput;
    private static File directoryOutput;
    private static int offset;

    @FXML
    protected void onFileChooser() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File (.txt)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT (*.txt)", "*.txt*"));
        fileInput = fileChooser.showOpenDialog(new Stage());
        if (fileInput != null) inputFileField.setText(fileInput.getAbsolutePath());
    }

    @FXML
    protected void onDirectoryChooser() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory");
        directoryOutput = directoryChooser.showDialog(new Stage());
        if (directoryOutput != null) outputDirectoryField.setText(directoryOutput.getAbsolutePath());
    }

    @FXML
    protected void onRadioButton() {
        ToggleGroup group = new ToggleGroup();
        rb_encrypt.setToggleGroup(group);
        rb_decrypt.setToggleGroup(group);
        rb_brute.setToggleGroup(group);

        rb_brute.setOnAction(actionEvent -> {keyField.setDisable(true); autoCheckBox.setDisable(false); outputDirectoryField.setDisable(!autoCheckBox.isSelected()); directoryChooserButton.setDisable(!autoCheckBox.isSelected());});
        rb_encrypt.setOnAction(actionEvent -> {keyField.setDisable(false); autoCheckBox.setDisable(true); outputDirectoryField.setDisable(false); directoryChooserButton.setDisable(false);});
        rb_decrypt.setOnAction(actionEvent -> {keyField.setDisable(false); autoCheckBox.setDisable(true); outputDirectoryField.setDisable(false); directoryChooserButton.setDisable(false);});
    }

    @FXML
    protected void onCheckBox() {

    }

    private void logger(String message) {
        logs.appendText(message + "\n");
    }

    private String loggerList(List<Character> list) {
        if (list.size() < 10) {logger("Error: Data is not enough."); throw new RuntimeException();}
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < 10; i++) {
            stringBuilder.append(list.get(i));
        }
        return stringBuilder.toString();
    }

    private void validateData(File fileInput) {
        if (fileInput == null) {
            logger("<IOException>: Input file not selected.");
            throw new RuntimeException();
        }
        if (!fileInput.isFile()) {
            logger("<IOException>: Input file is not supported.");
            throw new RuntimeException();
        }
        if (!(getFileExtension(fileInput).equals("txt"))) {
            logger("<IOException>: Input file with incorrect extension. Input file is not supported.");
            throw new RuntimeException();
        }
        if (fileInput.getName().equals("encrypted.txt") || fileInput.getName().equals("decrypted.txt") || fileInput.getName().equals("hacked1.txt")) {
            logger("<Warning>: The input file is named as: \"encrypted.txt,\", \"decrypted.txt\" or \"hacked1.txt.\" This can lead to problems. ");
        }
    }

    private void validateData(File fileInput, File directoryOutput) {
        validateData(fileInput);

        if (directoryOutput == null) {
            logger("<IOException>: Output directory not selected.");
            throw new RuntimeException();
        }
        if (!directoryOutput.isDirectory()) {
            logger("<IOException>: Wrong directory selected for Output.");
            throw new RuntimeException();
        }
    }

    private void validateData(File fileInput, File directoryOutput, String keyString) {
        validateData(fileInput, directoryOutput);
        if (!keyString.matches("^\\d+$")) {
            logger("<IOException>: KEY: Invalid numeric value entered.");
            throw new RuntimeException();
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    @FXML
    protected void onStart() {
        if (rb_encrypt.isSelected()) {
            String keyString = keyField.getText();
            validateData(fileInput, directoryOutput, keyString);
            offset = Integer.parseInt(keyString);
            CaesarCipher.encrypt(fileInput, new File(directoryOutput +  Character.toString(File.separatorChar) + "encrypted.txt"), offset);
            logger("Successfully encrypted. The specified directory contains the following files: encrypted.txt");
        } else {
            if (rb_decrypt.isSelected()) {
                String keyString = keyField.getText();
                validateData(fileInput, directoryOutput, keyString);
                offset = Integer.parseInt(keyString);
                CaesarCipher.decrypt(fileInput, new File(directoryOutput +  Character.toString(File.separatorChar) + "decrypted.txt"), offset);
                logger("Successfully decrypted. The specified directory contains the following files: decrypted.txt");
            } else {
                if (rb_brute.isSelected()) {
                    if (!autoCheckBox.isSelected()) {
                        validateData(fileInput, directoryOutput);
                        logger("Processing...");
                        CaesarCipher.breakCipher(fileInput, new File(directoryOutput +  Character.toString(File.separatorChar) + "hacked.txt"));
                        logger("Successfully. The specified directory contains the following files: hacked.txt");
                    }
                    else {
                        if (autoCheckBox.isSelected()) {
                            validateData(fileInput, directoryOutput);
                            logger("Processing...");
                            CaesarCipher.breakCipherAnalysis(fileInput, directoryOutput);
                            logger("Successfully. The specified directory contains the following files: hackedAnalysis.txt");
                        } else {
                            logger("<Error: Unexpected error. (Breaking cipher).>");
                            throw new RuntimeException();
                        }
                    }
                } else {
                    logger("<Error: Unexpected error.>");
                    throw new RuntimeException();
                }
            }
        }
    }
}