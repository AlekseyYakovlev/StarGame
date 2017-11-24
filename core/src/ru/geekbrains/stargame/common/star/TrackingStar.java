package ru.geekbrains.stargame.common.star;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Звёзды, движение которых зависит от движения корабля
 */
public class TrackingStar extends Star {

    // вектор скорости, который отслеживает звезда
    private final Vector2 trackingV;
    // вектор суммы скоростей
    private final Vector2 sumV = new Vector2();

    /**
     * Конструктор Star
     *
     * @param region текстура с изображением звезды
     * @param vx     скорость по оси x
     * @param vy     скорость по оси y
     * @param height высота звезды
     */
    public TrackingStar(TextureRegion region, float vx, float vy, float height, Vector2 trackingV) {
        super(region, vx, vy, height);
        this.trackingV = trackingV;
    }

    @Override
    public void update(float deltaTime) {
        sumV.setZero().mulAdd(trackingV, 0.2f).rotate(180).add(v);
        pos.mulAdd(sumV, deltaTime);
        checkAndHandleBounds();
    }
}
