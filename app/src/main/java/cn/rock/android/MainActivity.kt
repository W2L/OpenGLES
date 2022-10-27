package cn.rock.android

import android.app.Activity
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Build
import android.os.Bundle
import android.view.Surface
import androidx.annotation.RequiresApi
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : Activity(), GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private val vertexCoordinates = floatArrayOf(
        -1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f
    )
    private val textureCoordinates = floatArrayOf(
        0f, 1f, 0f, 0f, 1f, 1f, 1f, 0f
    )
    private val indexes = intArrayOf(
        0, 1, 2, 1, 2, 3
    )

    private var mProgram: Int = 0
    private var mVao: Int = 0
    private var mTextureId: Int = 0
    private var mMvpMatrixLoc: Int = 0
    private var mTextureMatrixLoc: Int = 0
    private var mMvpMatrix: FloatArray = FloatArray(16)
    private var mTextureMatrix: FloatArray = FloatArray(16)
    private lateinit var mSurfaceTure: SurfaceTexture
    private val mFrameAvailable: AtomicBoolean = AtomicBoolean(false)
    private var mMediaPlayer: MediaPlayer? = null
    private var mGLSurfaceView: GLSurfaceView? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGLSurfaceView = findViewById<GLSurfaceView>(R.id.surfaceView).apply {
            setEGLContextClientVersion(3)
            setRenderer(this@MainActivity)
        }
        mMediaPlayer = MediaPlayer().apply {
            reset()
            setDataSource(assets.openFd("red_alert.mp4"))
            isLooping = true
            prepare()
            start()
        }
    }

    override fun onResume() {
        super.onResume()
        mMediaPlayer?.start()
        mGLSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMediaPlayer?.pause()
        mGLSurfaceView?.onPause()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgram = GLTools.newProgram(this, R.raw.vertex, R.raw.fragment)
        mMvpMatrixLoc = GLES30.glGetUniformLocation(mProgram, "uMvpMatrix")
        mTextureMatrixLoc = GLES30.glGetUniformLocation(mProgram, "uTextureMatrix")
        mVao = GLTools.bufferData(mProgram, vertexCoordinates, textureCoordinates, indexes)
        mTextureId = GLTools.createOESTexture()

        mMediaPlayer?.let {
            mSurfaceTure = SurfaceTexture(mTextureId)
            mSurfaceTure.setOnFrameAvailableListener(this)
            val surface = Surface(mSurfaceTure)
            it.setSurface(surface)
            surface.release()
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        Matrix.setIdentityM(mMvpMatrix, 0)
        Matrix.setIdentityM(mTextureMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClearColor(1f, 0.5f, 0.2f, 1f)

        if (mFrameAvailable.get()) {
            mSurfaceTure.updateTexImage()
            mSurfaceTure.getTransformMatrix(mTextureMatrix)
        }

        GLES30.glUseProgram(mProgram)
        GLES30.glBindVertexArray(mVao)
        GLES30.glUniformMatrix4fv(mMvpMatrixLoc, 1, false, mMvpMatrix, 0)
        GLES30.glUniformMatrix4fv(mTextureMatrixLoc, 1, false, mTextureMatrix, 0)
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId)
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexes.size, GLES30.GL_UNSIGNED_INT, 0)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        mFrameAvailable.set(true)
    }
}