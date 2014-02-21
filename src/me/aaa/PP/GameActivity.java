package me.aaa.PP;

import android.view.KeyEvent;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

public class GameActivity extends BaseGameActivity {

    protected static final int CAMERA_WIDTH = 1280;
    protected static final int CAMERA_HEIGHT = 720;

    SceneManager sceneManager;

    Camera mCamera;

    @Override
    public EngineOptions onCreateEngineOptions() {
        mCamera = new Camera(0,0,CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions options = new EngineOptions(true,
                ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                mCamera);
        return options;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {

        sceneManager = new SceneManager(this, mEngine, mCamera);

        sceneManager.loadSplashResources();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {

        pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {

        mEngine.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);

                sceneManager.loadMenuResources();
                sceneManager.createMenuScene();
                sceneManager.setCurrentScene(SceneManager.AllScenes.MENU);
            }
        }));

        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public boolean onKeyUp(final int pKeyCode, final KeyEvent pEvent) {
        switch(pKeyCode) {
            case KeyEvent.KEYCODE_BACK:
                sceneManager.backKeyPressed();
                return true;
        }
        return super.onKeyUp(pKeyCode, pEvent);
    }
}
