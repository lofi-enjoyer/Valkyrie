package me.lofienjoyer.nublada.engine.input;

import me.lofienjoyer.nublada.engine.graphics.display.Window;
import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Input {

    private static final int KEY_COUNT = 512;
    private static final int MOUSE_BUTTONS_COUNT = 16;

    private static Input instance;

    private final boolean[] pressedKeys;
    private final boolean[] justPressedKeys;

    private final boolean[] pressedMouseButtons;
    private final boolean[] justPressedMouseButtons;

    private final Vector2d cursorPosition;
    private final Vector2d cursorMovement;

    private Input() {
        this.pressedKeys = new boolean[KEY_COUNT];
        this.justPressedKeys = new boolean[KEY_COUNT];

        this.pressedMouseButtons = new boolean[MOUSE_BUTTONS_COUNT];
        this.justPressedMouseButtons = new boolean[MOUSE_BUTTONS_COUNT];

        this.cursorPosition = new Vector2d();
        this.cursorMovement = new Vector2d();

        setup();
    }

    public void update() {
        for (int i = 0; i < KEY_COUNT; i++) {
            justPressedKeys[i] = false;
        }

        for (int i = 0; i < MOUSE_BUTTONS_COUNT; i++) {
            justPressedMouseButtons[i] = false;
        }

        cursorMovement.x = 0;
        cursorMovement.y = 0;
    }

    private void setup() {
        Window window = Window.getInstance();

        window.registerKeyCallback(this::keyCallback);
        window.registerButtonCallback(this::mouseButtonCallback);
        window.registerCursorPosCallback(this::cursorPositionCallback);
    }

    private void keyCallback(long id, int key, int scancode, int action, int mods) {
        pressedKeys[key] = action != GLFW_RELEASE;
        justPressedKeys[key] = action != GLFW_RELEASE;
    }

    private void mouseButtonCallback(long id, int button, int action, int mods) {
        pressedMouseButtons[button] = action != GLFW_RELEASE;
        justPressedMouseButtons[button] = action != GLFW_RELEASE;
    }

    private void cursorPositionCallback(long id, double posX, double posY) {
        cursorMovement.x = cursorPosition.x - posX;
        cursorMovement.y = cursorPosition.y - posY;

        cursorPosition.x = posX;
        cursorPosition.y = posY;
    }

    public boolean isKeyPressed(int key) {
        return pressedKeys[key];
    }

    public boolean isKeyJustPressed(int key) {
        return justPressedKeys[key];
    }

    public boolean isButtonPressed(int button) {
        return pressedMouseButtons[button];
    }

    public boolean isButtonJustPressed(int button) {
        return justPressedMouseButtons[button];
    }

    public double getCursorX() {
        return cursorPosition.x;
    }

    public double getCursorY() {
        return cursorPosition.y;
    }

    public double getCursorMovementX() {
        return cursorMovement.x;
    }

    public double getCursorMovementY() {
        return (int)cursorMovement.y;
    }

    public static Input getInstance() {
        if (instance == null)
            instance = new Input();

        return instance;
    }

}
