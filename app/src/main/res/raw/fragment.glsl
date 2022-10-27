#extension GL_OES_EGL_image_external: require
precision mediump float;

uniform samplerExternalOES u_TextureSampler;

varying vec2 textureCoords;

void main() {
    gl_FragColor = texture2D(u_TextureSampler, textureCoords);
}