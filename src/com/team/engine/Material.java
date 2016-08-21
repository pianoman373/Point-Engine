package com.team.engine;

import com.team.engine.vecmath.Vec3;

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

    public Material(Vec3 diffuseColor, Vec3 specularColor, float shininess) {
        this(0, diffuseColor, false, 0, specularColor, false, shininess);
    }

    public Material(int diffuseTex, int specularTex, float shininess) {
        this(diffuseTex, new Vec3(), true, specularTex, new Vec3(), true, shininess);
    }
}
