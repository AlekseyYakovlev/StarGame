package ru.geekbrains.stargame.screen.menu.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.ui.ActionListener;
import ru.geekbrains.engine.ui.ScaledTouchUpButton;

/**
 * Кнопка "Новая игра"
 */
public class ButtonRestartGame extends ScaledTouchUpButton {

    private static final float HEIGHT = 0.05f;
    /**
     * Конструктор
     *
     * @param atlas     атлас
     * @param listener   слушатель событий
     * @param pressScale на сколько уменьшить кнопку при нажатии
     */
    public ButtonRestartGame(TextureAtlas atlas, ActionListener listener, float pressScale) {
        super(atlas.findRegion("button_new_game"), listener, pressScale);

    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(HEIGHT);
        pos.y= -0.10f;
        pos.x=0;
    }
}
