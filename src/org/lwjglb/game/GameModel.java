package org.lwjglb.game;

import org.joml.Vector3f;
import org.lwjglb.game.engine.Mesh;

public class GameModel {

    private final Mesh mesh;

    private final Vector3f position;

    private float scale;

    private final Vector3f rotation;
    
    private final float reflectance;

    public GameModel(Mesh mesh, float reflectance) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
        this.reflectance = reflectance;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Mesh getMesh() {
        return mesh;
    }

	public float getReflectance() {
		return reflectance;
	}
}
