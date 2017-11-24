package ru.geekbrains.stargame.common.enemy;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.stargame.common.Ship;
import ru.geekbrains.stargame.common.bullet.BulletPool;
import ru.geekbrains.stargame.common.explosion.ExplosionPool;
import ru.geekbrains.stargame.screen.game.MainShip;

/**
 * Вражеский корабль
 */
public class Enemy extends Ship {

    private enum State { DESCENT, FIGHT }

    private MainShip mainShip; // ссылка на игровой корабль
    private State state; // состояние корабля

    private Vector2 descentV = new Vector2(0, -0.15f);
    private Vector2 v0 = new Vector2(); // начальная скорость

    public Enemy(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds, MainShip mainShip) {
        super(bulletPool, explosionPool, worldBounds);
        this.mainShip = mainShip;
        this.v.set(v0);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        pos.mulAdd(v, deltaTime);
        switch (state) {
            case DESCENT:
                if (getTop() <= worldBounds.getTop()) {
                    v.set(v0);
                    state = State.FIGHT;
                }
                break;
            case FIGHT:
                reloadTimer += deltaTime;
                if (reloadTimer >= reloadInterval) {
                    reloadTimer = 0f;
                    shoot();
                }
                if (getBottom() < worldBounds.getBottom()) {
                    mainShip.damage(bulletDamage);
                    boom();
                    destroy();
                }
                break;
        }
    }

    public void set(
            TextureRegion[] regions, // текстура корабля
            Vector2 v0, // начальная скорость
            TextureRegion bulletRegion, // текстура пули
            float bulletHeight, // высота пули
            float bulletVY, // скорость пули
            int bulletDamage, // урон пули
            float reloadInterval, // скорость перезарядки
            Sound soundShoot, // звук выстрела
            float height, // размер корабля
            int hp // жизни
    ) {
        this.regions = regions;
        this.v0.set(v0);
        this.bulletRegion = bulletRegion;
        this.bulletHeight = bulletHeight;
        this.bulletV.set(0f, bulletVY);
        this.bulletDamage = bulletDamage;
        this.reloadInterval = reloadInterval;
        this.bulletSound = soundShoot;
        this.hp = hp;
        setHeightProportion(height);
        reloadTimer = reloadInterval;
        v.set(descentV);
        state = State.DESCENT;
    }

    public boolean isBulletCollision(Rect bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > getTop()
                || bullet.getTop() < pos.y);
    }


}
