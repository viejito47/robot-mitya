package ru.dzakhov;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Класс, управляющий звуком.
 * @author Не Дмитрий.
 *
 */
public final class SoundManager {
	/**
	 * Объект SoundManager.
	 */
	private static SoundManager mInstance;
	
	/**
	 * Пул звуков.
	 */
	private static SoundPool mSoundPool; 

	/**
	 * HashMap звуков.
	 */
	private static HashMap<Integer, Integer> mSoundPoolMap; 

	/**
	 * Системный аудио-менеджер.
	 */
	private static AudioManager  mAudioManager;
	
	/**
	 * Контекст.
	 */
	private static Context mContext;
	
	/**
	 * Закрытый конструктор класса.
	 */
	private SoundManager() {   
	}
	
	/**
	 * Requests the instance of the Sound Manager and creates it
	 * if it does not exist.
	 * 
	 * @return Returns the single instance of the SoundManager.
	 */
	public static synchronized SoundManager getInstance() { 
	    if (mInstance == null) {
			mInstance = new SoundManager();
		}
	    return mInstance;
	 }
	
	/**
	 * Initialises the storage for the sounds.
	 * 
	 * @param theContext The Application context.
	 */
	public static  void initSounds(final Context theContext) { 
		 mContext = theContext;
		 
		 final int maxStreams = 4;
	     mSoundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
	     
	     mSoundPoolMap = new HashMap<Integer, Integer>(); 
	     
	     mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); 	    
	} 
	
	/**
	 * Add a new Sound to the SoundPool.
	 * 
	 * @param index - The Sound Index for Retrieval.
	 * @param soundID - The Android ID for the Sound asset.
	 */
	public static void addSound(final int index, final int soundID)	{
		mSoundPoolMap.put(index, mSoundPool.load(mContext, soundID, 1));
	}
	
	/**
	 * Loads the various sound assets.
	 * Currently hardcoded but could easily be changed to be flexible.
	 */
	public static void loadSounds()	{
		mSoundPoolMap.put(1, mSoundPool.load(mContext, R.raw.gun, 1));
		mSoundPoolMap.put(2, mSoundPool.load(mContext, R.raw.wilhelm_scream, 1));
	}
	
	/**
	 * Plays a Sound.
	 * 
	 * @param index - The Index of the Sound to be played.
	 * @param speed - The Speed to play not, not currently used but included for compatibility.
	 */
	public static void playSound(final int index, final float speed) { 		
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, speed); 
	}
	
	/**
	 * Stop a Sound.
	 * @param index - index of the sound to be stopped.
	 */
	public static void stopSound(final int index) {
		mSoundPool.stop(mSoundPoolMap.get(index));
	}
	
	/**
	 * Освобождение ресурсов.
	 */
	public static void cleanup() {
		mSoundPool.release();
		mSoundPool = null;
	    mSoundPoolMap.clear();
	    mAudioManager.unloadSoundEffects();
	    mInstance = null;
	}
}