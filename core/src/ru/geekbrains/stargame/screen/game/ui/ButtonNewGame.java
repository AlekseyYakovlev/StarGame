package ru.geekbrains.stargame.screen.game.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.engine.ui.ActionListener;
import ru.geekbrains.engine.ui.ScaledTouchUpButton;

public class ButtonNewGame extends ScaledTouchUpButton {

    private static final float HEIGHT = 0.05f;
    private static final float TOP = -0.012f;
    private static final float PRESS_SCALE = 0.9f;


    public ButtonNewGame(TextureAtlas atlas, ActionListener listener) {
        super(atlas.findRegion("button_new_game"), listener, PRESS_SCALE);
        setHeightProportion(HEIGHT);
        setTop(TOP);
    }
}
