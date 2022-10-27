package cn.rock.android

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Utils {
    companion object {
        fun readString(context: Context, resId: Int): String {
            val builder = StringBuilder()
            context.resources.openRawResource(resId).use {
                val reader = BufferedReader(InputStreamReader(it))
                reader.readLines().forEach {
                    builder.append(it).append("\r\n")
                }
            }
            return builder.toString()
        }

        fun buffer(array: FloatArray): FloatBuffer {
            val buffer = ByteBuffer.allocateDirect(array.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
            buffer.put(array).position(0)
            return buffer
        }

        fun buffer(array: IntArray): IntBuffer {
            val buffer = ByteBuffer.allocateDirect(array.size * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
            buffer.put(array).position(0)
            return buffer
        }
    }
}