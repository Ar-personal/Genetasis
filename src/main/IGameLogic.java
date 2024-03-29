package main;

import engine.graphics.Window;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput, Window window) throws Exception;

    void render(Window window, MouseInput mouseInput);

    void cleanup();
}