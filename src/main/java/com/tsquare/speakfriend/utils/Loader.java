package com.tsquare.speakfriend.utils;

import com.tsquare.speakfriend.state.State;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Loader {
    @FXML private Text notice_text;
    @FXML private ImageView update_loader;

    @FXML
    public void initialize() {
        RotateTransition rotate = new RotateTransition(Duration.millis(1600), update_loader);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        notice_text.setText(State.getLoadingMessage());
    }
}
