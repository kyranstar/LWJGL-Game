package org.lwjglb.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjglb.game.engine.Camera;
import org.lwjglb.game.engine.ShaderProgram;
import org.lwjglb.game.engine.Transformation;
import org.lwjglb.game.engine.WaterMesh;
import org.lwjglb.game.engine.Window;
import org.lwjglb.game.engine.lighting.DirectionalLight;
import org.lwjglb.game.engine.lighting.PointLight;
import org.lwjglb.game.engine.utils.Utils;

public class Renderer {

	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3f, 0.3f, 0.3f);

	private static final float SPECULAR_POWER = 10;

	private static final int MAX_POINT_LIGHTS = 5;

	private static final float WATER_REFLECTANCE = .9f;

	private Transformation transformation = new Transformation();

	ShaderProgram shader;

	ShaderProgram waterShader;

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
			shader.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
			shader.createDirectionalLightUniform("directionalLight");
			shader.createUniform("camera_pos");

			waterShader = new ShaderProgram();
			waterShader.createFragmentShader(Utils.loadResource("/waterFragment.fs"));
			waterShader.createVertexShader(Utils.loadResource("/waterVertex.vs"));
			waterShader.link();
			waterShader.createUniform("projectionMatrix");
			waterShader.createUniform("modelViewMatrix");
			waterShader.createUniform("ambientLight");
			waterShader.createUniform("specularPower");
			waterShader.createUniform("reflectance");
			waterShader.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
			waterShader.createDirectionalLightUniform("directionalLight");
			waterShader.createUniform("camera_pos");
			waterShader.createUniform("time");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void render(Window window, float time, WaterMesh water, GameModel[] models, PointLight[] pointLights, Camera camera,
			DirectionalLight directionalLight) {
		if (window.isResized()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

			window.setResized(false);
		}

		window.setClearColor(.6f, .6f, .6f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
				Z_NEAR, Z_FAR);

		Matrix4f viewMatrix = transformation.getViewMatrix(camera);

		// render water
		waterShader.bind();
		waterShader.setUniform("projectionMatrix", projectionMatrix);
		waterShader.setUniform("modelViewMatrix", transformation.getModelViewMatrix(water, viewMatrix));
		waterShader.setUniform("camera_pos", camera.getPosition());
		waterShader.setUniform("reflectance", WATER_REFLECTANCE);
		waterShader.setUniform("time", time);
		renderLights(waterShader, viewMatrix, pointLights, directionalLight);
		water.render();
		waterShader.unbind();

		// render game models
		shader.bind();
		shader.setUniform("projectionMatrix", projectionMatrix);
		renderLights(shader, viewMatrix, pointLights, directionalLight);
		shader.setUniform("camera_pos", camera.getPosition());
		for (int i = 0; i < models.length; i++) {
			shader.setUniform("reflectance", models[i].getReflectance());
			shader.setUniform("modelViewMatrix", transformation.getModelViewMatrix(models[i], viewMatrix));
			models[i].getMesh().render();
		}
		shader.unbind();

	}

	private void renderLights(ShaderProgram shader, Matrix4f viewMatrix, PointLight[] pointLightList, DirectionalLight directionalLight) {

		shader.setUniform("ambientLight", AMBIENT_LIGHT);
		shader.setUniform("specularPower", SPECULAR_POWER);

		// Process Point Lights
		int numLights = pointLightList != null ? pointLightList.length : 0;
		for (int i = 0; i < numLights; i++) {
			// Get a copy of the point light object and transform its position
			// to view coordinates
			PointLight currPointLight = new PointLight(pointLightList[i]);
			Vector3f lightPos = currPointLight.getPosition();
			Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;
			shader.setUniform("pointLights", currPointLight, i);
		}

		// // Process Spot Ligths
		// numLights = spotLightList != null ? spotLightList.length : 0;
		// for (int i = 0; i < numLights; i++) {
		// // Get a copy of the spot light object and transform its position and
		// cone direction to view coordinates
		// SpotLight currSpotLight = new SpotLight(spotLightList[i]);
		// Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
		// dir.mul(viewMatrix);
		// currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
		// Vector3f lightPos = currSpotLight.getPointLight().getPosition();
		//
		// Vector4f aux = new Vector4f(lightPos, 1);
		// aux.mul(viewMatrix);
		// lightPos.x = aux.x;
		// lightPos.y = aux.y;
		// lightPos.z = aux.z;
		//
		// shaderProgram.setUniform("spotLights", currSpotLight, i);
		// }

		// Get a copy of the directional light object and transform its position
		// to view coordinates
		DirectionalLight currDirLight = new DirectionalLight(directionalLight);
		Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
		dir.mul(viewMatrix);
		currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
		shader.setUniform("directionalLight", currDirLight);

	}

	public void cleanup() {
		if (shader != null) {
			shader.cleanup();
		}
		if (waterShader != null) {
			waterShader.cleanup();
		}
	}

}
