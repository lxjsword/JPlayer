package view;
import javax.swing.table.*;

import model.Song;
import model.SongInterface;
import model.SongList;

public class SongTableModel extends AbstractTableModel
{
	private SongList songlist;
		
	public SongTableModel(SongList songlist)
	{
		this.songlist = songlist;
	}

	public int getRowCount()
	{
		return songlist.getListSize();
	}
	
	public int getColumnCount()
	{
		return 3;
	}
	
	public Object getValueAt(int r, int c)
	{
		SongInterface song = songlist.getSongList().get(r);
		String s = null;
		switch (c)
		{
		case 0: s = song.getSongName();break;
		case 1: s = song.getArtist();break;
		case 2: s = song.getAlbum();break;
		}
		return s;		
	}
	
	public String getColumnName(int c)
	{
		String s = null;
		switch (c)
		{
		case 0: s = "歌曲名";break;
		case 1: s = "艺术家";break;
		case 2: s = "专辑";break;
		}
		return s;		
	}
}
