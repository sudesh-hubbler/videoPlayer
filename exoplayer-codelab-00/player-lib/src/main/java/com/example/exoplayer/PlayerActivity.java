/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    SimpleExoPlayerView playerView;
    SimpleExoPlayer player;
    ComponentListner componentListner;
//    Button takeSnapshot;
    ImageView snapshotResultImg;
    ImageButton increaseSpeed;
    ImageButton decreaseSpeed;
    ImageButton imgBtn_snapshot;
    TextView txt_speedValue;
    View ExoPlayerDisplayArea;

    private long playbackPosition = 0;
    private int currentWindow = 0;
    private float playbackSpeed = 1.0f;
    private boolean playWhenReady;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
//    private TextureView textureView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);
    playerView = findViewById(R.id.videoView);
    increaseSpeed = findViewById(R.id.speed_increase);
    decreaseSpeed = findViewById(R.id.speed_decrease);
    imgBtn_snapshot = findViewById(R.id.snapshot);
    txt_speedValue = findViewById(R.id.speed_value);
//    textureView = findViewById(R.id.textureView);

    ExoPlayerDisplayArea  = findViewById(R.id.exo_progress);

    increaseSpeed.setOnClickListener(this);
    decreaseSpeed.setOnClickListener(this);
    imgBtn_snapshot.setOnClickListener(this);

      checkStoragePermission();
  }

    private void checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
    }

    @Override
    public void onClick(View v) {

      if(v.getId() == R.id.speed_increase)
      {
          playbackSpeed += 0.25f;
          txt_speedValue.setText(Float.toString(playbackSpeed));
          player.setPlaybackParameters(new PlaybackParameters(playbackSpeed,1.0f));
      }
      else if(v.getId() == R.id.speed_decrease)
      {
          playbackSpeed -= 0.25f;
          txt_speedValue.setText(Float.toString(playbackSpeed));
          player.setPlaybackParameters(new PlaybackParameters(playbackSpeed,1.0f));
      }
      else if(v.getId() == R.id.snapshot)
      {
//          takeScreenshot();
          takeSnapsAndSaveToDrive();
      }
    }

    private void takeSnapsAndSaveToDrive() {
        Bitmap b = takeScreenShot();
        Screenshot.SaveImageToLocalDirectory(b);
    }

    public Bitmap takeScreenShot() {
//        v.setDrawingCacheEnabled(true);
//        v.buildDrawingCache(true);
//        Bitmap snap = Bitmap.createBitmap(v.getDrawingCache());
//        v.setDrawingCacheEnabled(false);
//        return snap;

//        View v1 = getWindow().getDecorView().getRootView();
        TextureView textureView = (TextureView) playerView.getVideoSurfaceView();
        Bitmap bitmap = textureView.getBitmap();

        return bitmap;
    }

//    private void captureScreen() {
//        View v = getWindow().getDecorView().getRootView();
//        v.setDrawingCacheEnabled(true);
//        Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
//        v.setDrawingCacheEnabled(false);
//        try {
//            FileOutputStream fos = new FileOutputStream(new File(Environment
//                    .getExternalStorageDirectory().toString(), "SCREEN"
//                    + System.currentTimeMillis() + ".png"));
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
//        TakeSnapshot();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void initializePlayer() {

      if(player==null)
      {
          TrackSelection.Factory adaptiveTrackSelectionFactory =
                  new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

          player = ExoPlayerFactory.newSimpleInstance(
                  new DefaultRenderersFactory(this),
                  new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());

          playerView.setPlayer(player);
//          player.setVideoTextureView(textureView);

          player.setPlayWhenReady(playWhenReady);
          player.seekTo(currentWindow, playbackPosition);
          player.setPlaybackParameters(new PlaybackParameters(1.0f,1.0f));
          Uri uri = Uri.parse(getString(R.string.media_url_mp4));
          MediaSource mediaSource = buildMediaSource(uri);
          player.prepare(mediaSource, true, false);
      }


    }
 /*   private void TakeSnapshot() {
        View textureView = playerView.getVideoSurfaceView();
//        Bitmap bitmap = Bitmap.createBitmap(textureView.getWidth(), textureView.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = getViewBitmapNew(textureView);
//        Canvas canvas = new Canvas(bitmap);
        snapshotResultImg.setImageBitmap(bitmap);
//        textureView.draw(canvas);
        Log.i("bitmap", "TakeSnapshot: "+bitmap.getWidth());
  }*/

   /* private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("hjk",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("ds", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("sd", "Error accessing file: " + e.getMessage());
        }
    }*/

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/HubblerPictures/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    /*private Bitmap getViewBitmapNew(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("img", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }*/

    /*Bitmap getViewBitmap(View view)
    {
        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        int width = view.getWidth();
        int height = view.getHeight();

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c);

        return b;
    }*/

    private MediaSource buildMediaSource(Uri uri) {


        // these are reused for both media sources we create below
        DefaultExtractorsFactory extractorsFactory =
                new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory =
                new DefaultHttpDataSourceFactory( "user-agent");

        ExtractorMediaSource videoSource =
                new ExtractorMediaSource.Factory(
                        new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                        createMediaSource(uri);

//        HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl, MAIN_BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE);
//        ExtractorMediaSource videoSource =
//                new ExtractorMediaSource.Factory(
//                        new DefaultHttpDataSourceFactory("exoplayer-codelab")).
//                        createMediaSource(uri);

//        Uri audioUri = Uri.parse(getString(R.string.media_url_mp4));
        ExtractorMediaSource audioSource =
                new ExtractorMediaSource.Factory(
                        new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                        createMediaSource(uri);

        return new ConcatenatingMediaSource(audioSource, videoSource);

//        DataSource.Factory manifestDataSourceFactory =
//                new DefaultHttpDataSourceFactory("ua");
//        DashChunkSource.Factory dashChunkSourceFactory =
//                new DefaultDashChunkSource.Factory(
//                        new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER));
//        return new DashMediaSource.Factory(dashChunkSourceFactory,
//                manifestDataSourceFactory).createMediaSource(uri);
    }

    private void takeScreenshot() {
        Date now = new Date();
        String outputDir;
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
//            String mPath = Environment.DIRECTORY_DOWNLOADS + "/" + now + ".jpg";

            String path = Environment.getExternalStorageDirectory() + "/" + "HubblerPictures/";

            File dir = new File(path);
            if(!dir.exists())
            {
                dir.mkdirs();
            }
            outputDir = dir.getAbsolutePath()+"/abc.JPEG";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(outputDir);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

//            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private class ComponentListner implements ExoPlayer.EventListener, VideoRendererEventListener,
            AudioRendererEventListener{

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        }

        @Override
        public void onSeekProcessed() {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }
    }
}
