package com.team.engine;

import com.team.engine.vecmath.Vec3;

/**
 * This is a convenience class for the struct Material in the standard shader.
 * This object can be passed to shader.uniformMaterial to automatically provide uniforms.
 */
public class Material {

    public String diffuseTex;
    public Vec3 diffuse;
    public boolean diffuseTextured;

    public String roughnessTex;
    public float roughness;
    public boolean roughnessTextured;

    public String normalTex;
    public boolean normalTextured;

    public String metallicTex;
    public float metallic;
    public boolean metallicTextured;

    private Material(String diffuseTex, Vec3 diffuse, String roughnessTex, float roughness, String normalTex, String metallicTex, float metallic) {
        this.diffuseTex = diffuseTex;
        this.diffuse = diffuse;
        if (diffuseTex != null) this.diffuseTextured = true;
        else this.diffuseTextured = false;

        this.roughnessTex = roughnessTex;
        this.roughness = roughness;
        if (roughnessTex != null) this.roughnessTextured = true;
        else this.roughnessTextured = false;

        this.normalTex = normalTex;
        if (normalTex != null) this.normalTextured = true;
        else this.normalTextured = false;

        this.metallic = metallic;
        this.metallicTex = metallicTex;
        if (metallicTex != null) this.metallicTextured = true;
        else this.metallicTextured = false;
    }

    /**
     * Creates a non-textured material.
     */
    public Material(Vec3 diffuse, float roughness, float metallic) {
        this(null, diffuse, null, roughness, null, null, metallic);
    }

    /**
     * Creates a textured material. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String diffuseTex, String roughnessTex, String normalTex, String metallicTex) {
        this(diffuseTex, new Vec3(), roughnessTex, 0f, normalTex, metallicTex, 0.0f);
    }
    
    /**
     * Creates a textured material with a constant for metallic. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String diffuseTex, String roughnessTex, String normalTex, float metallic) {
        this(diffuseTex, new Vec3(), roughnessTex, 0f, normalTex, null, metallic);
    }

    /**
     * Creates a textured material with a constant for roughness. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String diffuseTex, float roughness, String normalTex, String metallicTex) {
        this(diffuseTex, new Vec3(), null, roughness, normalTex, metallicTex, 0.0f);
    }
}
