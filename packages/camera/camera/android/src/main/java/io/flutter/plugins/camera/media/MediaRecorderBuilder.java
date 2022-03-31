// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.camera.media;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import androidx.annotation.NonNull;
import java.io.IOException;
import io.flutter.plugins.camera.CameraProperties;
import android.util.Size;

public class MediaRecorderBuilder {
  static class MediaRecorderFactory {
    MediaRecorder makeMediaRecorder() {
      return new MediaRecorder();
    }
  }

  private final String outputFilePath;
  private final CamcorderProfile recordingProfile;
  private final CameraProperties cameraProperties;
  private final MediaRecorderFactory recorderFactory;

  private boolean enableAudio;
  private int mediaOrientation;

  public MediaRecorderBuilder(
      @NonNull CamcorderProfile recordingProfile, @NonNull String outputFilePath, @NonNull CameraProperties cameraProperties) {
    this(recordingProfile, outputFilePath, cameraProperties, new MediaRecorderFactory());
  }

  MediaRecorderBuilder(
      @NonNull CamcorderProfile recordingProfile,
      @NonNull String outputFilePath,
      @NonNull CameraProperties cameraProperties,
      MediaRecorderFactory helper) {
    this.outputFilePath = outputFilePath;
    this.recordingProfile = recordingProfile;
    this.cameraProperties = cameraProperties;
    this.recorderFactory = helper;
  }

  public MediaRecorderBuilder setEnableAudio(boolean enableAudio) {
    this.enableAudio = enableAudio;
    return this;
  }

  public MediaRecorderBuilder setMediaOrientation(int orientation) {
    this.mediaOrientation = orientation;
    return this;
  }

  public MediaRecorder build() throws IOException {
    MediaRecorder mediaRecorder = recorderFactory.makeMediaRecorder();

    // There's a fixed order that mediaRecorder expects. Only change these functions accordingly.
    // You can find the specifics here: https://developer.android.com/reference/android/media/MediaRecorder.
    if (enableAudio) mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
    mediaRecorder.setOutputFormat(recordingProfile.fileFormat);
    if (enableAudio) {
      mediaRecorder.setAudioEncoder(recordingProfile.audioCodec);
      mediaRecorder.setAudioEncodingBitRate(recordingProfile.audioBitRate);
      mediaRecorder.setAudioSamplingRate(recordingProfile.audioSampleRate);
    }
    mediaRecorder.setVideoEncoder(recordingProfile.videoCodec);
    mediaRecorder.setVideoEncodingBitRate(recordingProfile.videoBitRate);
    mediaRecorder.setVideoFrameRate(recordingProfile.videoFrameRate);
    Size availableSize = cameraProperties.availableSize(recordingProfile.videoFrameWidth, recordingProfile.videoFrameHeight);

    mediaRecorder.setVideoSize(availableSize.getWidth(), availableSize.getHeight());
    mediaRecorder.setOutputFile(outputFilePath);
    mediaRecorder.setOrientationHint(this.mediaOrientation);

    mediaRecorder.prepare();

    return mediaRecorder;
  }
}
