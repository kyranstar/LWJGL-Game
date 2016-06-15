package org.lwjglb.game.engine.water;

import org.lwjglb.game.engine.ShaderProgram;

public class WaterShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/waterVertex.vs";
	private static final String FRAGMENT_FILE = "/waterFragment.fs";
	// sum of the amplitudes of the wave generator function in the water vertex shader
	public static final float MAX_HEIGHT_DIF = 0.003f + 0.003f + 0.002f;
	
	public WaterShader(int maxPointLights) throws Exception {
		super(VERTEX_FILE, FRAGMENT_FILE);
		
		createUniform("projectionMatrix");
		createUniform("modelViewMatrix");
		createUniform("ambientLight");
		createUniform("specularPower");
		createUniform("reflectance");
		createPointLightListUniform("pointLights", maxPointLights);
		createDirectionalLightUniform("directionalLight");
		createUniform("time");
		createUniform("refractTex");
		createUniform("reflectTex");
		createUniform("depthTex");
		createUniform("zNear");
		createUniform("zFar");
	}

}
