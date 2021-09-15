package com.example.twilliovideocallingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twilio.video.BandwidthProfileOptions;
import com.twilio.video.Camera2Capturer;
import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalDataTrack;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.Participant;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoBandwidthProfileOptions;
import com.twilio.video.VideoCapturer;
import com.twilio.video.VideoView;

import java.util.List;
import java.util.Locale;

import tvi.webrtc.Camera2Enumerator;
import tvi.webrtc.CapturerObserver;
import tvi.webrtc.SurfaceTextureHelper;

public class MainActivity extends AppCompatActivity {
    Room roomg;
    LocalVideoTrack videoTrack;
    LocalAudioTrack audioTrack;
    LocalDataTrack dataTrack;
    VideoView videoview;
    ToastClass toast;
    String frontCameraId;
    CameraCapturer cameracapturer;

    TextView textView;

    EditText editText;
    Button connectuserbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checking toast only
        toast = new ToastClass(this);

        videoview = (VideoView) findViewById(R.id.videoview);
        connectuserbtn=findViewById(R.id.connectuser);
        editText=findViewById(R.id.userconnectedittext);
        textView = findViewById(R.id.textview);

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        }

        audioTrack = LocalAudioTrack.create(this, true);
        videoTrack = LocalVideoTrack.create(this, true, videoCapturer);

        Button viewvideobtn=findViewById(R.id.viewvideobtn);
        viewvideobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocalVideoTrack localVideoTrack = LocalVideoTrack.create(MainActivity.this, true, cameracapturer);
                videoview.setMirror(true);
                localVideoTrack.addSink(videoview);
                localVideoTrack.release();
            }
        });







        connectuserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            connectoption(editText.getText().toString());

                //connectToVIdeo.connecttovideo(connectoption(editText.getText().toString()));

            }
        });



        Camera2Enumerator camera2Enumerator = new Camera2Enumerator(this);
        String frontCameraId = null;
        for (String cameraId : camera2Enumerator.getDeviceNames()) {
            if (camera2Enumerator.isFrontFacing(cameraId)) {
                frontCameraId = cameraId;
                break;
            }
        }

       if(frontCameraId != null) {

            cameracapturer = new CameraCapturer(this, frontCameraId);
            LocalVideoTrack localVideoTrack = LocalVideoTrack.create(this,true, cameracapturer);
            localVideoTrack.addSink(videoview);
            localVideoTrack.release();
            localVideoTrack.release();
        }
        else {
            toast.setToast("error in camera");
        }
    }



public ConnectOptions connectoption(String str) {
    ConnectOptions connectOptions = new ConnectOptions.Builder(getString(R.string.accesstoken))
            .roomName(str)
            // .audioTracks(audioTrack)
            //.videoTracks( videoTrack)
            // .dataTracks( dataTrack)
            .build();
    roomg = Video.connect(this, connectOptions, roomListenerl);
    return connectOptions;
}


    Room.Listener roomListenerl = new Room.Listener() {
        @Override
        public void onConnected(@NonNull Room room) {
            toast.setToast("in connected");
            textView.append(room.getName().toString()+" is connected \n");
        }
        @Override
        public void onConnectFailure(@NonNull Room room, @NonNull TwilioException twilioException) {
            toast.setToast(twilioException.toString());
            textView.setText(twilioException.toString());
        }

        @Override
        public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
            toast.setToast(twilioException.toString());
            textView.setText(twilioException.toString());
        }

        @Override
        public void onReconnected(@NonNull Room room) {
            toast.setToast("in reconnect listner");
            textView.setText("in reconneting");
        }

        @Override
        public void onDisconnected(@NonNull Room room, @Nullable TwilioException twilioException) {
            toast.setToast(twilioException.toString());
            textView.setText("in ondisconnect");
        }

        @Override
        public void onParticipantConnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
            toast.setToast(remoteParticipant.toString());
            textView.setText("in participant connected");
        }

        @Override
        public void onParticipantDisconnected(@NonNull Room room, @NonNull RemoteParticipant remoteParticipant) {
            toast.setToast(remoteParticipant.toString());
            textView.setText("in participant disconnected");
        }

        @Override
        public void onRecordingStarted(@NonNull Room room) {

        }

        @Override
        public void onRecordingStopped(@NonNull Room room) {
            if (room.isRecording()) {
                toast.setToast("recording...");

            } else {
                toast.setToast("recording stopped");
            }
        }
    };


    VideoCapturer videoCapturer = new VideoCapturer() {
        @Override
        public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver) {
        videoview.setBackground(getDrawable(R.drawable.ic_launcher_foreground));

        }

        @Override
        public void startCapture(int i, int i1, int i2) {

        }

        @Override
        public void stopCapture() throws InterruptedException {

        }

        @Override
        public boolean isScreencast() {
            return false;
        }
    };




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            Toast.makeText(MainActivity.this, "granted", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    RemoteParticipant.Listener participantlistner=new RemoteParticipant.Listener() {
        @Override
        public void onAudioTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onAudioTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onAudioTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

        }

        @Override
        public void onAudioTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull TwilioException twilioException) {

        }

        @Override
        public void onAudioTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication, @NonNull RemoteAudioTrack remoteAudioTrack) {

        }

        @Override
        public void onVideoTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }

        @Override
        public void onVideoTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }

        @Override
        public void onVideoTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {
           videoview.setMirror(false);
           remoteVideoTrack.addSink(videoview);
        }

        @Override
        public void onVideoTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull TwilioException twilioException) {

        }

        @Override
        public void onVideoTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication, @NonNull RemoteVideoTrack remoteVideoTrack) {

        }

        @Override
        public void onDataTrackPublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

        }

        @Override
        public void onDataTrackUnpublished(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication) {

        }

        @Override
        public void onDataTrackSubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

        }

        @Override
        public void onDataTrackSubscriptionFailed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull TwilioException twilioException) {

        }

        @Override
        public void onDataTrackUnsubscribed(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteDataTrackPublication remoteDataTrackPublication, @NonNull RemoteDataTrack remoteDataTrack) {

        }

        @Override
        public void onAudioTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onAudioTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteAudioTrackPublication remoteAudioTrackPublication) {

        }

        @Override
        public void onVideoTrackEnabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }

        @Override
        public void onVideoTrackDisabled(@NonNull RemoteParticipant remoteParticipant, @NonNull RemoteVideoTrackPublication remoteVideoTrackPublication) {

        }
    };






}