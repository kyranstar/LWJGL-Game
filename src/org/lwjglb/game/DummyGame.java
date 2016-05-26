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

import hu.kazocsaba.v3d.mesh.format.ply.PlyReader;

public class DummyGame implements IGameLogic {
	private static final float CAMERA_POS_STEP = 0.05f;
	private static final float MOUSE_SENSITIVITY = 0.4f;
	Camera camera = new Camera();
	Vector3f cameraInc = new Vector3f();
	Renderer renderer = new Renderer();
	private GameModel[] models;
	private PointLight[] lights;
	private float lightAngle;
	private DirectionalLight directionalLight = new DirectionalLight(new Vector3f(0, 1, 0), new Vector3f(1, 1, 1), .00f);

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
		torus.setPosition(0,3f,0);
		GameModel heightmap = new HeightMap(0, .25f, .5f, 40, 40, 100);
		heightmap.setScale(10);
		models = new GameModel[] { torus,heightmap };
		
		lights = new PointLight[]{new PointLight(new Vector3f(1,0,0), new Vector3f(0,3f,0), 5, new Attenuation(1, 3, 3))};
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
		// Update directional light direction, intensity and colour
		lightAngle += 1.1f;
		if (lightAngle > 90) {
		    directionalLight.setIntensity(0);
		    if (lightAngle >= 360) {
		        lightAngle = -90;
		    }
		} else if (lightAngle <= -80 || lightAngle >= 80) {
		    float factor = 1 - (float)(Math.abs(lightAngle) - 80)/ 10.0f;
		    directionalLight.setIntensity(factor);
		    directionalLight.getColor().y = Math.max(factor, 0.9f);
		    directionalLight.getColor().z = Math.max(factor, 0.5f);
		} else {
		    directionalLight.setIntensity(1);
		    directionalLight.getColor().x = 1;
		    directionalLight.getColor().y = 1;
		    directionalLight.getColor().z = 1;
		}
		double angRad = Math.toRadians(lightAngle);
		directionalLight.getDirection().x = (float) Math.sin(angRad);
		directionalLight.getDirection().y = (float) Math.cos(angRad);
	}

	@Override
	public void render(Window window) {
		renderer.render(window, models, lights, camera, directionalLight);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		for (int i = 0; i < models.length; i++) {
			models[i].getMesh().cleanup();
		}
	}

}
