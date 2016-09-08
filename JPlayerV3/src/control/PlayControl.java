package control;

import java.util.ArrayList;

import model.IPlayable;
import model.SongList;
import view.IObserver;

public class PlayControl
{
	SongList list;
	
	public PlayControl(String songDir, IObserver o)
	{
		list = new SongList();
		list.registerObserver(o);
		list.addSong(songDir);
	}
	
	public IPlayable getPlaylist()
	{
		return list;
	}
	
	public void playbyindex(int index)
	{
		list.playbyindex(index);
	}
	
	public void playprevious()
	{
		list.playprevious();
	}
	
	public void playnext()
	{
		list.playnext();
	}
	public boolean isStop()
	{
		return list.isStop();
	}
	public boolean hasStop()
	{
		return list.hasStop();
	}
	
	public void stopSong()
	{
		list.stopSong();
	}
	
	public boolean isPause()
	{
		return list.isPause();
	}
	
	public void setPause(boolean isPause)
	{
		list.setPause(isPause);
	}
	
	public boolean getOrder()
	{
		return list.getOrder();
	}
	
	public int getcurrentindex()
	{
		return list.getcurrentindex();
	}
	
	public IPlayable getCurrentSong()
	{
		return list.getCurrentSong();
	}
	
	public ArrayList<IPlayable> getSongList()
	{
		return list.getSongList();
	}
	
	public void setOrder()
	{
		list.setOrder();
	}
}
