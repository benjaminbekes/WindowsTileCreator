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

package source.service;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.stage.FileChooser;
import source.Main;
import source.exception.CreationError;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static source.util.Utils.log;

public class FileService {

    public static final String START_MENU_FOLDER = "C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\";

    private static final FileChooser fileChooser = new FileChooser();
    private static final String LARGE_FILE_END = "150.png";
    private static final String SMALL_FILE_END = "70.png";
    private static final String FOLDER_NAME = "Icons";
    private static final String VISUAL_ELEMENTS_EXTENSION = ".visualelementsmanifest.xml";


    private static final List<String> supportedIconExtensions = new ArrayList<>();

    private FileService() {
    }

    /**
     * The main method of the entire process. Everything starts from there.
     * @throws IOException possible file operation errors
     */
    public static void createVisualElementsFile(@NotNull String imagePath, @NotNull String exePath, @NotNull String tileColor, boolean small, boolean large, boolean appTitle) throws IOException {

        String exeDirPath = getDirPath(exePath, true);
        String iconName = getIconName(imagePath);
        String exeName = getExeName(exePath);
        String fileContent = createVisualElementsFileContent(tileColor, imagePath, exePath, small, large, appTitle);

        deleteIconsFolder(exeDirPath);

        if (exeDirPath != null && exeName != null && fileContent != null) {

            if (imagePath != null && iconName != null) {
                createAndSaveImages(imagePath, exeDirPath, iconName, small, large);
            }

            File file = new File(exeDirPath + exeName + VISUAL_ELEMENTS_EXTENSION);
            if (file.exists() && file.delete()) {
                log("Old VisualElements file was deleted");
            }

            if (file.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(fileContent);
                writer.close();
                log("VisualElements file was created");

            } else {
                throw new CreationError("Error: VisualElements file was not created");
            }
        } else {
            throw new CreationError("Error: Something is undefined");
        }
    }

    /**
     * Creates an XML content in Visualelements.xml file
     * @return created content with path of icons
     */
    private static String createVisualElementsFileContent(@NotNull String tileColor, @Nullable String imagePath, @Nullable String exePath, boolean small, boolean large, boolean appTitle) throws IOException {
        String iconName = getIconName(imagePath);

        String smallPath = null;
        String largePath = null;
        //ICONS CREATION
        if (iconName != null) {
            smallPath = small ? FOLDER_NAME + "\\" + iconName + SMALL_FILE_END : null;
            largePath = large ? FOLDER_NAME + "\\" + iconName + LARGE_FILE_END : null;

            //Return changes if a name was not found and it should have been found
        } else if (!imagePath.isEmpty()) {
            returnChanges(exePath);
            throw new IOException("Icon name was not recognized!");
        }

        //VISUAL ELEMENTS CREATION
        String largeText = appTitle ? "on" : "off";
        String result = "<Application xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <VisualElements\n" +
                "        BackgroundColor=\"" + tileColor + "\"\n" +
                "        ShowNameOnSquare150x150Logo=\"" + largeText + "\"\n" +
                "        ForegroundText=\"light\"\n";

        if (iconName != null) {
            if (smallPath != null) {
                result += "\t\tSquare70x70Logo=\"" + smallPath + "\"\n";
            }

            if (largePath != null) {
                result += "\t\tSquare150x150Logo=\"" + largePath + "\"\n";
            }
        }
        result += "\t\t/>\n</Application>";
        return result;
    }

    /**
     * Creates images for Windows Start Tiles by checked checkboxes
     * @param imagePath the exact path where the images should be stored <b>without the last file separator</b>
     * @param iconName  the exact name of an icon
     */
    private static void createAndSaveImages(@NotNull String imagePath, @NotNull String exePath, @NotNull String iconName, boolean small, boolean large) throws IOException {
        try {

            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            File dir = new File(exePath + FOLDER_NAME);

            //remove existing icons
            if (dir.exists()) {
                String exeDirPath = dir.getPath();
                deleteIconsFolder(exeDirPath);
            }

            if (small || large) {
                if (!dir.mkdir()) {
                    throw new IOException("Icons directory has not been created");
                }
            }

            if (small) {
                File file = new File(dir.getPath() + "\\" + iconName + SMALL_FILE_END);
                if (file.createNewFile()) {
                    BufferedImage resizedImage = resizeImage(originalImage, type, 70, 70);
                    ImageIO.write(resizedImage, "png", file);
                    log("Icon with size 70x70 was created");
                } else {
                    log("Error: Icon with size 70x70 was not created");
                }
            }

            if (large) {
                File file = new File(dir.getPath() + "\\" + iconName + LARGE_FILE_END);
                if (file.createNewFile()) {
                    BufferedImage resizedImage = resizeImage(originalImage, type, 150, 150);
                    ImageIO.write(resizedImage, "png", file);
                    log("Icon with size 150x150 was created");
                } else {
                    log("Error: Icon with size 150x150 was not created");
                }
            }

        } catch (IOException e) {
            throw new CreationError("Error: Some of required images were not created");
        }
    }

    /**
     * Gets the icon name from the entered path <b>without format type</b>
     * @return the exact name without type format
     */
    private static String getIconName(@Nullable String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        String[] slashSplit = imagePath.split("\\\\");
        if (slashSplit.length > 0) {
            String name = slashSplit[slashSplit.length - 1];

            if (name != null && name.contains(".")) {
                String[] nameSplit = name.split("\\.");

                if (nameSplit.length > 0) {
                    return nameSplit[0];
                }
            }
        }
        return null;
    }

    /**
     * @return Returns the extact name of the exe file without extension.
     */
    private static String getExeName(@Nullable String exePath) {
        if (exePath == null || exePath.isEmpty()) {
            log("Error: Exe name was not recognized");
            return null;
        }
        String[] slashSplit = exePath.split("\\\\");

        if (slashSplit.length > 0) {
            String name = slashSplit[slashSplit.length - 1];

            if (name != null && name.contains(".exe")) {
                return name.replace(".exe", "");
            }
        }
        log("Error: Invalid Icon Path");
        return null;
    }

    /**
     * Gets the absolute path of a file with extension. <b>Includes the last file separator character</b>
     * @param path the exact path from which should be extracted a dir of a file
     * @param displayErrors enables logging into the console
     * @return an absolute path of a dir
     */
    public static String getDirPath(@Nullable String path, boolean displayErrors) {
        if (path == null || path.isEmpty()) {
            if (displayErrors) {
                log("Error: Exe directory was not found");
            }
            return null;
        }
        String[] slashSplit = path.split("\\\\");
        if (slashSplit.length > 0) {
            return path.split(slashSplit[slashSplit.length - 1])[0];
        }

        if (displayErrors) {
            log("Error: Invalid Icon Path");
        }
        return null;
    }

    /**
     * Gets an absolute path from the chooser dialog.
     *
     * @param openAtPath On which path should be the dialog opened
     * @return Returns an absolute path of a file
     */
    public static String getPathFromChooser(@Nullable String openAtPath) {
        fileChooser.setInitialDirectory(openAtPath != null ? new File(openAtPath) : null);
        File file = fileChooser.showOpenDialog(Main.getMainStage());
        if (file != null) {
            return file.getPath();
        }
        return null;
    }

    /**
     * This method removes all created changes.
     */
    public static void returnChanges(@Nullable String exePath) {
        log("---=== RETURNING CHANGES ===---");
        String exeDirPath = getDirPath(exePath, true);
        String exeName = getExeName(exePath);

        if (exeDirPath != null) {
            deleteIconsFolder(exeDirPath);
        }

        File visualFile = new File(exeDirPath + exeName + VISUAL_ELEMENTS_EXTENSION);
        if (visualFile.exists() && visualFile.delete()) {
            log("Created VisualElements file was removed.");
        }
        log("---=== DONE ===---");
    }

    private static void deleteIconsFolder(@NotNull String exeDirPath) {
        File icons = new File(exeDirPath + FOLDER_NAME);
        String[] entries = icons.list();
        if (entries != null) {
            for (String s : entries) {
                File currentFile = new File(exeDirPath + FOLDER_NAME, s);
                if (currentFile.delete()) {
                    log("...Removed an old icon: " + s);
                } else if (currentFile.exists()) {
                    log("...Error: Removing an old icon failed: " + s);
                }
            }
            //remove the icon folder as well
            icons.delete();
        }

    }

    @Deprecated
    private void clearWindowsStartMenuCache() {
        try {
            Runtime.getRuntime().exec("ie4uinit.exe -show");
            log("Cleared Windows StartMenu icon cache.");
        } catch (IOException e) {
            log("Warning: Windows Start Menu Cache was not cleared. If you want to see changes, restart the computer.");
            e.printStackTrace();
        }
    }

    /**
     * Provides resizing images
     * @param originalImage a read image
     * @param type          the exact type of an image
     * @param IMG_WIDTH     to what width should be an image resized
     * @param IMG_HEIGHT    to what height should be an image resized
     * @return a resized buffered image
     */
    private static BufferedImage resizeImage(@NotNull BufferedImage originalImage, int type, final int IMG_WIDTH, final int IMG_HEIGHT) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

    /**
     * Checks if an icon of path is one of supported extensions
     *
     * @param iconPath an user icon selected path
     * @return true if the extension is supported
     */
    public static boolean checkForIconExtension(@NotNull String iconPath) {
        if(supportedIconExtensions.size() == 0) {
            Collections.addAll(supportedIconExtensions, "jpg", "png");
        }

        for (String extension : supportedIconExtensions) {
            if (iconPath.endsWith("." + extension)) {
                return true;
            }
        }
        StringBuilder supportedFormats = new StringBuilder();
        for (String format : supportedIconExtensions) {
            supportedFormats.append(".").append(format).append("<br>");
        }
        log("Error: Unsupported image format detected");
        log("Please select one of these supported formats: <br>" + supportedFormats.toString());
        return false;
    }

}
