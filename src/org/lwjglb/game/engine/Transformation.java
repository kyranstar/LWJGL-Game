package org.lwjglb.game.engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjglb.game.GameModel;

public class Transformation {

	private final Matrix4f projectionMatrix;
	private final Matrix4f viewMatrix;
	private final Matrix4f worldMatrix;

	public Transformation() {
		worldMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
	}

	public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
		float aspectRatio = width / height;
		projectionMatrix.identity();
		projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
		return projectionMatrix;
	}

	public final Matrix4f getViewMatrix(Camera camera) {
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(camera.getRotation().x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(camera.getRotation().y), new Vector3f(0, 1, 0));
		viewMatrix.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
		return viewMatrix;
	}

	public Matrix4f getModelViewMatrix(GameModel model, Matrix4f viewMatrix) {
		worldMatrix.identity().translate(model.getPosition()).rotateX((float) Math.toRadians(model.getRotation().x))
				.rotateY((float) Math.toRadians(model.getRotation().y))
				.rotateZ((float) Math.toRadians(model.getRotation().z)).scale(model.getScale());
		Matrix4f viewCurr = new Matrix4f(viewMatrix);
		return viewCurr.mul(worldMatrix);
	}
}