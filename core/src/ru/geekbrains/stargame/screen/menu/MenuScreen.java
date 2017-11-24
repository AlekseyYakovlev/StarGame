package ru.geekbrains.stargame.screen.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.engine.Base2DScreen;
import ru.geekbrains.engine.Sprite2DTexture;
import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.math.Rnd;
import ru.geekbrains.engine.ui.ActionListener;
import ru.geekbrains.stargame.common.Background;
import ru.geekbrains.stargame.screen.game.GameScreen;
import ru.geekbrains.stargame.screen.menu.ui.ButtonExit;
import ru.geekbrains.stargame.screen.menu.ui.ButtonNewGame;
import ru.geekbrains.stargame.common.star.Star;

/**
 * Скрин меню
 */
public class MenuScreen extends Base2DScreen implements ActionListener {

    private static final int STAR_COUNT = 256; // число звёзд
    private static final float STAR_HEIGHT = 0.01f; // высота звезды
    private static final float BUTTON_PRESS_SCALE = 0.9f;
    private static final float BUTTON_HEIGHT = 0.15f;

    private Sprite2DTexture textureBackground; // текстура для фона
    private Background background; // фон
    private Star star[]; // массив звёзд
    private TextureAtlas textureAtlas; // утилита для работы с атласами
    private ButtonExit buttonExit; // кнопка выхода
    private ButtonNewGame buttonNewGame; // кнопка начала новой игры


    /**
     * Конструктор
     * @param game объект класса Game
     */
    public MenuScreen(Game game) {
        super(game);
        textureBackground = new Sprite2DTexture("textures/bg.png");
        background = new Background(new TextureRegion(textureBackground));
        textureAtlas = new TextureAtlas("textures/menuAtlas.tpack");
        TextureRegion regionStar = textureAtlas.findRegion("star");
        star = new Star[STAR_COUNT];
        for (int i = 0; i < star.length; i++) {
            star[i] = new Star(regionStar, Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.3f, -0.1f), STAR_HEIGHT);
        }
        buttonExit = new ButtonExit(textureAtlas, this, BUTTON_PRESS_SCALE);
        buttonExit.setHeightProportion(BUTTON_HEIGHT);
        buttonNewGame = new ButtonNewGame(textureAtlas, this, BUTTON_PRESS_SCALE);
        buttonNewGame.setHeightProportion(BUTTON_HEIGHT);
    }

    /**
     * Отображение экрана
     */
    @Override
    public void show () {
        super.show();
    }

    /**
     * Отрисовка экрана 60 раз в секунду
     * @param delta дельта
     */
    @Override
    public void render (float delta) {
        update(delta);
        draw();
    }

    /**
     * Метод обновление информации в объектах
     * @param delta дельта
     */
    public void update(float delta) {
        for (int i = 0; i < star.length; i++) {
            star[i].update(delta);
        }
    }

    /**
     * Метод отрисовки
     */
    public void draw() {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 0.7f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (int i = 0; i < star.length; i++) {
            star[i].draw(batch);
        }
        buttonExit.draw(batch);
        buttonNewGame.draw(batch);
        batch.end();
    }

    /**
     * Завершение работы
     */
    @Override
    public void dispose () {
        textureBackground.dispose();
        textureAtlas.dispose();
        super.dispose();
    }

    /**
     * Изменение размера экрана
     * @param worldBounds новые границы игрового мира
     */
    @Override
    protected void resize(Rect worldBounds) {
        background.resize(worldBounds);
        for (int i = 0; i < star.length; i++) {
            star[i].resize(worldBounds);
        }
        buttonExit.resize(worldBounds);
        buttonNewGame.resize(worldBounds);
    }

    @Override
    public void actionPerformed(Object src) {
        if (src == buttonExit) {
            Gdx.app.exit();
        } else if (src == buttonNewGame) {
            game.setScreen(new GameScreen(game));
        }else {
            throw new RuntimeException("Unknown src = " + src);
        }
    }

    @Override
    protected void touchDown(Vector2 touch, int pointer) {
        buttonExit.touchDown(touch, pointer);
        buttonNewGame.touchDown(touch, pointer);
    }

    @Override
    protected void touchUp(Vector2 touch, int pointer) {
        buttonExit.touchUp(touch, pointer);
        buttonNewGame.touchUp(touch, pointer);
    }
}
