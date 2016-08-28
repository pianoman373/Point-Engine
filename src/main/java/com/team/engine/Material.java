package com.team.engine;

import com.team.engine.vecmath.Vec3;

/**
 * This is a convenience class for the struct Material in the standard shader.
 * This object can be passed to shader.uniformMaterial to automatically provide uniforms.
 */
public class Material {

    public String diffuseTex;
    public Vec3 diffuseColor;
    public boolean diffuseTextured;

    public String specularTex;
    public Vec3 specularColor;
    public boolean specularTextured;
    
    public String normalTex;
    public boolean normalTextured;

    public float shininess;

    private Material(String diffuseTex, Vec3 diffuseColor, String specularTex, Vec3 specularColor, String normalTex, float shininess) {
        this.diffuseTex = diffuseTex;
        this.diffuseColor = diffuseColor;
        if (diffuseTex != null) this.diffuseTextured = true;
        else this.diffuseTextured = false;
        
        this.specularTex = specularTex;
        this.specularColor = specularColor;
        if (specularTex != null) this.specularTextured = true;
        else this.specularTextured = false;
        
        this.normalTex = normalTex;
        if (normalTex != null) this.normalTextured = true;
        else this.normalTextured = false;

        this.shininess = shininess;
    }

    /**
     * Creates a non-textured material.
     */
    public Material(Vec3 diffuseColor, Vec3 specularColor, float shininess) {
        this(null, diffuseColor, null, specularColor, null, shininess);
    }

    /**
     * Creates a textured material. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String diffuseTex, String specularTex, String normalTex, float shininess) {
        this(diffuseTex, new Vec3(), specularTex, new Vec3(), normalTex, shininess);
    }
}
