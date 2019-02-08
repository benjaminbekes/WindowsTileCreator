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
import source.Main;
import source.util.Utils;

import java.io.*;
import java.util.Properties;

public class PropertiesService {

    private static final String PROPERTIES = Main.class.getResource("save.properties").getPath();
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
        try {
            FileOutputStream outputStream = new FileOutputStream(PROPERTIES);
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.log("Error: Writing changes into a save file failed: " + e);
        }
    }

    /**
     * Gets properties file with values.
     * @return Gets properties file
     */
    private static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(new File(PROPERTIES))) {

            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.log("Error: Reading a save file failed" + e);
            return properties;
        }
        return properties;
    }


}
