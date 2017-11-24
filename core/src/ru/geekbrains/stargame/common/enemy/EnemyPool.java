package ru.geekbrains.stargame.common.enemy;

import com.badlogic.gdx.audio.Sound;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.pool.SpritesPool;
import ru.geekbrains.stargame.common.bullet.BulletPool;
import ru.geekbrains.stargame.common.explosion.ExplosionPool;
import ru.geekbrains.stargame.screen.game.MainShip;

/**
 * Пулл врагов
 */
public class EnemyPool extends SpritesPool<Enemy> {

    private final BulletPool bulletPool;
    private final ExplosionPool explosionPool;
    private final Rect worldBounds;
    private final MainShip mainShip;

    public EnemyPool(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds, MainShip mainShip) {
        this.bulletPool = bulletPool;
        this.explosionPool = explosionPool;
        this.worldBounds = worldBounds;
        this.mainShip = mainShip;
    }

    @Override
    protected Enemy newObject() {
        return new Enemy(bulletPool, explosionPool, worldBounds, mainShip);
    }

    @Override
    protected void debugLog() {
        //System.out.println("EnemyPool change active/free: " + activeObjects.size() + "/" + freeObjects.size());
    }
}
