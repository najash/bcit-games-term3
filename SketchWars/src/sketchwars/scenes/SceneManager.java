/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sketchwars.scenes;

import java.util.HashMap;
import sketchwars.exceptions.SceneMangerException;

/**
 * Manage scenes like game scene and menu scenes
 * @author Najash Najimudeen <najash.najm@gmail.com>
 * @param <T> key type
 */
public class SceneManager <T> {            
    private final HashMap<T, AbstractScene> scenes;
    private AbstractScene currentScene;
    
    public SceneManager() {
        scenes = new HashMap<>();
        currentScene = null;
    }

    public void init() {
        
    }
    
    public void addScene(T key, AbstractScene scene) throws SceneMangerException {
        if (scenes.containsKey(key)) {
            throw new SceneMangerException("A scene with the same key already exists.");            
        } else {
            scenes.put(key, scene);
        }
    }
    
    public void removeScene(T key) throws SceneMangerException {
        if (scenes.containsKey(key)) {
            scenes.remove(key);        
        } else {
            throw new SceneMangerException("Given scene key does not exist.");             
        }
    }
    
    public void setCurrentScene(T key) throws SceneMangerException  {
        if (scenes.containsKey(key)) {
            currentScene = scenes.get(key);
        } else {
            throw new SceneMangerException("Given scene key does not exist.");
        }
    }
    
    public AbstractScene getCurrentScene() {
        return currentScene;
    }
    
    public void render() {
        if (currentScene != null) {
            currentScene.render();
        }
    }

    public AbstractScene getScene(T key) throws SceneMangerException {
        if (scenes.containsKey(key)) {
            return scenes.get(key);        
        } else {
            throw new SceneMangerException("Given scene key does not exist.");             
        }
    }
    
    public void update(double delta) {
        if (currentScene != null) {
            currentScene.update(delta);
        }
    }
}