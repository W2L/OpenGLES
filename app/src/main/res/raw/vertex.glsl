attribute vec4 aPosition;
attribute vec2 aTextureCoords;

uniform mat4 uMvpMatrix;
uniform mat4 uTextureMatrix;

varying vec2 textureCoords;

void main() {
    textureCoords = (uTextureMatrix * vec4(aTextureCoords, 1, 1)).xy;
    gl_Position = uMvpMatrix * aPosition;
}