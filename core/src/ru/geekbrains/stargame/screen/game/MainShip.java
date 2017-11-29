package ru.geekbrains.stargame.screen.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.stargame.common.Ship;
import ru.geekbrains.stargame.common.bullet.BulletPool;
import ru.geekbrains.stargame.common.explosion.ExplosionPool;

/**
 * Корабль
 */
public class MainShip extends Ship {

    private static final float SHIP_HEIGHT = 0.15f; // размер корабля
    private static final float BOTTOM_MARGIN = 0.05f; // отступ от низа экрана
    private static final int INVALID_POINTER = -1;

    private final Vector2 v0 = new Vector2(0.5f, 0.0f); // нулевая скорость с направлением вправо

    private boolean pressedLeft;
    private boolean pressedRight;
    private boolean readyToShoot;

    private int leftPointer = INVALID_POINTER;
    private int rightPointer = INVALID_POINTER;

    /**
     * Конструктор
     */
    public MainShip(TextureAtlas atlas, BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds, Sound bulletSound) {
        super(atlas.findRegion("main_ship"), 1, 2, 2, bulletPool, explosionPool, worldBounds);
        setHeightProportion(SHIP_HEIGHT);
        this.bulletRegion = atlas.findRegion("bulletMainShip");
        this.bulletSound = bulletSound;
        setToNewGame();

    }

    public void setToNewGame() {
        pos.x = worldBounds.pos.x;
        this.bulletHeight = 0.01f;
        this.bulletV.set(0, 0.5f);
        this.bulletDamage = 1;
        this.reloadInterval = 0.2f;
        maxHp=hp = 100;
        flushDestroy();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setBottom(worldBounds.getBottom() + BOTTOM_MARGIN);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        pos.mulAdd(v, deltaTime);
        reloadTimer += deltaTime;
        if (readyToShoot || reloadTimer >= reloadInterval) {
            reloadTimer = 0f;
            shoot();
        }
        if (getRight() > worldBounds.getRight()) {
            setRight(worldBounds.getRight());
            stop();
        }
        if (getLeft() < worldBounds.getLeft()) {
            setLeft(worldBounds.getLeft());
            stop();
        }
    }

    public void keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = true;
                moveLeft();
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = true;
                moveRight();
                break;
            case Input.Keys.UP:
                shoot();
                break;
            case Input.Keys.SPACE:
                shoot();
                break;
        }
    }

    public void keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = false;
                if (pressedRight) {
                    moveRight();
                } else {
                    stop();
                }
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = false;
                if (pressedLeft) {
                    moveLeft();
                } else {
                    stop();
                }
                break;
            case Input.Keys.UP:
                break;
        }
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if(touch.x < pos.x) {
            if(leftPointer != INVALID_POINTER) return false;
            leftPointer = pointer;
            moveLeft();
        } else if (touch.x > pos.x) {
            if(rightPointer != INVALID_POINTER) return false;
            rightPointer = pointer;
            moveRight();
        }
        readyToShoot=true;
        return false;
    }

    @Override
    public boolean touchDragged(Vector2 touch, int pointer) {
        readyToShoot=true;
        if(touch.x < pos.x) {
            if(leftPointer != INVALID_POINTER) return false;
            leftPointer = pointer;
            moveLeft();
        } else if (touch.x > pos.x) {
            if(rightPointer != INVALID_POINTER) return false;
            rightPointer = pointer;
            moveRight();
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        readyToShoot=false;
        if(pointer == leftPointer) {
            leftPointer = INVALID_POINTER;
            if(rightPointer != INVALID_POINTER) {
                moveRight();
                readyToShoot=true;
            }
            else stop();
        } else if(pointer == rightPointer) {
            rightPointer = INVALID_POINTER;
            if(leftPointer != INVALID_POINTER) {
                moveLeft();
                readyToShoot=true;
            }
            else stop();
        }
        return false;
    }

    /**
     * Дижение вправо: установка скорости
     */
    private void moveRight() {
        v.set(v0);
    }

    /**
     * Дижение влево: установка скорости и разворот вектора
     */
    private void moveLeft() {
        v.set(v0).rotate(180);
    }

    /**
     * Останавливаем корабль
     */
    void stop() {
        v.setZero();
    }

    public Vector2 getV() {
        return v;
    }

    public boolean isBulletCollision(Rect bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > pos.y
                || bullet.getTop() < getBottom());
    }
}
