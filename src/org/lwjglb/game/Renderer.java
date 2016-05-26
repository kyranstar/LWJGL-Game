package org.lwjglb.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjglb.game.engine.Camera;
import org.lwjglb.game.engine.PointLight;
import org.lwjglb.game.engine.ShaderProgram;
import org.lwjglb.game.engine.Transformation;
import org.lwjglb.game.engine.Window;
import org.lwjglb.game.engine.utils.Utils;

public class Renderer {

	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3f, 0.3f, 0.3f);

	private static final float SPECULAR_POWER = 10;

	private Transformation transformation = new Transformation();

	ShaderProgram shader;

	public void init(Window window) {
		try {
			shader = new ShaderProgram();
			shader.createFragmentShader(Utils.loadResource("/fragment.fs"));
			shader.createVertexShader(Utils.loadResource("/vertex.vs"));
			shader.link();
			shader.createUniform("projectionMatrix");
			shader.createUniform("modelViewMatrix");
			shader.createUniform("ambientLight");
			shader.createUniform("specularPower");
			shader.createUniform("reflectance");
			shader.createPointLightUniform("pointLight");
			shader.createUniform("camera_pos");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void render(Window window, GameModel[] models, PointLight[] lights, Camera camera) {
		if (window.isResized()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

			window.setResized(false);
		}

		window.setClearColor(.6f, .6f, .6f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		shader.bind();
		Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
				Z_NEAR, Z_FAR);
		shader.setUniform("projectionMatrix", projectionMatrix);

		Matrix4f viewMatrix = transformation.getViewMatrix(camera);
		// Get a copy of the light object and transform its position to view
		// coordinates
		PointLight currPointLight = new PointLight(lights[0]);
		Vector3f lightPos = currPointLight.getPosition();
		Vector4f aux = new Vector4f(lightPos, 1).mul(viewMatrix);
		lightPos.x = aux.x;
		lightPos.y = aux.y;
		lightPos.z = aux.z;
		shader.setUniform("pointLight", currPointLight);
		
		shader.setUniform("ambientLight", AMBIENT_LIGHT);
		shader.setUniform("specularPower", SPECULAR_POWER);
		shader.setUniform("camera_pos", camera.getPosition());

		for (int i = 0; i < models.length; i++) {
			shader.setUniform("reflectance", models[i].getReflectance());
			shader.setUniform("modelViewMatrix", transformation.getModelViewMatrix(models[i], viewMatrix));
			models[i].getMesh().render();
		}
		shader.unbind();

	}

	public void cleanup() {
		if (shader != null) {
			shader.cleanup();
		}
	}

}
