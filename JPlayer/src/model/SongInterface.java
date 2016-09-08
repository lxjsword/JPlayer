package model;

import view.ObserverInterface;;

public interface SongInterface
{
	// 播放
	void playSong();
	void stopSong();
	void setPause(boolean isPause);
	boolean isPause();
	boolean isStop();
	boolean hasStop();
	
	
	// 得到歌曲属性
	void setFilePath(String filePath);
	String getFilePath();
	String getSongName();
	String getArtist();
	String getAlbum();
	String getYear();
	String getComment();
	long getDuration();
	
	// 注册观察者
	void registerObserver(ObserverInterface o);
}