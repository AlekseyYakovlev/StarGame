package ru.geekbrains.stargame.common.star;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.math.Rnd;
import ru.geekbrains.engine.sprite.Sprite;

/**
 * Класс, описывающий поведение звезды
 */
public class Star extends Sprite {

    // вектор скорости
    protected final Vector2 v = new Vector2();
    // границы игрового мира
    private Rect worldBounds;

    /**
     * Конструктор Star
     * @param region текстура с изображением звезды
     * @param vx скорость по оси x
     * @param vy скорость по оси y
     * @param height высота звезды
     */
    public Star(TextureRegion region, float vx, float vy, float height) {
        super(region);
        v.set(vx, vy);
        setHeightProportion(height);
    }

    /**
     * Изменение размера экрана
     * @param worldBounds новые границы игрового мира
     */
    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        float posX = Rnd.nextFloat(this.worldBounds.getLeft(), this.worldBounds.getRight());
        float posY = Rnd.nextFloat(this.worldBounds.getBottom(), this.worldBounds.getTop());
        pos.set(posX, posY);
    }

    /**
     * Обновление позиции звезды
     * @param deltaTime смещение
     */
    @Override
    public void update(float deltaTime) {
        pos.mulAdd(v, deltaTime);
        checkAndHandleBounds();
    }

    /**
     * Проверка границ экрана для того чтобы вернуть звезду в игровое поле
     */
    protected void checkAndHandleBounds() {
        if (getRight() < worldBounds.getLeft()) setLeft(worldBounds.getRight());
        if (getLeft() > worldBounds.getRight()) setRight(worldBounds.getLeft());
        if (getTop() < worldBounds.getBottom()) setBottom(worldBounds.getTop());
        if (getBottom() > worldBounds.getTop()) setTop(worldBounds.getBottom());
    }
}
