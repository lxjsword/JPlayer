package model;

import view.IObserver;

public interface IPlayable
{
	// �������
	void playSong();
	void stopSong();
	boolean isPause();
	boolean isStop();
	boolean hasStop();
	void setPause(boolean isPause);
	
	// �õ���������
	void setFilePath(String filePath);
	String getFilePath();
	String getSongName();
	String getArtist();
	String getAlbum();
	String getYear();
	String getComment();
	long getDuration();
		
	// ע��۲���
	void registerObserver(IObserver o);
}
