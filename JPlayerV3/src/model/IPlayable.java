package model;

import view.IObserver;

public interface IPlayable
{
	// 播放相关
	void playSong();
	void stopSong();
	boolean isPause();
	boolean isStop();
	boolean hasStop();
	void setPause(boolean isPause);
	
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
	void registerObserver(IObserver o);
}
