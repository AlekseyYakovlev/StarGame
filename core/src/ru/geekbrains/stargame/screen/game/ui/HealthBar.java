package ru.geekbrains.stargame.screen.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.geekbrains.engine.math.Rect;
import ru.geekbrains.engine.sprite.Sprite;
import ru.geekbrains.stargame.screen.game.MainShip;

/**
 * Сообщение Game Over
 */

public class HealthBar extends Sprite {

    private static final float HEIGHT = 0.025f;
    private static final float BORDER = 0.001f;
    protected Rect worldBounds;
    private TextureRegion dot;
    private MainShip mainShip;

    public HealthBar(TextureAtlas atlas, Rect worldBounds, MainShip mainShip) {
        super(atlas.findRegion("star"));
        dot=new TextureRegion(regions[0], 5,5,1,1); // получаем белую точку из звезды
        this.worldBounds=worldBounds;
        this.mainShip= mainShip;


    }


    public void draw(SpriteBatch batch,int hp) {
        batch.setColor(Color.RED);
        batch.draw(dot,worldBounds.getLeft(),worldBounds.getBottom(), worldBounds.getWidth()*mainShip.getHp()/mainShip.getMaxHp(),HEIGHT);
        batch.setColor(Color.WHITE);
        batch.draw(dot,worldBounds.getLeft(),worldBounds.getBottom(), worldBounds.getWidth(),BORDER);
        batch.draw(dot,worldBounds.getLeft(),worldBounds.getBottom()+HEIGHT, worldBounds.getWidth(),BORDER);
        batch.draw(dot,worldBounds.getLeft(),worldBounds.getBottom(), BORDER,HEIGHT);
        batch.draw(dot,worldBounds.getRight()-BORDER,worldBounds.getBottom(), BORDER,HEIGHT);
    }
}
