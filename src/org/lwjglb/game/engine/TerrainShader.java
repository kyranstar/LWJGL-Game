package org.lwjglb.game.engine;

public class TerrainShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/terrainVertex.vs";
	private static final String FRAGMENT_FILE = "/terrainFragment.fs";

	public TerrainShader(int maxPointLights) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		
		createUniform("projectionMatrix");
		createUniform("modelViewMatrix");
		createUniform("modelMatrix");
		createUniform("ambientLight");
		createUniform("specularPower");
		createUniform("reflectance");
		createPointLightListUniform("pointLights", maxPointLights);
		createDirectionalLightUniform("directionalLight");
		createUniform("clipPlane");
	}
	
}
