package org.lwjglb.game.engine.lighting;

import org.joml.Vector3f;

public class DirectionalLight {
	private Vector3f direction;
	private Vector3f color;
	private float intensity;

	public DirectionalLight(Vector3f direction, Vector3f color, float intensity) {
		this.direction = direction;
		this.color = color;
		this.intensity = intensity;
	}

	public DirectionalLight(DirectionalLight light) {
		this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public Vector3f getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

}
