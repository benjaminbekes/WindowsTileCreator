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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import static source.util.Utils.log;

public class PropertiesService {

    private static final String PROPERTIES_FOLDER = System.getenv("APPDATA") + "\\WindowsTileCreator";
    private static final String PROPERTIES = PROPERTIES_FOLDER + "\\save.properties";
    private static final String JAR_PROPERTIES = "/source/save.properties";
    private static final String TILE_COLOR = "tile_color";
    private static final String APP_TITLE = "app_title";
    private static final String IMAGE_PATH = "image_path";
    private static final String EXE_PATH = "exe_path";

    /**
     * Saves the selected tile color.
     * @param hexColor A specific hex color to save.
     */
    public static void saveTileColor(@NotNull String hexColor) {
        Properties properties = getProperties();
        properties.setProperty(TILE_COLOR, hexColor);
        save(properties);
    }

    /**
     * Saves selected option 'AppTitle' on UI
     * @param allow a specific state.
     */
    public static void saveAppTitle(boolean allow) {
        Properties properties = getProperties();
        properties.setProperty(APP_TITLE, String.valueOf(allow));
        save(properties);
    }

    /**
     * Saves an image path selected by a user.<br>
     * The Java File Chooser {@link javafx.stage.FileChooser} will open on this path if it is not null.
     * @param imagePath A specific image path.
     */
    public static void saveImagePath(@NotNull String imagePath) {
        Properties properties = getProperties();
        properties.setProperty(IMAGE_PATH, imagePath);
        save(properties);
    }

    /**
     * Saves an exe path selected by a user.<br>
     * The Java File Chooser {@link javafx.stage.FileChooser} will open on this path if it is not null.
     * @param exePath A specific image path.
     */
    public static void saveExePath(@NotNull String exePath) {
        Properties properties = getProperties();
        properties.setProperty(EXE_PATH, exePath);
        save(properties);
    }

    /**
     * Gets a value of selected option 'AppTitle' from properties.
     * @return Returns true if this option should be checked.
     */
    public static boolean isAppTitle() {
        String value = getProperties().getProperty(APP_TITLE, String.valueOf(false));
        return Boolean.parseBoolean(value);
    }

    /**
     * Gets the tile color as a hex value.
     * @return A specific hex color value.
     */
    public static String getTileColor() {
        return getProperties().getProperty(TILE_COLOR, "#333435");
    }

    /**
     * Gets an image path selected by a user previously.<br>
     * <b>May returns null!</b>
     * @return A specific string file path.
     */
    @Nullable
    public static String getImagePath() {
        return getProperties().getProperty(IMAGE_PATH);
    }

    /**
     * Gets an exe path selected by a user previously.<br>
     * <b>May returns null!</b>
     * @return A specific string file path.
     */
    @Nullable
    public static String getExePath() {
        return getProperties().getProperty(EXE_PATH);
    }


    /**
     * A universal method to save properties.
     * @param properties A modified properties to save. Should be used properties returned from the {@link #getProperties()} method.
     */
    private static void save(@NotNull Properties properties) {
        if (!propertiesExists()) {
            createAppDataFolder();
        }

        try (FileOutputStream outputStream = new FileOutputStream(new File(PROPERTIES))) {
            properties.store(outputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            log("Error: Writing changes into a save file failed: " + e);
        }
    }

    /**
     * Gets properties file with values.
     * @return Gets properties file
     */
    private static Properties getProperties() {
        Properties properties = new Properties();

        if (!propertiesExists()) {
            createAppDataFolder();
        }

        try (InputStream in = new FileInputStream(new File(PROPERTIES))) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            log("Error: Reading a save file failed" + e);
            return properties;
        }
        return properties;
    }

    private static void createAppDataFolder() {
        File folder = new File(PROPERTIES_FOLDER);

        if (!folder.exists()) {
            log("[<b>AppData</b>] folder created");
            folder.mkdir();
        }

        try (InputStream in = PropertiesService.class.getResourceAsStream(JAR_PROPERTIES)) {

            Files.copy(in, Paths.get(PROPERTIES), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log("Error: Program save file was not copied into " + PROPERTIES + "<br>" + e);
        }

    }

    private static boolean propertiesExists() {
        File folder = new File(PROPERTIES_FOLDER);
        File properties = new File(PROPERTIES);

        return folder.exists() && properties.exists();

    }
}
