package org.lwjglb.game.engine;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput input);

    void update(float dt);
    
    void render(Window window);
    
    void cleanup();
}