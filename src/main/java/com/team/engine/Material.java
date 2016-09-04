package com.team.engine;

import com.team.engine.vecmath.Vec3;

/**
 * This is a convenience class for the struct Material in the standard shader.
 * This object can be passed to shader.uniformMaterial to automatically provide uniforms.
 */
public class Material {

    public String albedoTex;
    public Vec3 albedo;
    public boolean albedoTextured;

    public String roughnessTex;
    public float roughness;
    public boolean roughnessTextured;

    public String normalTex;
    public boolean normalTextured;

    public String metallicTex;
    public float metallic;
    public boolean metallicTextured;

    private Material(String albedoTex, Vec3 albedo, String roughnessTex, float roughness, String normalTex, String metallicTex, float metallic) {
        this.albedoTex = albedoTex;
        this.albedo = albedo;
        if (albedoTex != null) this.albedoTextured = true;
        else this.albedoTextured = false;

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
    public Material(Vec3 albedo, float roughness, float metallic) {
        this(null, albedo, null, roughness, null, null, metallic);
    }

    /**
     * Creates a material with textured albedo, but constants for everything else.
     */
    public Material(String albedo, float roughness, float metallic) {
        this(albedo, new Vec3(), null, roughness, null, null, metallic);
    }

    /**
     * Creates a material with textured albedo and normal, but constants for everything else.
     */
    public Material(String albedoTex, float roughness, String normalTex, float metallic) {
        this(albedoTex, new Vec3(), null, roughness, normalTex, null, metallic);
    }

    /**
     * Creates a textured material. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String albedoTex, String roughnessTex, String normalTex, String metallicTex) {
        this(albedoTex, new Vec3(), roughnessTex, 0f, normalTex, metallicTex, 0.0f);
    }

    /**
     * Creates a textured material with a constant for roughness. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String albedoTex, float roughness, String normalTex, String metallicTex) {
        this(albedoTex, new Vec3(), null, roughness, normalTex, metallicTex, 0.0f);
    }

    /**
     * Creates a textured material with a constant for metallic. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String albedoTex, String roughnessTex, String normalTex, float metallic) {
        this(albedoTex, new Vec3(), roughnessTex, 0f, normalTex, null, metallic);
    }

    /**
     * Creates a textured material with a constant for roughness. Strings you supply will be loaded as textures, and automatically
     * bound when sent as a uniform.
     */
    public Material(String diffuseTex, float roughness, String normalTex, String metallicTex) {
        this(diffuseTex, new Vec3(), null, roughness, normalTex, metallicTex, 0.0f);
    }
}
