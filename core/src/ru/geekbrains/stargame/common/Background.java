package ru.geekbrains.stargame.common;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.sprite.Sprite;

/**
 * Игровой фон
 */
public class Background extends Sprite {

    /**
     * Конструктор
     * @param region регион с текстурой фона
     */
    public Background(TextureRegion region) {
        super(region);
    }

    /**
     * Событие изменения размера экрана
     * @param worldBounds новые границы игрового мира
     */
    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(worldBounds.getHeight());
        pos.set(worldBounds.pos);
    }
}
