package com.ljr.opengl_a.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.ljr.opengl_a.GLRender;

public class OpenGL extends GLSurfaceView {
    public OpenGL(Context context) {
        super(context);
    }

    public OpenGL(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setRenderer(new GLRender(this));
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
