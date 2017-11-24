package ru.geekbrains.stargame.common.explosion;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.engine.sprite.Sprite;

/**
 * Взрыв
 */
public class Explosion extends Sprite {

    private Sound sound;
    private float animateInterval = 0.017f; // время между кадрами анимации
    private float animateTimer; // таймер для анимации взрыва

    public Explosion(TextureRegion region, int rows, int cols, int frames, Sound sound) {
        super(region, rows, cols, frames);
        this.sound = sound;
    }

    public void set(float height, Vector2 pos) {
        this.pos.set(pos);
        setHeightProportion(height);
        if (sound.play() == -1) throw new RuntimeException("sound.play() == -1");
    }

    @Override
    public void update(float deltaTime) {
        animateTimer += deltaTime;
        if (animateTimer >= animateInterval) {
            animateTimer = 0f;
            if (++frame == regions.length) {
                destroy();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        frame = 0;
    }
}
