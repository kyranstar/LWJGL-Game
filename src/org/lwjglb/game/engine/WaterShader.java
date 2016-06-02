package org.lwjglb.game.engine;

public class WaterShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/waterVertex.vs";
	private static final String FRAGMENT_FILE = "/waterFragment.fs";
	
	public WaterShader(int maxPointLights) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		
		createUniform("projectionMatrix");
		createUniform("modelViewMatrix");
		createUniform("ambientLight");
		createUniform("specularPower");
		createUniform("reflectance");
		createPointLightListUniform("pointLights", maxPointLights);
		createDirectionalLightUniform("directionalLight");
		createUniform("camera_pos");
		createUniform("time");
	}

}
