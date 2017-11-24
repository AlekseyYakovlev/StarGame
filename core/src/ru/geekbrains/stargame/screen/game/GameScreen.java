package ru.geekbrains.stargame.screen.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.List;

import ru.geekbrains.engine.Base2DScreen;
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
import ru.geekbrains.stargame.screen.game.ui.MessageGameOver;
import ru.geekbrains.stargame.screen.menu.ui.ButtonNewGame;
import ru.geekbrains.stargame.screen.menu.ui.ButtonRestartGame;

/**
 * Игровой экран
 */
public class GameScreen extends Base2DScreen implements ActionListener {

    private static final int STAR_COUNT = 56; // число звёзд
    private static final float STAR_HEIGHT = 0.01f; // высота звезды
    private static final float BUTTON_PRESS_SCALE = 0.8f;
    private static final float BUTTON_HEIGHT = 0.15f;

    private static Label.LabelStyle labelStyleWhite = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    private Label hpLabel;
    private Label scoreLabel;

    private ButtonRestartGame buttonRestartGame;

    private enum State { PLAYING, GAME_OVER }

    private State state;

    private int frags=0; //количество убитых врагов

    private MessageGameOver messageGameOver;

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

        this.music.setLooping(true);
        this.music.play();
        startNewGame();

        buttonRestartGame = new ButtonRestartGame(atlas, this, BUTTON_PRESS_SCALE);
        buttonRestartGame.setHeightProportion(BUTTON_HEIGHT);

        hpLabel = new Label("",labelStyleWhite);
        scoreLabel = new Label("",labelStyleWhite);

        hpLabel.setText("Health: "+mainShip.getHp());
        scoreLabel.setText("Score: "+frags);


    }

    @Override
    protected void resize(Rect worldBounds) {
        background.resize(worldBounds);
        for (int i = 0; i < trackingStars.length; i++) {
            trackingStars[i].resize(worldBounds);
        }
        mainShip.resize(worldBounds);
        buttonRestartGame.resize(worldBounds);
        hpLabel.setPosition(100,100);
        scoreLabel.setPosition(worldBounds.getLeft(),worldBounds.getBottom());
    }

    @Override
    public void render(float delta) {
        update(delta);
        checkCollisions();
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
        bulletPool.updateActiveSprites(delta);


        switch (state) {
            case PLAYING:
                enemyPool.updateActiveSprites(delta);
                mainShip.update(delta);
                enemiesEmitter.generateEnemies(delta);
                if (mainShip.isDestroyed()) {
                    state = State.GAME_OVER;
                }
                break;
            case GAME_OVER:
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
            if (mainShip.isBulletCollision(bullet) && state == State.PLAYING) {
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

        hpLabel.setPosition(mainShip.pos.x,mainShip.pos.y);
        hpLabel.setText("Health: "+mainShip.getHp());
        hpLabel.draw(batch,1f);
        scoreLabel.setText("Score: ");
        scoreLabel.draw(batch,1f);

        if (state == State.GAME_OVER) {
            messageGameOver.draw(batch);
            buttonRestartGame.draw(batch);
        }
        batch.end();
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
        super.dispose();
    }

    @Override
    protected void touchDown(Vector2 touch, int pointer) {
        if (state == State.GAME_OVER) buttonRestartGame.touchDown(touch, pointer);
        else mainShip.touchDown(touch, pointer);
    }

    @Override
    protected void touchUp(Vector2 touch, int pointer) {
        if (state == State.GAME_OVER) buttonRestartGame.touchUp(touch, pointer);
        else mainShip.touchUp(touch, pointer);
    }

    @Override
    public boolean keyDown(int keycode) {
        mainShip.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        mainShip.keyUp(keycode);
        return false;
    }

    private void startNewGame() {
        state = State.PLAYING;
        frags = 0;

        mainShip.setToNewGame();

        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
    }

    @Override
    public void actionPerformed(Object src) {
        if(src==buttonRestartGame) startNewGame();
        else throw new RuntimeException("Unknown src = " + src);

    }
}
