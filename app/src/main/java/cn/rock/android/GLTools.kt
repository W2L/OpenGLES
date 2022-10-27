package cn.rock.android

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import cn.rock.android.Utils.Companion.buffer
import cn.rock.android.Utils.Companion.readString

class GLTools {
    companion object {

        fun newProgram(context: Context, vShader: Int, fShader: Int): Int {
            val vsStr = readString(context, vShader)
            val fsStr = readString(context, fShader)
            val vShaderId = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER).apply {
                GLES30.glShaderSource(this, vsStr)
                GLES30.glCompileShader(this)
                val status = IntArray(1)
                GLES30.glGetShaderiv(this, GLES30.GL_COMPILE_STATUS, status, 0)
                if (status[0] == 0) {
                    val log = GLES30.glGetShaderInfoLog(this)
                    println("顶点着色器编译失败:$log")
                }
            }
            val fShaderId = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER).apply {
                GLES30.glShaderSource(this, fsStr)
                GLES30.glCompileShader(this)
                val status = IntArray(1)
                GLES30.glGetShaderiv(this, GLES30.GL_COMPILE_STATUS, status, 0)
                if (status[0] == 0) {
                    val log = GLES30.glGetShaderInfoLog(this)
                    println("片段着色器编译失败:$log")
                }
            }
            val program = GLES30.glCreateProgram().apply {
                GLES30.glAttachShader(this, vShaderId)
                GLES30.glAttachShader(this, fShaderId)
                GLES30.glLinkProgram(this)
                val status = IntArray(1)
                GLES30.glGetProgramiv(this, GLES30.GL_LINK_STATUS, status, 0)
                if (status[0] == 0) {
                    val log = GLES30.glGetProgramInfoLog(this)
                    println("GL程序链接结果失败:$log")
                }
                GLES30.glDetachShader(this, vShaderId)
                GLES30.glDetachShader(this, fShaderId)
            }
            return program
        }

        fun bufferData(program: Int, vertexCoords: FloatArray, textureCoords: FloatArray, indices: IntArray): Int {
            val vao = IntArray(1)
            val vbo = IntArray(2)
            GLES30.glGenVertexArrays(1, vao, 0)
            GLES30.glGenBuffers(2, vbo, 0)
            GLES30.glBindVertexArray(vao[0])
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, (vertexCoords.size + textureCoords.size) * 4, null, GLES30.GL_STATIC_DRAW)
            GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, vertexCoords.size * 4, buffer(vertexCoords))
            GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, vertexCoords.size * 4, textureCoords.size * 4, buffer(textureCoords))

            val vLoc = GLES30.glGetAttribLocation(program, "aPosition")
            val tLoc = GLES30.glGetAttribLocation(program, "aTextureCoords")
            GLES30.glEnableVertexAttribArray(vLoc)
            GLES30.glVertexAttribPointer(vLoc, 2, GLES30.GL_FLOAT, false, 2 * 4, 0)
            GLES30.glEnableVertexAttribArray(tLoc)
            GLES30.glVertexAttribPointer(tLoc, 2, GLES30.GL_FLOAT, false, 2 * 4, vertexCoords.size * 4)

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbo[1])
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.size * 4, buffer(indices), GLES30.GL_STATIC_DRAW)

            return vao[0]
        }

        fun createOESTexture(): Int {
            val textureId = IntArray(1)
            GLES30.glGenTextures(1, textureId, 0)
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId[0])
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE)
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
            GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
            GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_NONE)
            return textureId[0]
        }
    }
}