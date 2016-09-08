package model;

import view.ObserverInterface;;

public interface SongInterface
{
	// ����
	void playSong();
	void stopSong();
	void setPause(boolean isPause);
	boolean isPause();
	boolean isStop();
	boolean hasStop();
	
	
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
	void registerObserver(ObserverInterface o);
}