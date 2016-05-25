package org.lwjglb.game;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjglb.game.engine.Camera;
import org.lwjglb.game.engine.ShaderProgram;
import org.lwjglb.game.engine.Transformation;
import org.lwjglb.game.engine.Window;
import org.lwjglb.game.engine.utils.Utils;

public class Renderer {

	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void render(Window window, GameModel[] models, Camera camera) {
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
		for (int i = 0; i < models.length; i++) {
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
