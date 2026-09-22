package br.ytdash;

import javafx.scene.Node;

public class ControlsManager {

    public static void setDisabled(boolean disabled, Node... controls) {

        for (Node control : controls) {
            control.setDisable(disabled);
        }
    }
}