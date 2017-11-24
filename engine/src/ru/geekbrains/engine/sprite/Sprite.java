package ru.geekbrains.engine.sprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.Arrays;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.utils.Regions;

/**
 * Класс спрайта (графического объекта)
 */
public class Sprite extends Rect {

    // угол поворота
    protected float angle;
    // масштаб
    protected float scale = 1f;
    // массив текстур
    protected TextureRegion[] regions;
    // номер текущей текстуры из массива
    protected int frame;
    // помечен ли объект на удаление
    private boolean isDestroyed;

    public Sprite() {

    }

    /**
     * Конструктор
     * @param region текстура
     */
    public Sprite(TextureRegion region) {
        if (region == null) {
            throw new NullPointerException("Create Sprite with null region");
        }
        regions = new TextureRegion[1];
        regions[0] = region;
    }

    public Sprite(TextureRegion region, int rows, int cols, int frames) {
        this.regions = Regions.split(region, rows, cols, frames);
    }

    /**
     * Метод отрисовки
     * @param batch батчер
     */
    public void draw(SpriteBatch batch) {
        if (isDestroyed()) {
            return;
        }
        batch.draw(
                regions[frame], // текущий регион
                getLeft(), getBottom(), // точка отрисовки
                halfWidth, halfHeight, // точка вращения
                getWidth(), getHeight(), // ширина и высота
                scale, scale, // масштаб по оси x и оси y
                angle // угол вращения
        );
    }

    /**
     * Считает соотношение сторон textureRegion и устанавливает with и посчитанный height
     * @param width - ширина
     */
    public void setWidthProportion(float width) {
        setWidth(width);
        float aspect = regions[frame].getRegionWidth() / (float) regions[frame].getRegionHeight();
        setHeight(width / aspect);
    }

    /**
     * Считает соотношение сторон textureRegion и устанавливает with и посчитанный height
     * @param height - высота
     */
    public void setHeightProportion(float height) {
        setHeight(height);
        float aspect = regions[frame].getRegionWidth() / (float) regions[frame].getRegionHeight();
        setWidth(height * aspect);
    }

    /**
     * Изменение размера экрана
     * @param worldBounds новые границы игрового мира
     */
    public void resize(Rect worldBounds) {

    }

    public boolean touchDown(Vector2 touch, int pointer) {
        return false;
    }

    public boolean touchUp(Vector2 touch, int pointer) {
        return false;
    }

    public boolean touchDragged(Vector2 touch, int pointer) {
        return false;
    }

    public void update(float deltaTime) {

    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Пометить объект на удаление
     */
    public void destroy() {
        this.isDestroyed = true;
    }

    /**
     * Снять метку удаления
     */
    public void flushDestroy() {
        this.isDestroyed = false;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
