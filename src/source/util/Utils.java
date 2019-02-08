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

package source.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import source.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static final SimpleDateFormat format = new SimpleDateFormat("hh:mm");
    private static WebView consoleView;

    /**
     * Must be called on {@link Controller}.<br>
     * Required for elements initialization.
     * @param console The log console.
     */
    public static void init(@NotNull WebView console) {
        consoleView = console;
    }

    public static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void centerImage(@NotNull ImageView imageView) {
        Image img = imageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);

        }
    }

    /**
     * Provides simple text showing on UI
     *
     * @param message A message to show on UI
     */
    public static void log(@Nullable String message) {
        String log = "<span><b style=\"color: white;\">[" + format.format(new Date()) + "]: </b>";
        if (message.toLowerCase().contains("error")) {
            log += "<span style=\"color: #FF5A5A;\">" + message + "</span>";
        } else if (message.toLowerCase().contains("warning")) {
            log += "<span style=\"color: #FFA53E;\">" + message + "</span>";
        } else {
            log += "<span style=\"color: white;\">" + message + "</span>";
        }
        log += "</span><br>";
        WebEngine engine = consoleView.getEngine();
        engine.executeScript("document.body.innerHTML += '" + log + "'; window.scrollTo(0,document.body.scrollHeight);");
    }
}
