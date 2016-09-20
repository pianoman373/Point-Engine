package com.team.rendering;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.team.engine.Settings;
import com.team.engine.vecmath.Vec2;
import com.team.engine.vecmath.Vec3;

public class ObjLoader {
	
	/**
	 * Parses an obj file and returns the mesh. Currently this parser has NO support for obj materials,
	 * obj objects, and automatic texture loading. That is all up to the user currently.
	 */
	public static Mesh loadFile(String path) {
		ArrayList<Vec3> positions = new ArrayList<Vec3>();
		ArrayList<Vec2> uvs = new ArrayList<Vec2>();
		ArrayList<Vec3> normals = new ArrayList<Vec3>();
		ArrayList<Integer> faces = new ArrayList<Integer>();
		
		try {
			BufferedReader reader = Files.newBufferedReader(Paths.get(Settings.RESOURCE_PATH + "meshes/" + path), Charset.defaultCharset());
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] splitline = line.split(" ");
				
				if (splitline[0].equals("v") && splitline.length == 4) {
					float x = Float.parseFloat(splitline[1]);
					float y = Float.parseFloat(splitline[2]);
					float z = Float.parseFloat(splitline[3]);
					positions.add(new Vec3(x, y, z));
				}
				if (splitline[0].equals("vt") && splitline.length == 3) {
					float u = Float.parseFloat(splitline[1]);
					float v = Float.parseFloat(splitline[2]);
					uvs.add(new Vec2(u, v));
				}
				if (splitline[0].equals("vn") && splitline.length == 4) {
					float x = Float.parseFloat(splitline[1]);
					float y = Float.parseFloat(splitline[2]);
					float z = Float.parseFloat(splitline[3]);
					normals.add(new Vec3(x, y, z));
				}
				if (splitline[0].equals("f") && splitline.length == 4) {
					for (int i = 1; i < 4; i++) {
						String[] face = splitline[i].split("/");
						int x = Integer.parseInt(face[0]);
						int y = Integer.parseInt(face[1]);
						int z = Integer.parseInt(face[2]);
						faces.add(x);
						faces.add(y);
						faces.add(z);
					}
				}
			}
			
			int[] indices = new int[faces.size() / 3];
			
			float[] finalPositions = new float[faces.size()];
			float[] finalNormals = new float[faces.size()];
			float[] finalUvs = new float[faces.size() / 3 * 2];
			
			int j = 0;
			int k = 0;
			for (int i = 0; i < faces.size(); i += 3) {
				Vec3 position = positions.get(faces.get(i) - 1);
				finalPositions[i] = position.x;
				finalPositions[i+1] = position.y;
				finalPositions[i+2] = position.z;
				
				Vec2 uv = uvs.get(faces.get(i + 1) - 1);
				finalUvs[k] = uv.x;
				finalUvs[k+1] = uv.y;
				
				Vec3 normal = normals.get(faces.get(i + 2) - 1);
				finalNormals[i] = normal.x;
				finalNormals[i+1] = normal.y;
				finalNormals[i+2] = normal.z;
				
				indices[j] = j;
				j++;
				k += 2;
			}
			
			return Mesh.normalIndexed(finalPositions, finalNormals, finalUvs, indices, true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
