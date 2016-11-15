package com.team.engine;

import static com.team.engine.Globals.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.team.engine.rendering.Material;
import com.team.engine.rendering.Mesh;
import com.team.engine.rendering.Shader;
import com.team.engine.vecmath.Mat4;

import jassimp.AiMaterial;
import jassimp.AiMesh;
import jassimp.AiPostProcessSteps;
import jassimp.AiScene;
import jassimp.AiTextureType;
import jassimp.Jassimp;

public class Model {
	private ArrayList<Mesh> meshes = new ArrayList<>();
	private ArrayList<Material> materials = new ArrayList<>();
	private Mat4 matrix;
	private boolean specularWorkflow;
	
	public Model(String file, Mat4 matrix, boolean specularWorkflow) {
		try {
			this.matrix = matrix;
			this.specularWorkflow = specularWorkflow;
			
			Set<AiPostProcessSteps> mySet = new HashSet<>(Arrays.asList(new AiPostProcessSteps[] {AiPostProcessSteps.TRIANGULATE}));
			
			AiScene scene = Jassimp.importFile(Settings.RESOURCE_PATH + file, mySet);
			List<AiMesh> sceneMeshes = scene.getMeshes();
			List<AiMaterial> sceneMaterials = scene.getMaterials();
			
			for (int i = 0; i < sceneMeshes.size(); i++) {
				AiMesh mesh = sceneMeshes.get(i);
				AiMaterial mat = sceneMaterials.get(mesh.getMaterialIndex());
				
				Matcher matcher = Pattern.compile("(.*/).*$").matcher(file);
				
				if (matcher.find()) {
					print(matcher.group(1));
				}
				else {
					print("something went wrong");
				}
				
				String diffuseTex = "../" + mat.getTextureFile(AiTextureType.DIFFUSE, 0);
				
				loadTexture(diffuseTex, false, true);
				
				String normalTex = "../" + mat.getTextureFile(AiTextureType.NORMALS, 0);
				loadTexture(normalTex);
				
				String specularTex = "../" + mat.getTextureFile(AiTextureType.SPECULAR, 0);
				loadTexture(specularTex);
				
				Mesh finalMesh = Mesh.normalIndexed(
						GLBuffers.toArray(mesh.getPositionBuffer()),
						GLBuffers.toArray(mesh.getNormalBuffer()),
						GLBuffers.toArray(mesh.getTexCoordBuffer(0)),
						GLBuffers.toArray(mesh.getIndexBuffer()),
						true
				);
				meshes.add(finalMesh);
				
				if (diffuseTex.equals("../")) {
					materials.add(new Material(vec3(0.5, 0.5, 0.5), 0.5f, 0.0f));
				}
				else {
					materials.add(new Material(diffuseTex, 0.0f, normalTex, specularTex));
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Model(String file, Mat4 matrix) {
		this(file, matrix, false);
	}
	
	public void render() {
		Shader s;
		if (specularWorkflow) s = getShader("pbr-specular");
		else s = getShader("pbr");
		s.bind();
		s.uniformMat4("model", matrix);
		
		for (int i = 0; i < meshes.size(); i++) {
			Mesh mesh = meshes.get(i);
			Material material = materials.get(i);
			
			s.uniformMaterial(material);
			mesh.draw();
		}
	}
	
	public void renderShadow(Shader s) {
		s.uniformMat4("model", matrix);
		
		for (int i = 0; i < meshes.size(); i++) {
			Mesh mesh = meshes.get(i);
			mesh.draw();
		}
	}
}
