package me.aaa.PP;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;

public class SceneManager{
    private AllScenes currentScene;

    private BaseGameActivity activity;
    private Engine engine;
    private Camera camera;
    private BitmapTextureAtlas splashTA, playerTexture, playerMenuTexture, playMenuTexture, settingsMenuTexture;
    private ITextureRegion splashTR, playerTextureRegion, playerMenuTextureRegion, playMenuTextureRegion, settingsMenuTextureRegion;
    private Scene splashScene, gameScene;
    private MenuScene menuScene;
    private PhysicsWorld menuPhysicsWorld, gamePhysicsWorld;
    private Rectangle gPaddle;

    private Text score;
    private Font font;

    final private FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0f, 0f, 0f);
    final private FixtureDef PLAYER_FIX = PhysicsFactory.createFixtureDef(10, 1f, 0f);

    private final int MENU_PLAY = 0;
    private final int MENU_SETTINGS = 1;

    private static int PADDLE_WIDTH = 100;
    private static int PADDLE_HEIGHT = 20;

    public enum AllScenes{
        SPLASH,
        MENU,
        GAME
    }

    public SceneManager(BaseGameActivity activity, Engine engine, Camera camera) {
        this.activity = activity;
        this.engine = engine;
        this.camera = camera;
    }

    public void loadSplashResources(){
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        splashTA = new BitmapTextureAtlas(this.activity.getTextureManager(), 512, 512);
        splashTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
                splashTA,
                this.activity,
                "ping_pong.png",
                0, 0);
        splashTA.load();
    }

    public Scene createSplashScene(){
        splashScene = new Scene();
        splashScene.setBackground(new Background(Color.BLACK));

        Sprite icon = new Sprite(0, 0, splashTR, engine.getVertexBufferObjectManager());
        icon.setHeight(256f);
        icon.setWidth(256f);
        icon.setPosition(camera.getWidth()/2,
                camera.getHeight()/2);

        splashScene.attachChild(icon);
        return splashScene;
    }


    public void loadMenuResources(){
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        //width & height = 2^x
        playerMenuTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 512, 512);
        playerMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.
                createFromAsset(playerMenuTexture, activity, "circle-512-menu.png", 0, 0);

        playMenuTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 512, 512);
        playMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.
                createFromAsset(playMenuTexture, activity, "play-512.png", 0, 0);

        settingsMenuTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 512, 512);
        settingsMenuTextureRegion = BitmapTextureAtlasTextureRegionFactory.
                createFromAsset(settingsMenuTexture, activity, "settings-512.png", 0, 0);

        playMenuTexture.load();
        settingsMenuTexture.load();
        playerMenuTexture.load();
    }

    public MenuScene createMenuScene(){
        menuScene = new MenuScene(camera);
        menuScene.setBackground(new Background(Color.WHITE));

        menuPhysicsWorld = new PhysicsWorld(new Vector2(0,0), false);
        menuScene.registerUpdateHandler(this.menuPhysicsWorld);

        createMenuBackground();

        final IMenuItem playButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, 50, 50, playMenuTextureRegion,
                engine.getVertexBufferObjectManager()), 1.1f, 1);

        final IMenuItem settingButton = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SETTINGS, 50, 50, settingsMenuTextureRegion,
                engine.getVertexBufferObjectManager()), 1.1f, 1);

        playButton.setPosition(camera.getWidth()/2,
                camera.getHeight()/2);
        settingButton.setPosition(camera.getWidth()/2,
                camera.getHeight()/2 - 100);

        menuScene.addMenuItem(playButton);
        menuScene.addMenuItem(settingButton);

        menuScene.setOnMenuItemClickListener(new MenuScene.IOnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
                switch (pMenuItem.getID())
                {
                    case MENU_PLAY:
                        loadGameResources();
                        createGameScene();
                        setCurrentScene(AllScenes.GAME);
                        break;
                    case MENU_SETTINGS:
                        // when click options
                        break;

                }
                return false;
            }
        });

        return menuScene;
    }

    private void createMenuBackground() {
        Rectangle left = new Rectangle(10f,
                camera.getHeight()/2,
                10,
                camera.getHeight(),
                this.engine.getVertexBufferObjectManager());
        left.setColor(new Color(Color.BLACK));
        PhysicsFactory.createBoxBody(menuPhysicsWorld, left, BodyDef.BodyType.StaticBody, WALL_FIX);
        menuScene.attachChild(left);

        Rectangle right = new Rectangle(camera.getWidth() - 10f,
                camera.getHeight()/2,
                10,
                camera.getHeight(),
                engine.getVertexBufferObjectManager());
        right.setColor(new Color(Color.BLACK));
        PhysicsFactory.createBoxBody(menuPhysicsWorld, right, BodyDef.BodyType.StaticBody, WALL_FIX);
        menuScene.attachChild(right);

        Rectangle down = new Rectangle(camera.getWidth()/2,
                10f,
                camera.getWidth(),
                10,
                this.engine.getVertexBufferObjectManager());
        down.setColor(new Color(Color.BLACK));
        PhysicsFactory.createBoxBody(menuPhysicsWorld, down, BodyDef.BodyType.StaticBody, WALL_FIX);
        menuScene.attachChild(down);

        Rectangle up = new Rectangle(camera.getWidth()/2,
                camera.getHeight() - 10f,
                camera.getWidth(),
                10,
                this.engine.getVertexBufferObjectManager());
        up.setColor(new Color(Color.BLACK));
        PhysicsFactory.createBoxBody(menuPhysicsWorld, up, BodyDef.BodyType.StaticBody, WALL_FIX);
        menuScene.attachChild(up);

        Sprite sMenuPlayer = new Sprite(camera.getWidth()/2,
                camera.getHeight()/2,
                playerMenuTextureRegion,
                engine.getVertexBufferObjectManager());
        sMenuPlayer.setHeight(50f);
        sMenuPlayer.setWidth(50f);

        Body body = PhysicsFactory.createCircleBody(menuPhysicsWorld, sMenuPlayer, BodyDef.BodyType.DynamicBody, PLAYER_FIX);
        body.setLinearVelocity(MathUtils.random(0f, 360f), MathUtils.random(0f, 360f));
        menuScene.attachChild(sMenuPlayer);
        menuPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(sMenuPlayer, body, true, false));
    }


    public void loadGameResources(){
        final ITexture scoreFontTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);

        FontFactory.setAssetBasePath("font/");
        font = FontFactory.createFromAsset(this.activity.getFontManager(), scoreFontTexture, this.activity.getAssets(), "LCD.ttf", 32, true, android.graphics.Color.WHITE);
        font.load();

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        //width & height = 2^x
        playerTexture = new BitmapTextureAtlas(this.activity.getTextureManager(), 512, 512);
        playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.
                createFromAsset(playerTexture, activity, "circle-512.png", 0, 0);
        playerTexture.load();
    }

    public Scene createGameScene(){
        gameScene = new Scene();
        gameScene.setBackground(new Background(Color.BLACK));

        gamePhysicsWorld = new PhysicsWorld(new Vector2(0,0), false);
        gameScene.registerUpdateHandler(this.gamePhysicsWorld);

        gameScene.registerUpdateHandler(this.gamePhysicsWorld);

        gameScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
                gPaddle.setPosition(pSceneTouchEvent.getX(), PADDLE_HEIGHT + 20);
                return true;
            }
        });

        gPaddle = new Rectangle(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT, this.engine.getVertexBufferObjectManager());
        gPaddle.setColor(new Color(Color.WHITE));
        PhysicsFactory.createBoxBody(menuPhysicsWorld, gPaddle, BodyDef.BodyType.StaticBody, WALL_FIX);
        gPaddle.setPosition(camera.getWidth()/2, PADDLE_HEIGHT + 20);

        gameScene.attachChild(gPaddle);

//        score = new Text(0, 20, font, "0", new TextOptions(HorizontalAlign.CENTER), this.engine.getVertexBufferObjectManager());
//
//        gameScene.attachChild(score);

        createWalls();
        createPlayer();
        return gameScene;
    }

    private void createWalls() {
        FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0f, 0f, 0f);
        Rectangle left = new Rectangle(15f,
                camera.getHeight()/2,
                15,
                camera.getHeight(),
                this.engine.getVertexBufferObjectManager());
        left.setColor(new Color(Color.WHITE));
        PhysicsFactory.createBoxBody(gamePhysicsWorld, left, BodyDef.BodyType.StaticBody, WALL_FIX);
        gameScene.attachChild(left);

        Rectangle right = new Rectangle(camera.getWidth() - 15f,
                camera.getHeight()/2,
                15,
                camera.getHeight(),
                engine.getVertexBufferObjectManager());
        right.setColor(new Color(Color.WHITE));
        PhysicsFactory.createBoxBody(gamePhysicsWorld, right, BodyDef.BodyType.StaticBody, WALL_FIX);
        gameScene.attachChild(right);

        Rectangle up = new Rectangle(camera.getWidth()/2,
                camera.getHeight() - 15f,
                camera.getWidth(),
                15,
                this.engine.getVertexBufferObjectManager());
        up.setColor(new Color(Color.WHITE));
        PhysicsFactory.createBoxBody(gamePhysicsWorld, up, BodyDef.BodyType.StaticBody, WALL_FIX);
        gameScene.attachChild(up);
    }

    private void createPlayer() {
        final Sprite sPlayer = new Sprite(camera.getWidth()/2,
                camera.getHeight()/2,
                playerTextureRegion,
                engine.getVertexBufferObjectManager());
        sPlayer.setHeight(50f);
        sPlayer.setWidth(50f);

        Body body = PhysicsFactory.createCircleBody(gamePhysicsWorld, sPlayer, BodyDef.BodyType.DynamicBody, PLAYER_FIX);
        body.setLinearVelocity(MathUtils.random(0f, 360f), MathUtils.random(0f, 360f));
        gameScene.attachChild(sPlayer);
        gamePhysicsWorld.registerPhysicsConnector(new PhysicsConnector(sPlayer, body, true, false));


        gameScene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void onUpdate(final float pSecondsElapsed) {

                score.setText(sPlayer.getX() + "");

                if (sPlayer.getX() < 0 || sPlayer.getX() > camera.getWidth() || sPlayer.getY() < 0 || sPlayer.getY() > camera.getHeight()){
                    sPlayer.setPosition(camera.getWidth()/2, camera.getHeight()/2);
                }
            }

            @Override
            public void reset() {}
        });
    }


    public AllScenes getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(AllScenes currentScene) {
        this.currentScene = currentScene;

        switch (currentScene){
            case SPLASH:
                break;
            case MENU:
                engine.setScene(menuScene);
                break;
            case GAME:
                engine.setScene(gameScene);
                break;
            default:
                break;
        }
    }

    public void backKeyPressed(){
        switch (currentScene){
            case SPLASH:
                break;
            case MENU:
                break;
            case GAME:
                engine.setScene(menuScene);
                break;
            default:
                break;
        }
    }
}
