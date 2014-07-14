package dttv.app;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import dttv.app.widget.GlVideoView;
import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;


public class PlayActivity extends Activity implements OnClickListener{
	
	private final String TAG = "DT-PLAYING";
    private int surface_width = 320;
    private int surface_height = 240;
    
    private final int PLAYER_STATUS_IDLE=0;
    private final int PLAYER_STATUS_RUNNING=1;
    private final int PLAYER_STATUS_PAUSED=2;
    private final int PLAYER_STATUS_QUIT=3;
    
    private String strFileName;
    private int playerStatus=PLAYER_STATUS_IDLE;
        
	//Native API declare
    private static native void native_gl_resize(int w, int h);
	private native int native_ui_init(int w, int h);
	private native int native_disp_frame();
	private native int native_ui_stop();
	
	private native int native_playerStart(String url);
	private native int native_playerPause();
	private native int native_playerResume();
	private native int native_playerStop();
	private native int native_playerSeekTo(int pos);
	

	
	private GlVideoView glSurfaceView;
	private SeekBar playerBar;
	private TextView currentPositionTxt,totalTimeTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
		//start playing video
		Intent intent = getIntent();
		strFileName = intent.getStringExtra(MainActivity.FILE_MSG);
		Log.d(TAG, "Start playing "+strFileName);
		
		initWidget();
		//glSurfaceView = new GLSurfaceView(this);
		
		glSurfaceView = (GlVideoView)findViewById(R.id.glvideo_view);
        glSurfaceView.setRenderer(new GLSurfaceViewRender());  
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        this.setContentView(glSurfaceView);

	}
	
	private void initWidget(){
		playerBar = (SeekBar)findViewById(R.id.dt_play_progress_seekbar);
		currentPositionTxt = (TextView)findViewById(R.id.dt_play_current_time);
		totalTimeTxt = (TextView)findViewById(R.id.dt_play_total_time);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG,"--PAUSE--");    
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG,"--DESTROY--");

        if(playerStatus != PLAYER_STATUS_IDLE)
        {
            native_playerStop();
            playerStatus = PLAYER_STATUS_IDLE;
        }
    }


	class GLSurfaceViewRender implements GLSurfaceView.Renderer {  
		  
        @Override  
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {  
            Log.i(TAG, "onSurfaceCreated");  
             
            // 设置背景颜色 - load backgroud picture 
            //gl.glClearColor(0.0f, 0f, 1f, 0.5f);
        }  
  
        @Override  
        public void onSurfaceChanged(GL10 gl, int width, int height) {  
            // 设置输出屏幕大小 
        	native_gl_resize(width, height);
        	surface_width = width;
        	surface_height = height;

            if(playerStatus == PLAYER_STATUS_IDLE)
            {
                Log.i(TAG, "surface changed, start play:"+strFileName);  
                native_ui_init(surface_width,surface_height); 
                native_playerStart(strFileName);
                playerStatus = PLAYER_STATUS_RUNNING;
            }
            
            //other case
        }
  
        @Override  
        public void onDrawFrame(GL10 gl) {  
            //Log.i(TAG, "onDrawFrame");  
            // 清除屏幕和深度缓存(如果不调用该代码, 将不显示glClearColor设置的颜色)  
            // 同样如果将该代码放到 onSurfaceCreated 中屏幕会一直闪动  
            
            //gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            native_disp_frame();
  
        }  
  
    }  
	
	static {
		System.loadLibrary("dtp_jni");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.dt_play_pause_btn:
			
			break;
		case R.id.dt_play_next_btn:
			
			break;
		case R.id.dt_play_prev_btn:
			
			break;
		}
	}

}

/*
class GlBufferView extends GLSurfaceView {

	public GlBufferView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setRenderer(new MyRenderer());
		requestFocus();
		setFocusableInTouchMode(true);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);   // render on demand
		//setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//continue render  
	}

	class MyRenderer implements GLSurfaceView.Renderer {
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig c) { 
		// do nothing 
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int w, int h) {
			//native_gl_resize(w, h);
			//do nothing too
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			//native_gl_render();
			//draw frame
		}
	}


	
}
*/
