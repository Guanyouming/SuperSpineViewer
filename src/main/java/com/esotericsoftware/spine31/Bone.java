package com.esotericsoftware.spine31;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

import static com.badlogic.gdx.math.Matrix3.*;

public class Bone implements Updatable {
    final BoneData data;
    final Skeleton skeleton;
    final Bone parent;
    float x, y, rotation, scaleX, scaleY;
    float appliedRotation, appliedScaleX, appliedScaleY;
    float a, b, worldX;
    float c, d, worldY;
    float worldSignX, worldSignY;

    Bone(BoneData data) {
        this.data = data;
        parent = null;
        skeleton = null;
    }

    public Bone(BoneData data, Skeleton skeleton, Bone parent) {
        if (data == null) throw new IllegalArgumentException("data cannot be null.");
        if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null.");
        this.data = data;
        this.skeleton = skeleton;
        this.parent = parent;
        setToSetupPose();
    }

    public Bone(Bone bone, Skeleton skeleton, Bone parent) {
        if (bone == null) throw new IllegalArgumentException("bone cannot be null.");
        this.skeleton = skeleton;
        this.parent = parent;
        data = bone.data;
        x = bone.x;
        y = bone.y;
        rotation = bone.rotation;
        scaleX = bone.scaleX;
        scaleY = bone.scaleY;
    }

    public void update() {
        updateWorldTransform(x, y, rotation, scaleX, scaleY);
    }

    public void updateWorldTransform() {
        updateWorldTransform(x, y, rotation, scaleX, scaleY);
    }

    public void updateWorldTransform(float x, float y, float rotation, float scaleX, float scaleY) {
        appliedRotation = rotation;
        appliedScaleX = scaleX;
        appliedScaleY = scaleY;
        float cos = MathUtils.cosDeg(rotation), sin = MathUtils.sinDeg(rotation);
        float la = cos * scaleX, lb = -sin * scaleY, lc = sin * scaleX, ld = cos * scaleY;
        Bone parent = this.parent;
        if (parent == null) {
            Skeleton skeleton = this.skeleton;
            if (skeleton.flipX) {
                x = -x;
                la = -la;
                lb = -lb;
            }
            if (skeleton.flipY) {
                y = -y;
                lc = -lc;
                ld = -ld;
            }
            a = la;
            b = lb;
            c = lc;
            d = ld;
            worldX = x;
            worldY = y;
            worldSignX = Math.signum(scaleX);
            worldSignY = Math.signum(scaleY);
            return;
        }
        float pa = parent.a, pb = parent.b, pc = parent.c, pd = parent.d;
        worldX = pa * x + pb * y + parent.worldX;
        worldY = pc * x + pd * y + parent.worldY;
        worldSignX = parent.worldSignX * Math.signum(scaleX);
        worldSignY = parent.worldSignY * Math.signum(scaleY);
        if (data.inheritRotation && data.inheritScale) {
            a = pa * la + pb * lc;
            b = pa * lb + pb * ld;
            c = pc * la + pd * lc;
            d = pc * lb + pd * ld;
        } else {
            if (data.inheritRotation) {
                pa = 1;
                pb = 0;
                pc = 0;
                pd = 1;
                do {
                    cos = MathUtils.cosDeg(parent.appliedRotation);
                    sin = MathUtils.sinDeg(parent.appliedRotation);
                    float temp = pa * cos + pb * sin;
                    pb = pa * -sin + pb * cos;
                    pa = temp;
                    temp = pc * cos + pd * sin;
                    pd = pc * -sin + pd * cos;
                    pc = temp;
                    if (!parent.data.inheritRotation) break;
                    parent = parent.parent;
                } while (parent != null);
                a = pa * la + pb * lc;
                b = pa * lb + pb * ld;
                c = pc * la + pd * lc;
                d = pc * lb + pd * ld;
            } else if (data.inheritScale) {
                pa = 1;
                pb = 0;
                pc = 0;
                pd = 1;
                do {
                    float r = parent.appliedRotation;
                    cos = MathUtils.cosDeg(r);
                    sin = MathUtils.sinDeg(r);
                    float psx = parent.appliedScaleX, psy = parent.appliedScaleY;
                    float za = cos * psx, zb = -sin * psy, zc = sin * psx, zd = cos * psy;
                    float temp = pa * za + pb * zc;
                    pb = pa * zb + pb * zd;
                    pa = temp;
                    temp = pc * za + pd * zc;
                    pd = pc * zb + pd * zd;
                    pc = temp;
                    if (psx < 0) r = -r;
                    cos = MathUtils.cosDeg(-r);
                    sin = MathUtils.sinDeg(-r);
                    temp = pa * cos + pb * sin;
                    pb = pa * -sin + pb * cos;
                    pa = temp;
                    temp = pc * cos + pd * sin;
                    pd = pc * -sin + pd * cos;
                    pc = temp;
                    if (!parent.data.inheritScale) break;
                    parent = parent.parent;
                } while (parent != null);
                a = pa * la + pb * lc;
                b = pa * lb + pb * ld;
                c = pc * la + pd * lc;
                d = pc * lb + pd * ld;
            } else {
                a = la;
                b = lb;
                c = lc;
                d = ld;
            }
            if (skeleton.flipX) {
                a = -a;
                b = -b;
            }
            if (skeleton.flipY) {
                c = -c;
                d = -d;
            }
        }
    }

    public void setToSetupPose() {
        BoneData data = this.data;
        x = data.x;
        y = data.y;
        rotation = data.rotation;
        scaleX = data.scaleX;
        scaleY = data.scaleY;
    }

    public BoneData getData() {
        return data;
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public Bone getParent() {
        return parent;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void setScale(float scale) {
        scaleX = scale;
        scaleY = scale;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public float getC() {
        return c;
    }

    public float getD() {
        return d;
    }

    public float getWorldX() {
        return worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public float getWorldSignX() {
        return worldSignX;
    }

    public float getWorldSignY() {
        return worldSignY;
    }

    public float getWorldRotationX() {
        return MathUtils.atan2(c, a) * MathUtils.radDeg;
    }

    public float getWorldRotationY() {
        return MathUtils.atan2(d, b) * MathUtils.radDeg;
    }

    public float getWorldScaleX() {
        return (float) Math.sqrt(a * a + b * b) * worldSignX;
    }

    public float getWorldScaleY() {
        return (float) Math.sqrt(c * c + d * d) * worldSignY;
    }

    public Matrix3 getWorldTransform(Matrix3 worldTransform) {
        if (worldTransform == null) throw new IllegalArgumentException("worldTransform cannot be null.");
        float[] val = worldTransform.val;
        val[M00] = a;
        val[M01] = b;
        val[M10] = c;
        val[M11] = d;
        val[M02] = worldX;
        val[M12] = worldY;
        val[M20] = 0;
        val[M21] = 0;
        val[M22] = 1;
        return worldTransform;
    }

    public Vector2 worldToLocal(Vector2 world) {
        float x = world.x - worldX, y = world.y - worldY;
        float a = this.a, b = this.b, c = this.c, d = this.d;
        float invDet = 1 / (a * d - b * c);
        world.x = (x * d * invDet - y * b * invDet);
        world.y = (y * a * invDet - x * c * invDet);
        return world;
    }

    public Vector2 localToWorld(Vector2 local) {
        float x = local.x, y = local.y;
        local.x = x * a + y * b + worldX;
        local.y = x * c + y * d + worldY;
        return local;
    }

    public String toString() {
        return data.name;
    }
}
