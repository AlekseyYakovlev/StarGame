package ru.geekbrains.engine.pool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.engine.sprite.Sprite;

/**
 * Пулл спрайтов
 */
public abstract class SpritesPool<T extends Sprite> {

    // список активных объектов
    protected final List<T> activeObjects = new ArrayList<T>();
    // список свободных объектов
    protected final List<T> freeObjects = new ArrayList<T>();

    protected abstract T newObject();

    /**
     * Возвращает новый объект либо объект из списка
     * @return Т
     */
    public T obtain() {
        T object;
        if (freeObjects.isEmpty()) {
            object = newObject();
        } else {
            object = freeObjects.remove(freeObjects.size() - 1);
        }
        activeObjects.add(object);
        debugLog();
        return object;
    }

    /**
     * Обновление всех активных объектов
     * @param delta дельта
     */
    public void updateActiveSprites(float delta) {
        for (int i = 0; i < activeObjects.size(); i++) {
            Sprite sprite = activeObjects.get(i);
            if (sprite.isDestroyed()) {
                throw new RuntimeException("Попытка обновления объекта, помеченного на удаление");
            }
            sprite.update(delta);
        }
    }

    /**
     * Освобождаем активные объекты, помеченные на удаление
     */
    public void freeAllDestroyedActiveObjects() {
        for (int i = 0; i < activeObjects.size(); i++) {
            T sprite = activeObjects.get(i);
            if (sprite.isDestroyed()) {
                free(sprite);
                i--;
                sprite.flushDestroy();
            }
        }
    }

    /**
     * Освобождаем все активные объекты
     */
    public void freeAllActiveObjects() {
        freeObjects.addAll(activeObjects);
        activeObjects.clear();
    }

    /**
     * Отрисовать все активные объекты
     * @param batch батчер
     */
    public void drawActiveObjects(SpriteBatch batch) {
        for (int i = 0; i < activeObjects.size(); i++) {
            Sprite sprite = activeObjects.get(i);
            if (sprite.isDestroyed()) {
                throw new RuntimeException("Попытка обновления объекта, помеченного на удаление");
            }
            sprite.draw(batch);
        }
    }

    protected void debugLog() {

    }

    /**
     * Добавляет объект в список свободных объектов
     * @param object объект
     */
    private void free(T object) {
        if (!activeObjects.remove(object)) {
            throw new RuntimeException("Попытка удаления несуществующего object = " + object);
        }
        freeObjects.add(object);
        debugLog();
    }

    public List<T> getActiveObjects() {
        return activeObjects;
    }

    public void dispose() {
        activeObjects.clear();
        freeObjects.clear();
    }
}
