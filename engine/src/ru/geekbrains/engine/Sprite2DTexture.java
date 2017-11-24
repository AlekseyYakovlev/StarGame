package ru.geekbrains.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

/**
 * Обёртка для класса Texture с реализованными фильтрами
 */
public class Sprite2DTexture extends Texture {

    /**
     * Конструктор
     * @param internalPath путь к текстуре
     */
    public Sprite2DTexture(String internalPath) {
        this(Gdx.files.internal(internalPath));
    }

    /**
     * Конструктор с настройкой фильтров
     * @param file файл текстуры
     */
    public Sprite2DTexture(FileHandle file) {
        super(file, true);
        setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
    }
}
