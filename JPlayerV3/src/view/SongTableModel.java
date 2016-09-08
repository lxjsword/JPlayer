package view;

import javax.swing.table.AbstractTableModel;

import model.IPlayable;
import model.SongList;

class SongTableModel extends AbstractTableModel 
{
	private IPlayable playlist;
	
	public SongTableModel(IPlayable playlist)
	{
		this.playlist = playlist;
	}
	
	@Override
	public int getColumnCount() 
	{
		return 3;
	}

	@Override
	public int getRowCount()
	{
		if (playlist instanceof SongList)
			return ((SongList)playlist).getListSize();
		else
			return 1;
	}

	@Override
	public Object getValueAt(int r, int c) 
	{
		IPlayable song= null;
		if (playlist instanceof SongList)
			song = ((SongList)playlist).getSongList().get(r);
		else
			song = playlist;
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
