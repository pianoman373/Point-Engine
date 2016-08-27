package com.team.engine;

import com.team.engine.vecmath.Vec3;

/**
 * This is a convenience class for the struct Material in the standard shader.
 * This object can be passed to shader.uniformMaterial to automatically provide uniforms.
 */
public class Material {

    public int diffuseTex;
    public Vec3 diffuseColor;
    public boolean diffuseTextured;

    public int specularTex;
    public Vec3 specularColor;
    public boolean specularTextured;

    public float shininess;

    private Material(int diffuseTex, Vec3 diffuseColor, boolean diffuseTextured, int specularTex, Vec3 specularColor, boolean specularTextured, float shininess) {
        this.diffuseTex = diffuseTex;
        this.diffuseColor = diffuseColor;
        this.diffuseTextured = diffuseTextured;

        this.specularTex = specularTex;
        this.specularColor = specularColor;
        this.specularTextured = specularTextured;

        this.shininess = shininess;
    }

    /**
     * Creates a non-textured material.
     */
    public Material(Vec3 diffuseColor, Vec3 specularColor, float shininess) {
        this(0, diffuseColor, false, 1, specularColor, false, shininess);
    }

    /**
     * Creates a textured material. Textures are not objects, but integers. These integers
     * represent the texture slot you bound it in. Such as for: 
     * 
     * Engine.getTexture("tex.png").bind(3);
     * 
     * The parameter to send that texture as a material input would be 3.
     */
    public Material(int diffuseTex, int specularTex, float shininess) {
        this(diffuseTex, new Vec3(), true, specularTex, new Vec3(), true, shininess);
    }
}
