/*
 * Copyright (C) 2019 Benjamín Bekeš
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package source;

import com.sun.istack.internal.NotNull;
import com.sun.javafx.scene.control.skin.CustomColorDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import source.exception.CreationError;
import source.service.FileService;
import source.service.PropertiesService;
import source.util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import static source.service.FileService.START_MENU_FOLDER;
import static source.util.Utils.log;


public class Controller implements Initializable {

    @FXML
    public Text title;
    @FXML
    public AnchorPane window;
    @FXML
    public Pane windowTitleBar;
    @FXML
    public TextField iconPathField;
    @FXML
    public CheckBox smallCheckbox;
    @FXML
    public CheckBox largeCheckbox;
    @FXML
    public CheckBox textCheckbox;
    @FXML
    public Button createButton;
    @FXML
    public TextField exePathField;
    @FXML
    public WebView console;
    @FXML
    public ImageView iconErrorIcon;
    @FXML
    public ImageView exeErrorIcon;
    @FXML
    public ImageView closeImage;
    @FXML
    public ImageView minImage;
    @FXML
    public Pane colorBox;
    @FXML
    public Pane previewColor;
    @FXML
    public ImageView previewIcon;
    @FXML
    public Label previewText;
    @FXML
    public Label author;
    @FXML
    public ImageView restoreColor;
    @FXML
    public ImageView iconSearch;
    @FXML
    public ImageView iconDelete;
    @FXML
    public ImageView exeSearch;
    @FXML
    public ImageView exeDelete;
    @FXML
    public AnchorPane shortcutDialog;
    @FXML
    public Button shortcutDialogPositiveBtn;
    @FXML
    public Button shortcutDialogOpenBtn;

    private final String COLOR_BOX_DEFAULT_COLOR = "#333435";

    /**
     * Required for fluent dragging the main window.
     */
    private double xPos, yPos;

    private String tileColor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utils.init(console);
        changeBackgroundColor(COLOR_BOX_DEFAULT_COLOR);
        iconPathField.setFocusTraversable(false);
        exePathField.setFocusTraversable(false);
        iconPathField.setEditable(false);
        exePathField.setEditable(false);
        textCheckbox.setSelected(PropertiesService.isAppTitle());
        tileColor = PropertiesService.getTileColor();
        setCheckboxesDisability(false);
        setPreviewVisibility(false);
        createAuthorText();
        initConsole();
        initListeners();
    }


    private void initListeners() {
        initTitleBar();
        initShortcutDialog();

        iconSearch.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String iconPath = iconPathField.getText();

                String dirPath = null;
                if (iconPath != null) {
                    dirPath = FileService.getDirPath(iconPath, false);
                }

                if (dirPath == null) {
                    dirPath = PropertiesService.getImagePath();
                }

                String path = FileService.getPathFromChooser(dirPath != null && !dirPath.isEmpty() ? dirPath : null);
                if (path != null) {
                    dirPath = FileService.getDirPath(path, false);
                    if (dirPath != null) {
                        PropertiesService.saveImagePath(dirPath);
                    }
                    iconPathField.setText(path);
                    previewIcon.setImage(new Image(new File(path).toURI().toString()));
                    Utils.centerImage(previewIcon);
                    setCheckboxesDisability(true);
                    setPreviewVisibility(true);
                    changeBackgroundColor(tileColor);
                    iconErrorIcon.setVisible(false);
                }
            }
        });

        exeSearch.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                String exePath = exePathField.getText();
                String dirPath = null;
                if (exePath != null) {
                    dirPath = FileService.getDirPath(exePath, false);
                }

                if (dirPath == null) {
                    dirPath = PropertiesService.getExePath();
                }

                String path = FileService.getPathFromChooser(dirPath != null && !dirPath.isEmpty() ? dirPath : null);
                if (path != null) {
                    dirPath = FileService.getDirPath(path, false);
                    if (dirPath != null) {
                        PropertiesService.saveExePath(dirPath);
                    }
                    exePathField.setText(path);
                    exeErrorIcon.setVisible(false);
                }
            }

        });

        textCheckbox.setOnAction(event -> {
            if (previewIcon.isVisible()) {
                previewText.setVisible(textCheckbox.isSelected());
            }
            PropertiesService.saveAppTitle(textCheckbox.isSelected());
        });

        createButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                clearConsole();
                try {
                    if (validateFields()) {
                        FileService.createVisualElementsFile(
                                iconPathField.getText(),
                                exePathField.getText(),
                                tileColor,
                                smallCheckbox.isSelected(),
                                largeCheckbox.isSelected(),
                                textCheckbox.isSelected()
                        );
                        log("DONE");
                        setShortcutDialogVisibility(true);
                    }
                } catch (IOException e) {
                    log("Error: " + e.getMessage());
                    if (e instanceof CreationError) {
                        FileService.returnChanges(exePathField.getText());
                    }
                    e.printStackTrace();
                }
            }
        });

        colorBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                CustomColorDialog dialog = new CustomColorDialog(Main.getMainStage());
                dialog.setCurrentColor(Color.web(tileColor));
                dialog.setShowOpacitySlider(false);
                dialog.setShowUseBtn(false);

                dialog.setOnSave(() -> {
                    Color color = dialog.getCustomColor();
                    changeBackgroundColor(Utils.toRGBCode(color));
                });

                dialog.getStylesheets().add(Main.class.getResource("css/style.css").toExternalForm());
                dialog.setId("color-picker");
                Stage dialogStage = dialog.getDialog();
                //disable dialog's title bar
                dialogStage.initStyle(StageStyle.UNDECORATED);
                dialog.show();
            }
        });

        restoreColor.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                changeBackgroundColor(COLOR_BOX_DEFAULT_COLOR);
            }
        });

        iconDelete.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                iconPathField.clear();
                setPreviewVisibility(false);
                setCheckboxesDisability(false);
                iconErrorIcon.setVisible(false);
            }
        });

        exeDelete.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                exePathField.clear();
                exeErrorIcon.setVisible(false);
            }
        });

        title.setCursor(Cursor.HAND);
        author.setCursor(Cursor.HAND);

        title.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                openGithubPage();
            }
        });

        author.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                openGithubPage();
            }
        });

    }

    /**
     * Initializes main window custom title bar. Includes the close, minimize and dragging action.
     */
    private void initTitleBar() {
        closeImage.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Platform.exit();
            }
        });

        minImage.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Main.getMainStage().setIconified(true);
            }
        });

        windowTitleBar.setOnMousePressed(mouseEvent -> {
            xPos = Main.getMainStage().getX() - mouseEvent.getScreenX();
            yPos = Main.getMainStage().getY() - mouseEvent.getScreenY();
        });
        windowTitleBar.setOnMouseDragged(mouseEvent -> {
            Main.getMainStage().setX(mouseEvent.getScreenX() + xPos);
            Main.getMainStage().setY(mouseEvent.getScreenY() + yPos);
        });
    }

    private void initShortcutDialog() {
        shortcutDialogPositiveBtn.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                setShortcutDialogVisibility(false);
            }
        });

        shortcutDialogOpenBtn.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                try {
                    Runtime.getRuntime().exec("explorer.exe /select," + START_MENU_FOLDER);
                } catch (IOException e) {
                    log("Error: " + START_MENU_FOLDER + " was not open.");
                    e.printStackTrace();
                }
                setShortcutDialogVisibility(false);
            }
        });

    }

    /**
     * Shows possible error messages if something is filled incorrectly
     * @return true if everything is ok
     */
    private boolean validateFields() {
        String iconPath = iconPathField.getText();
        String exePath = exePathField.getText();

        //EXE PATH
        if (exePath == null || exePath.isEmpty() || !exePath.endsWith(".exe")) {
            exeErrorIcon.setVisible(true);
            log("Error: Please choose a valid exe file to which the icon should be set");
            return false;
        } else {
            exeErrorIcon.setVisible(false);
        }

        //ICON PATH
        if (iconPath != null && !iconPath.isEmpty()) {
            boolean result = FileService.checkForIconExtension(iconPath);
            iconErrorIcon.setVisible(!result);

            if (result) {
                //CHECK BOXES
                if (!smallCheckbox.isSelected() && !largeCheckbox.isSelected()) {
                    log("Error: Please select at least one of the checkboxes");
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Changes the background color of a windows tile.
     * @param color a specific background color which will be applied on a tile.
     */
    private void changeBackgroundColor(@NotNull String color) {
        tileColor = color;
        PropertiesService.saveTileColor(color);
        colorBox.setStyle("-fx-background-color: " + color + ";");
        if (previewIcon.getImage() != null) {
            previewColor.setStyle("-fx-background-color: " + color + ";");
        }
    }

    private void setCheckboxesDisability(boolean visible) {
        smallCheckbox.setSelected(visible);
        largeCheckbox.setSelected(visible);

        smallCheckbox.setDisable(!visible);
        largeCheckbox.setDisable(!visible);
    }

    private void setPreviewVisibility(boolean visible) {
        previewColor.setVisible(visible);
        previewIcon.setVisible(visible);
        if (textCheckbox.isSelected() || !visible) {
            previewText.setVisible(visible);
        }
    }

    private void setShortcutDialogVisibility(boolean visible) {
        //Make the dialog center
        shortcutDialog.setLayoutX(((window.getPrefWidth() / 2) - (shortcutDialog.getPrefWidth() / 2)) + window.getLayoutX());
        shortcutDialog.setLayoutY(((window.getPrefHeight() / 2) - (shortcutDialog.getPrefHeight() / 2)) + window.getLayoutY());
        shortcutDialog.setVisible(visible);
    }

    private void openGithubPage() {
        try {
            java.awt.Desktop.getDesktop().browse(URI.create("https://github.com/BenSvK/WindowsTileCreator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAuthorText() {
        author.setText("Windows Tile Creator© - " + new SimpleDateFormat("yyyy").format(new Date()) + " - BenSvK - [v1.0] - OpenSource");
    }

    /**
     * Init the console with basic styles
     */
    private void initConsole() {
        console.getEngine().load(Controller.class.getResource("html/console.html").toExternalForm());
    }

    private void clearConsole() {
        console.getEngine().executeScript("document.body.innerHTML = \"\";");
    }
}
