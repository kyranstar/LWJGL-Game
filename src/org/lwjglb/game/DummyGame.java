package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjglb.game.engine.Camera;
import org.lwjglb.game.engine.IGameLogic;
import org.lwjglb.game.engine.MouseInput;
import org.lwjglb.game.engine.Window;
import org.lwjglb.game.engine.lighting.DirectionalLight;
import org.lwjglb.game.engine.lighting.PointLight;
import org.lwjglb.game.engine.lighting.PointLight.Attenuation;
import org.lwjglb.game.engine.water.WaterModel;

import hu.kazocsaba.v3d.mesh.format.ply.PlyReader;

public class DummyGame implements IGameLogic {
	private static final float CAMERA_POS_STEP = 0.05f;
	private static final float MOUSE_SENSITIVITY = 0.4f;
	Camera camera = new Camera(new Vector3f(0,3,0), new Vector3f());
	Vector3f cameraInc = new Vector3f();
	Renderer renderer = new Renderer();
	private GameModel[] models;
	private PointLight[] lights;
	private DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0, 1, 0), new Vector3f(1, 1, 1), 0f);
	private WaterModel waterMesh;
	private float time;

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);
		// float[] positions = new float[] {
		// -0.5f, 0.5f, -1.05f, // front left up
		// -0.5f, -0.5f, -1.05f, // front left down
		// 0.5f, -0.5f, -1.05f, // front right down
		// 0.5f, 0.5f, -1.05f, // front right up
		// -0.5f, 0.5f, -2.05f, // back left up
		// -0.5f, -0.5f, -2.05f, // back left down
		// 0.5f, -0.5f, -2.05f, // back right down
		// 0.5f, 0.5f, -2.05f, // back right up
		// };
		// int[] indices = new int[] {
		// 0, 1, 3, 3, 1, 2, // front face
		// 0, 4, 1, 1, 5, 4, // left face
		// 3, 2, 6, 6, 3, 7,
		// };
		// float[] colors = new float[] { 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f,
		// 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f, };

		GameModel torus = new GameModel(new PlyReader("/untitled.ply").readMesh(), 1);
		torus.setScale(.1f);
		torus.setPosition(0, 3f, 0);
		GameModel heightmap = new HeightMap(0, .25f, .5f, 40, 40, 100);
		heightmap.setScale(10);
		models = new GameModel[] { torus, heightmap };

		waterMesh = new WaterModel(40, 40, .1f);
		waterMesh.setScale(10);

		lights = new PointLight[] {
				new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 3f, 0), 5, new Attenuation(1, 1, 1)) };
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
		time += dt;
		//lights[0].setPosition(camera.getPosition());
	}

	@Override
	public void render(Window window) {
		renderer.render(window, time, waterMesh, models, lights, camera, directionalLight);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		for (int i = 0; i < models.length; i++) {
			models[i].getMesh().cleanup();
		}
	}

}
