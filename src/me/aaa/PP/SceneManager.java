package me.aaa.PP;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

public class SceneManager {
    private AllScenes currentScene;

    private BaseGameActivity activity;
    private Engine engine;
    private Camera camera;
    private BitmapTextureAtlas splashTA;
    private ITextureRegion splashTR;
    private Scene splashScene, gameScene, menuScene;

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

    }

    public void loadGameResources(){

    }

    public Scene createSplashScene(){
        return null;
    }

    public Scene createGameScene(){
        return null;
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
}
