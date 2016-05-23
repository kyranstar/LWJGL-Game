package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjglb.game.engine.Camera;
import org.lwjglb.game.engine.IGameLogic;
import org.lwjglb.game.engine.MouseInput;
import org.lwjglb.game.engine.Window;

public class DummyGame implements IGameLogic {
	private static final float CAMERA_POS_STEP = 0.05f;
	private static final float MOUSE_SENSITIVITY = 0.4f;
	Camera camera = new Camera();
	Vector3f cameraInc = new Vector3f();
	Renderer renderer = new Renderer();
	private GameModel[] models;

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);
//		float[] positions = new float[] { 
//				-0.5f, 0.5f, -1.05f,  // front left up
//				-0.5f, -0.5f, -1.05f, // front left down
//				0.5f, -0.5f, -1.05f, // front right down
//				0.5f, 0.5f,	-1.05f,  // front right up
//				-0.5f, 0.5f, -2.05f,  // back left up
//				-0.5f, -0.5f, -2.05f, // back left down
//				0.5f, -0.5f, -2.05f, // back right down
//				0.5f, 0.5f,	-2.05f,  // back right up
//		};
//		int[] indices = new int[] { 
//				0, 1, 3, 3, 1, 2, // front face
//				0, 4, 1, 1, 5, 4, // left face
//				3, 2, 6, 6, 3, 7,
//				};
//		float[] colors = new float[] { 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f, };
		
		
		HeightMap heightMap = new HeightMap(0.0f, 0.1f, 0.70f, 40, 40);
		heightMap.setScale(10);
		models = new GameModel[] { heightMap };
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
			cameraInc.z = -1;
		} else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
			cameraInc.z = 1;
		}
		if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
			cameraInc.x = -1;
		} else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
			cameraInc.x = 1;
		}
		if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
			cameraInc.y = -1;
		} else if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
			cameraInc.y = 1;
		}
		// Update camera position
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
				cameraInc.z * CAMERA_POS_STEP);

		// Update camera based on mouse
		if (mouseInput.isRightButtonPressed()) {
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void render(Window window) {
		renderer.render(window, models, camera);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		for (int i = 0; i < models.length; i++) {
			models[i].getMesh().cleanup();
		}
	}

}
