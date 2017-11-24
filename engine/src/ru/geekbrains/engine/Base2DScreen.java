package ru.geekbrains.engine;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.engine.math.MatrixUtils;
import ru.geekbrains.engine.math.Rect;

/**
 * Базовый класс для всех экранов
 */
public class Base2DScreen implements Screen, InputProcessor{

    protected Game game; // объект Game
    private Rect screenBounds; // границы области рисования в пикселях
    protected Rect worldBounds; // границы проекции мировых координат
    private Rect glBounds; // дефолтные границы проекции мир - gl

    protected Matrix4 worldToGl; // матрица преобразований мир - gl
    protected Matrix3 screenToWorld; // матрица преобразований экран - мир

    protected SpriteBatch batch; // батчер

    private final Vector2 touch = new Vector2(); // вектор для хранения координат срабатывания мыши/тача

    /**
     * Конструктор
     * @param game // объект Game
     */
    public Base2DScreen(Game game) {
        this.game = game;
        this.screenBounds = new Rect();
        this.worldBounds = new Rect();
        this.glBounds = new Rect(0,0,1f,1f);
        this.worldToGl = new Matrix4();
        this.screenToWorld = new Matrix3();
    }

    /**
     * Отображение экрана
     */
    @Override
    public void show() {
        System.out.println("show");
        Gdx.input.setInputProcessor(this);
        if (batch != null) {
            throw new RuntimeException("batch != null, повторная установка screen без dispose");
        }
        batch = new SpriteBatch();
    }

    /**
     * Отрисовка экрана 60 раз в секунду
     * @param delta дельта
     */
    @Override
    public void render(float delta) {

    }

    /**
     * Изменение размеров экрана
     * @param width новая ширина
     * @param height новая высота
     */
    @Override
    public void resize(int width, int height) {
        System.out.println("resize w = " + width + ", h = " + height);
        screenBounds.setSize(width, height);
        screenBounds.setLeft(0);
        screenBounds.setBottom(0);

        float aspect = width / (float) height;
        worldBounds.setHeight(1f);
        worldBounds.setWidth(1f * aspect);
        MatrixUtils.calcTransitionMatrix(worldToGl, worldBounds, glBounds);
        batch.setProjectionMatrix(worldToGl);
        MatrixUtils.calcTransitionMatrix(screenToWorld, screenBounds, worldBounds);
        resize(worldBounds);
    }

    /**
     * Изменение размера экрана
     * @param worldBounds новые границы игрового мира
     */
    protected void resize(Rect worldBounds) {

    }

    @Override
    public void pause() {
        System.out.println("pause");
    }

    @Override
    public void resume() {
        System.out.println("resume");
    }

    @Override
    public void hide() {
        System.out.println("hide");
        dispose();
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
        batch.dispose();
        batch = null;
    }


    @Override
    public boolean keyDown(int keycode) {
        System.out.println("keyDown keycode = " + keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        System.out.println("keyUp keycode = " + keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        System.out.println("keyTyped character = " + character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touch.set(screenX, screenBounds.getHeight() - 1f - screenY).mul(screenToWorld);
        touchDown(touch, pointer);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touch.set(screenX, screenBounds.getHeight() - 1f - screenY).mul(screenToWorld);
        touchUp(touch, pointer);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touch.set(screenX, screenBounds.getHeight() - 1f - screenY).mul(screenToWorld);
        touchDragged(touch, pointer);
        return false;
    }

    protected void touchDown(Vector2 touch, int pointer) {

    }

    protected void touchUp(Vector2 touch, int pointer) {

    }

    protected void touchDragged(Vector2 touch, int pointer) {

    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        System.out.println("scrolled amount = " + amount);
        return false;
    }
}
