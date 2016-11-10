package com.team.engine;

import static com.team.engine.Globals.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
 
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
 
public class Audio {
  /** Buffers hold sound data. */
  IntBuffer buffer = BufferUtils.createIntBuffer(1);
 
  /** Sources are points emitting sound. */
  IntBuffer source = BufferUtils.createIntBuffer(1);
 
  /** Position of the source sound. */
  FloatBuffer sourcePos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
 
  /** Velocity of the source sound. */
  FloatBuffer sourceVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 10.0f, 0.0f }).rewind();
 
  /** Position of the listener. */
  FloatBuffer listenerPos = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
 
  /** Velocity of the listener. */
  FloatBuffer listenerVel = (FloatBuffer)BufferUtils.createFloatBuffer(3).put(new float[] { 0.0f, 0.0f, 0.0f }).rewind();
 
  /** Orientation of the listener. (first 3 elements are "at", second 3 are "up") */
  FloatBuffer listenerOri = (FloatBuffer)BufferUtils.createFloatBuffer(6).put(new float[] { 0.0f, 0.0f, -1.0f,  0.0f, 1.0f, 0.0f }).rewind();
  
  public Audio(String file) {
    if(!loadALData(file)) {
      print("Error loading data.");
      return;
    }
  }
 
  /**
  * boolean LoadALData()
  *
  *  This function will load our sample data from the disk using the Alut
  *  utility and send the data into OpenAL as a buffer. A source is then
  *  also created to play that buffer.
  */
  boolean loadALData(String file) {
    // Load wav data into a buffer.
    AL10.alGenBuffers(buffer);
    

    
    if(AL10.alGetError() != AL10.AL_NO_ERROR)
      return false;
 
    WaveData waveFile = WaveData.create(Settings.RESOURCE_PATH + file);
 
    AL10.alBufferData(buffer.get(0), waveFile.format, waveFile.data, waveFile.samplerate);
    waveFile.dispose();
 
    // Bind the buffer with the source.
    AL10.alGenSources(source);
 
    if (AL10.alGetError() != AL10.AL_NO_ERROR)
      return false;
 
    AL10.alSourcei(source.get(0), AL10.AL_BUFFER, buffer.get(0));
    AL10.alSourcef(source.get(0), AL10.AL_PITCH, 1.0f);
    AL10.alSourcefv(source.get(0), AL10.AL_POSITION, sourcePos);
    AL10.alSourcefv(source.get(0), AL10.AL_VELOCITY, sourceVel);
 
    // Do another error check and return.
    if (AL10.alGetError() == AL10.AL_NO_ERROR)
      return true;
 
    return false;
  }
 
  /**
   * void killALData()
   *
   *  We have allocated memory for our buffers and sources which needs
   *  to be returned to the system. This function frees that memory.
   */
  public void delete() {
    AL10.alDeleteSources(source);
    AL10.alDeleteBuffers(buffer);
  }
 
  public void play(boolean loop, float volume) {
	  AL10.alListenerfv(AL10.AL_POSITION,    listenerPos);
	  AL10.alListenerfv(AL10.AL_VELOCITY,    listenerVel);
	  AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOri);
	  AL10.alSourcef(source.get(0), AL10.AL_GAIN, volume);
	  AL10.alSourcei(source.get(0), AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    
	  AL10.alSourcePlay(source.get(0));
  }
}