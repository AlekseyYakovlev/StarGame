package ru.geekbrains.stargame.screen.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.geekbrains.engine.Base2DScreen;
import ru.geekbrains.engine.Font;
import ru.geekbrains.engine.Sprite2DTexture;
import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.math.Rnd;
import ru.geekbrains.engine.ui.ActionListener;
import ru.geekbrains.stargame.common.bullet.Bullet;
import ru.geekbrains.stargame.common.enemy.EnemiesEmitter;
import ru.geekbrains.stargame.common.enemy.Enemy;
import ru.geekbrains.stargame.common.enemy.EnemyPool;
import ru.geekbrains.stargame.common.explosion.Explosion;
import ru.geekbrains.stargame.common.bullet.BulletPool;
import ru.geekbrains.stargame.common.explosion.ExplosionPool;
import ru.geekbrains.stargame.common.Background;
import ru.geekbrains.stargame.common.star.TrackingStar;
import ru.geekbrains.stargame.screen.game.ui.ButtonNewGame;
import ru.geekbrains.stargame.screen.game.ui.MessageGameOver;

/**
 * Игровой экран
 */
public class GameScreen extends Base2DScreen implements ActionListener {

    private static final int STAR_COUNT = 56; // число звёзд
    private static final float STAR_HEIGHT = 0.01f; // высота звезды
    private static final float FONT_SIZE = 0.02f;

    private static final String FRAGS = "Frags:";
    private static final String HP = "HP:";
    private static final String STAGE = "Stage:";

    private enum State { PLAYING, GAME_OVER }

    private State state;

    private int frags; //количество убитых врагов

    private MessageGameOver messageGameOver;
    private ButtonNewGame buttonNewGame;

    private final BulletPool bulletPool = new BulletPool();
    private ExplosionPool explosionPool;
    private EnemyPool enemyPool;

    private Sprite2DTexture textureBackground;
    private Background background;
    private TextureAtlas atlas;
    private MainShip mainShip;
    private TrackingStar[] trackingStars;
    private EnemiesEmitter enemiesEmitter;

    private Sound soundLaser;
    private Sound soundBullet;
    private Sound soundExplosion;
    private Music music;

    private Font font;
    private StringBuilder sbFrags = new StringBuilder();
    private StringBuilder sbHP = new StringBuilder();
    private StringBuilder sbStage = new StringBuilder();

    /**
     * Конструктор
     *
     * @param game // объект Game
     */
    public GameScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        this.soundLaser = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
        this.soundBullet = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        this.soundExplosion = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        this.music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));

        this.atlas = new TextureAtlas("textures/mainAtlas.tpack");

        this.textureBackground = new Sprite2DTexture("textures/bg.png");
        this.background = new Background(new TextureRegion(this.textureBackground));

        this.explosionPool = new ExplosionPool(atlas, soundExplosion);
        this.mainShip = new MainShip(atlas, bulletPool, explosionPool, worldBounds, soundLaser);

        this.enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds, mainShip);
        this.enemiesEmitter = new EnemiesEmitter(enemyPool, worldBounds, atlas, soundBullet);

        TextureRegion regionStar = atlas.findRegion("star");
        trackingStars = new TrackingStar[STAR_COUNT];
        for (int i = 0; i < trackingStars.length; i++) {
            trackingStars[i] = new TrackingStar(regionStar, Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.3f, -0.1f), STAR_HEIGHT, mainShip.getV());
        }

        this.messageGameOver = new MessageGameOver(atlas);
        this.buttonNewGame = new ButtonNewGame(atlas, this);

        this.font = new Font("font/font.fnt", "font/font.png");
        this.font.setWorldSize(FONT_SIZE);

        this.music.setLooping(true);
        this.music.play();
        startNewGame();
    }

    @Override
    protected void resize(Rect worldBounds) {
        background.resize(worldBounds);
        for (int i = 0; i < trackingStars.length; i++) {
            trackingStars[i].resize(worldBounds);
        }
        mainShip.resize(worldBounds);
    }

    @Override
    public void render(float delta) {
        update(delta);
        if (state == State.PLAYING) {
            checkCollisions();
        }
        deleteAllDestroyed();
        draw();
    }

    /**
     * Метод обновление информации в объектах
     * @param delta дельта
     */
    public void update(float delta) {
        for (int i = 0; i < trackingStars.length; i++) {
            trackingStars[i].update(delta);
        }
        explosionPool.updateActiveSprites(delta);
        switch (state) {
            case PLAYING:
                bulletPool.updateActiveSprites(delta);
                enemyPool.updateActiveSprites(delta);
                mainShip.update(delta);
                enemiesEmitter.generateEnemies(delta, frags);
                if (mainShip.isDestroyed()) {
                    state = State.GAME_OVER;
                }
                break;
            case GAME_OVER:
                mainShip.stop();
                break;
        }

    }

    /**
     * Проверка коллизий (попала пуля в корабль, и т.д.)
     */
    public void checkCollisions() {
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            float minDist = enemy.getHalfWidth() + mainShip.getHalfWidth();
            if (enemy.pos.dst2(mainShip.pos) < minDist * minDist) {
                enemy.boom();
                enemy.destroy();
                mainShip.boom();
                mainShip.destroy();
                state = State.GAME_OVER;
                return;
            }
        }

        List<Bullet> bulletList = bulletPool.getActiveObjects();

        for (Bullet bullet: bulletList) {
            if (bullet.isDestroyed() || bullet.getOwner() == mainShip) {
                continue;
            }
            if (mainShip.isBulletCollision(bullet)) {
                mainShip.damage(bullet.getDamage());
                bullet.destroy();
                if (mainShip.isDestroyed()) {
                    state = State.GAME_OVER;
                }
            }
        }

        for (Enemy enemy : enemyList) {
            if (enemy.isDestroyed()) {
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (enemy.isBulletCollision(bullet)) {
                    enemy.damage(bullet.getDamage());
                    bullet.destroy();
                    if (enemy.isDestroyed()) {
                        frags++;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Удаление объектов, помеченных на удаление (уничтоженные корабли, и т.д.)
     */
    public void deleteAllDestroyed() {
        bulletPool.freeAllDestroyedActiveObjects();
        explosionPool.freeAllDestroyedActiveObjects();
        enemyPool.freeAllDestroyedActiveObjects();
    }

    /**
     * Метод отрисовки
     */
    public void draw() {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 0.7f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (int i = 0; i < trackingStars.length; i++) {
            trackingStars[i].draw(batch);
        }
        mainShip.draw(batch);
        enemyPool.drawActiveObjects(batch);
        bulletPool.drawActiveObjects(batch);
        explosionPool.drawActiveObjects(batch);
        if (state == State.GAME_OVER) {
            messageGameOver.draw(batch);
            buttonNewGame.draw(batch);
        }
        printInfo();
        batch.end();
    }

    public void printInfo() {
        sbFrags.setLength(0);
        sbHP.setLength(0);
        sbStage.setLength(0);
        font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft(), worldBounds.getTop());
        font.draw(batch, sbHP.append(HP).append(mainShip.getHp()), worldBounds.pos.x, worldBounds.getTop(), Align.center);
        font.draw(batch, sbStage.append(STAGE).append(enemiesEmitter.getStage()), worldBounds.getRight(), worldBounds.getTop(), Align.right);
    }

    @Override
    public void dispose() {
        soundLaser.dispose();
        soundBullet.dispose();
        soundExplosion.dispose();
        music.dispose();

        textureBackground.dispose();
        atlas.dispose();
        bulletPool.dispose();
        explosionPool.dispose();
        enemyPool.dispose();

        font.dispose();
        super.dispose();
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == buttonNewGame) {
            startNewGame();
        } else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    @Override
    protected void touchDown(Vector2 touch, int pointer) {
        switch (state) {
            case PLAYING:
                mainShip.touchDown(touch, pointer);
                break;
            case GAME_OVER:
                buttonNewGame.touchDown(touch, pointer);
                break;
        }
    }

    @Override
    protected void touchUp(Vector2 touch, int pointer) {
        switch (state) {
            case PLAYING:
                mainShip.touchUp(touch, pointer);
                break;
            case GAME_OVER:
                buttonNewGame.touchUp(touch, pointer);
                break;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (state == State.PLAYING) {
            mainShip.keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (state == State.PLAYING) {
            mainShip.keyUp(keycode);
        }
        return false;
    }

    private void startNewGame() {
        state = State.PLAYING;
        frags = 0;

        mainShip.setToNewGame();
        enemiesEmitter.setToNewGame();

        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
    }
}
