package org.lwjglb.game;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjglb.game.engine.Camera;
import org.lwjglb.game.engine.ShaderProgram;
import org.lwjglb.game.engine.TerrainShader;
import org.lwjglb.game.engine.Transformation;
import org.lwjglb.game.engine.Window;
import org.lwjglb.game.engine.lighting.DirectionalLight;
import org.lwjglb.game.engine.lighting.PointLight;
import org.lwjglb.game.engine.water.WaterFrameBuffers;
import org.lwjglb.game.engine.water.WaterModel;
import org.lwjglb.game.engine.water.WaterShader;

public class Renderer {

	private static final Vector4f NO_CLIP = new Vector4f(0, 0, 0, 0);

	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3f, 0.3f, 0.3f);

	private static final float SPECULAR_POWER = 10;

	private static final int MAX_POINT_LIGHTS = 5;

	private static final float WATER_REFLECTANCE = .9f;

	private Transformation transformation = new Transformation();

	ShaderProgram shader;

	ShaderProgram terrainShader;

	WaterShader waterShader;

	WaterFrameBuffers fbos;

	public void init(Window window) {
		try {
			shader = new ShaderProgram("/vertex.vs", "/fragment.fs");
			shader.createUniform("projectionMatrix");
			shader.createUniform("modelViewMatrix");
			shader.createUniform("modelMatrix");
			shader.createUniform("ambientLight");
			shader.createUniform("specularPower");
			shader.createUniform("reflectance");
			shader.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
			shader.createDirectionalLightUniform("directionalLight");
			shader.createUniform("clipPlane");

			terrainShader = new TerrainShader(MAX_POINT_LIGHTS);
			waterShader = new WaterShader(MAX_POINT_LIGHTS);

			fbos = new WaterFrameBuffers(window);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void render(Window window, float time, WaterModel water, HeightMap heightmap, GameModel[] models,
			PointLight[] pointLights, Camera camera, DirectionalLight directionalLight) {
		if (window.isResized()) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

			window.setResized(false);
		}

		window.setClearColor(1f, .47f, .674f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(),
				Z_NEAR, Z_FAR);

		fbos.bindRefractionFrameBuffer();
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			// render game models
			float maxWaterHeight = water.getHeight() + WaterShader.MAX_HEIGHT_DIF * water.getScale();
			renderModels(models,heightmap, pointLights, camera, directionalLight, projectionMatrix,
					new Vector4f(0, -1, 0, maxWaterHeight));

		}
		fbos.bindReflectionFrameBuffer();
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			float dist = 2 * (camera.getPosition().y - water.getHeight());

			Camera reflectionCamera = new Camera(new Vector3f(camera.getPosition()),
					new Vector3f(camera.getRotation()));
			reflectionCamera.movePosition(0, -dist, 0);
			reflectionCamera.getRotation().x = -reflectionCamera.getRotation().x;

			// render game models
			float minWaterHeight = water.getHeight() - WaterShader.MAX_HEIGHT_DIF * water.getScale();
			renderModels(models,heightmap, pointLights, reflectionCamera, directionalLight, projectionMatrix,
					new Vector4f(0, 1, 0, -minWaterHeight));
		}
		fbos.unbindCurrentFrameBuffer();

		// render game models
		renderModels(models,heightmap, pointLights, camera, directionalLight, projectionMatrix, NO_CLIP);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// render water
		waterShader.bind();
		waterShader.setUniform("zNear", Z_NEAR);
		waterShader.setUniform("zFar", Z_FAR);
		waterShader.setUniform("refractTex", 0);
		waterShader.setUniform("reflectTex", 1);
		waterShader.setUniform("depthTex", 2);
		waterShader.setUniform("projectionMatrix", projectionMatrix);
		waterShader.setUniform("modelViewMatrix",
				transformation.getModelViewMatrix(water, transformation.getViewMatrix(camera)));
		waterShader.setUniform("reflectance", WATER_REFLECTANCE);
		waterShader.setUniform("time", time);
		renderLights(waterShader, camera, pointLights, directionalLight);
		water.getMesh().render();
		GL11.glDisable(GL11.GL_BLEND);
		waterShader.unbind();

	}

	private void renderModels(GameModel[] models, HeightMap heightmap, PointLight[] pointLights, Camera camera,
			DirectionalLight directionalLight, Matrix4f projectionMatrix, Vector4f clipPlane) {
		shader.bind();
		shader.setUniform("projectionMatrix", projectionMatrix);
		renderLights(shader, camera, pointLights, directionalLight);
		shader.setUniform("clipPlane", clipPlane);
		for (int i = 0; i < models.length; i++) {
			shader.setUniform("reflectance", models[i].getReflectance());
			shader.setUniform("modelMatrix", transformation.getModelMatrix(models[i]));
			shader.setUniform("modelViewMatrix",
					transformation.getModelViewMatrix(models[i], transformation.getViewMatrix(camera)));
			models[i].getMesh().render();
		}
		shader.unbind();

		// draw terrain
		terrainShader.bind();
		terrainShader.setUniform("projectionMatrix", projectionMatrix);
		renderLights(terrainShader, camera, pointLights, directionalLight);
		terrainShader.setUniform("clipPlane", clipPlane);
		terrainShader.setUniform("reflectance", heightmap.getReflectance());
		terrainShader.setUniform("modelMatrix", transformation.getModelMatrix(heightmap));
		terrainShader.setUniform("modelViewMatrix",
				transformation.getModelViewMatrix(heightmap, transformation.getViewMatrix(camera)));
		heightmap.getMesh().render();
		terrainShader.unbind();
	}

	private void renderLights(ShaderProgram shader, Camera camera, PointLight[] pointLightList,
			DirectionalLight directionalLight) {

		Matrix4f viewMatrix = transformation.getViewMatrix(camera);
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
		if (fbos != null) {
			fbos.cleanup();
		}
	}

}
