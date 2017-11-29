package ru.geekbrains.stargame.common;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.sprite.Sprite;
import ru.geekbrains.stargame.common.bullet.Bullet;
import ru.geekbrains.stargame.common.bullet.BulletPool;
import ru.geekbrains.stargame.common.explosion.Explosion;
import ru.geekbrains.stargame.common.explosion.ExplosionPool;

/**
 * Базовый класс для всех кораблей
 */
public class Ship extends Sprite {

    private static final float DAMAGE_ANIMATE_INTERVAL = 0.1f;
    private float damageAnimateTimer = DAMAGE_ANIMATE_INTERVAL;

    protected final Vector2 v = new Vector2(); // скорость корабля
    protected int hp; // жизни корабля
    protected int maxHp;
    protected Rect worldBounds; // граница мира

    protected ExplosionPool explosionPool; // пулл взрывов
    protected BulletPool bulletPool; // пулл пуль
    protected TextureRegion bulletRegion; // текстура пули

    protected final Vector2 bulletV = new Vector2(); // скорость пули
    protected float bulletHeight; // высота пули
    protected int bulletDamage; // урон

    protected float reloadInterval; // время перезарядки
    protected float reloadTimer; // таймер для стрельбы

    protected Sound bulletSound;

    public Ship(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
    }

    public Ship(TextureRegion region, int rows, int cols, int frames, BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds) {
        super(region, rows, cols, frames);
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
    }

    @Override
    public void update(float deltaTime) {
        damageAnimateTimer += deltaTime;
        if (damageAnimateTimer >= DAMAGE_ANIMATE_INTERVAL) {
            frame = 0;
        }
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
    }

    /**
     * Нанесение урона кораблю
     * @param damage урон
     */
    public void damage(int damage) {
        frame = 1;
        damageAnimateTimer = 0f;
        hp -= damage;
        if (hp < 0) {
            hp = 0;
        }
        if (hp == 0) {
            boom();
            destroy();
        }
    }

    /**
     * Выстрел
     */
    protected void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, bulletHeight, worldBounds, bulletDamage);
        if (bulletSound.play() == -1) {
            throw new RuntimeException("Can't play sound");
        }
    }

    /**
     * Взрыв корабля
     */
    public void boom() {
        hp = 0;
        Explosion explosion = explosionPool.obtain();
        explosion.set(getHeight(), pos);
    }

    public int getBulletDamage() {
        return bulletDamage;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }
}
