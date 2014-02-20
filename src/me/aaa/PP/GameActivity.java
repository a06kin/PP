package me.aaa.PP;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.adt.color.Color;

import java.io.IOException;

public class GameActivity extends BaseGameActivity {

    protected static final int CAMERA_WIDTH = 1280;
    protected static final int CAMERA_HEIGHT = 720;
    Scene mainS;

    BitmapTextureAtlas playerTexture;
    ITextureRegion playerTextureRegion;

    PhysicsWorld physicsWorld;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Camera mCamera = new Camera(0,0,CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions options = new EngineOptions(true,
                ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                mCamera);
        return options;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        loadGfx();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    private void loadGfx() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        //width & height = 2^x
        playerTexture = new BitmapTextureAtlas(getTextureManager(), 512, 512);
        playerTextureRegion = BitmapTextureAtlasTextureRegionFactory.
                createFromAsset(playerTexture, this, "circle-512.png", 0, 0);
        playerTexture.load();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        this.mainS = new Scene();
        this.mainS.setBackground(new Background(Color.BLACK));

        physicsWorld = new PhysicsWorld(new Vector2(0,0), false);
        this.mainS.registerUpdateHandler((IUpdateHandler) this.physicsWorld);
        createWalls();

        pOnCreateSceneCallback.onCreateSceneFinished(this.mainS);
    }

    private void createWalls() {
        FixtureDef WALL_FIX = PhysicsFactory.createFixtureDef(0f, 0f, 0f);
        Rectangle left = new Rectangle(15f,
                CAMERA_HEIGHT/2,
                15,
                CAMERA_HEIGHT,
                this.mEngine.getVertexBufferObjectManager());
        left.setColor(new Color(Color.WHITE));
        PhysicsFactory.createBoxBody(physicsWorld, left, BodyDef.BodyType.StaticBody, WALL_FIX);
        this.mainS.attachChild(left);

        Rectangle right = new Rectangle(CAMERA_WIDTH - 15f,
                CAMERA_HEIGHT/2,
                15,
                CAMERA_HEIGHT,
                this.mEngine.getVertexBufferObjectManager());
        right.setColor(new Color(Color.WHITE));
        PhysicsFactory.createBoxBody(physicsWorld, right, BodyDef.BodyType.StaticBody, WALL_FIX);
        this.mainS.attachChild(right);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
        Sprite sPlayer = new Sprite(CAMERA_WIDTH/2,
                CAMERA_HEIGHT/2,
                playerTextureRegion,
                this.mEngine.getVertexBufferObjectManager());
        sPlayer.setHeight(50f);
        sPlayer.setWidth(50f);

        final FixtureDef PLAYER_FIX = PhysicsFactory.createFixtureDef(10f, 1f, 0f);
        Body body = PhysicsFactory.createCircleBody(physicsWorld, sPlayer, BodyDef.BodyType.DynamicBody, PLAYER_FIX);
        body.setLinearVelocity(90f,0f);
        this.mainS.attachChild(sPlayer);
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(sPlayer, body, true, false));

        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }
}
