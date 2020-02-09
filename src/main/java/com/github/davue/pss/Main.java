/*
 * A cross-platform tool to overcome the limitations of the speed controls of Paradox Interactive games.
 * Copyright (C) 2020 David Enderlin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.davue.pss;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    public static void main(String[] args) {
//        if (args.length >= 1 && args[0].toLowerCase().equals("-s")) {
//            new Server(Integer.parseInt(args[1])).start();
//        } else if (args.length >= 2 && args[0].toLowerCase().equals("-c")) {
//            new Client(args[1], Integer.parseInt(args[2])).start();
//        } else {
//            System.out.println("Usage:\n" +
//                    "    Server: -s <port>\n" +
//                    "    Client: -c <host> <port>");
//        }

        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Text testText1 = new Text("Test");
        Text testText2 = new Text("Soos");

        GridPane root = new GridPane();
        root.setGridLinesVisible(true);
        root.add(testText1, 0, 0);
        root.add(testText2, 0, 1);

        Scene scene = new Scene(root);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
