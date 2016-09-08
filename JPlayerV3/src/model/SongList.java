package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import view.IObserver;

public class SongList implements IPlayable 
{
	ArrayList<IPlayable> songList;	// 歌曲列表
	private int currentsongIndex;// 当前歌曲编号
	private IPlayable currentsong; // 当前歌曲
	private  boolean isOrder;// 是否顺序播放
	// 观察者
	private IObserver observer;

	public SongList()
	{
		songList = new ArrayList<IPlayable>();
		currentsongIndex = 0;
		currentsong = null;
		isOrder = true;
	}
	
	// 歌曲列表信息
	// 歌曲总数
	public int getListSize()
	{
		return songList.size();
	}
	// 设置播放歌曲
	public void playbyindex(int index)
	{
		setCurrentSong(index);
		playSong();
	}	
	// 播放下一首
	public void playnext()
	{
		if (getOrder())
		{
			if (currentsongIndex < songList.size() - 1)
			{
				setCurrentSong(++currentsongIndex);
				playSong();
			}
		}
		else
		{
			Random random = new Random();
			setCurrentSong(random.nextInt(getListSize()));
			playSong();
		}
	}
	// 播放上一首
	public void playprevious()
	{
		if (getOrder())
		{
			if (currentsongIndex > 0)
			{
				setCurrentSong(--currentsongIndex);
				playSong();
			}
		}
		else
		{
			Random random = new Random();
			setCurrentSong(random.nextInt(getListSize()));
			playSong();
		}
	}
	// 设置当前播放歌曲
	public void setCurrentSong(int index)
	{
		assert((index >= 0) && (index < songList.size()));
		this.currentsongIndex = index;
		currentsong = songList.get(currentsongIndex);
	}
	// 得到当前歌曲编号
	public int getcurrentindex()
	{
		return currentsongIndex;
	}
	// 得到当前播放歌曲
	public IPlayable getCurrentSong()
	{
		return currentsong;
	}
	// 设置播放次序
	public void setOrder()
	{
		isOrder = !isOrder;
		observer.updateView();
	}
	public boolean getOrder()
	{
		return isOrder;
	}
	// 得到歌曲列表
	public ArrayList<IPlayable> getSongList()
	{
		return songList;
	}
	// 添加歌曲
	public void addSong(IPlayable songs)
	{
		if (songs instanceof SongList)
		{
			ArrayList<IPlayable> list = ((SongList) songs).getSongList();
			Iterator<IPlayable> iter = list.iterator();
			while (iter.hasNext())
			{
				songList.add(iter.next());
			}
		}
		else
		{
			songList.add(songs);
		}
		if (currentsong == null)
		{
			currentsongIndex = 0;
			currentsong = songList.get(0);
		}
	}
	// 添加文件夹下的所有歌曲
	public void addSong(String songDir)
	{
		File filedir = new File(songDir);
        File[] filelist = filedir.listFiles();
        for (File file : filelist) 
        {
            String filename = file.getName().toLowerCase();
            if (filename.endsWith(".mp3")) 
            {
            	IPlayable song = new MP3Song(file.getAbsolutePath(),observer);
                songList.add(song);
            }
        }
		if (currentsong == null)
		{
			currentsongIndex = 0;
			currentsong = songList.get(0);
		}
	}
	// 删除歌曲
	public void delSong(int index)
	{
		songList.remove(index);
	}
	
	
	// 播放
	public void playSong() 
	{
		currentsong.playSong();
		observer.updateView();
	}

	public void stopSong() 
	{
		currentsong.stopSong();
		observer.updateView();
	}

	public boolean isPause()
	{
		return currentsong.isPause();
	}

	public boolean isStop() 
	{
		return currentsong.isStop();
	}

	public boolean hasStop() 
	{
		return currentsong.hasStop();
	}

	public void setPause(boolean isPause) 
	{
		currentsong.setPause(isPause);
		observer.updateView();

	}

	// 得到歌曲属性
	public void setFilePath(String filePath) 
	{
		currentsong.setFilePath(filePath);
	}

	public String getFilePath()
	{
		return currentsong.getFilePath();
	}

	public String getSongName() 
	{

		return currentsong.getSongName();
	}

	public String getArtist() 
	{
		return currentsong.getArtist();
	}

	public String getAlbum()
	{

		return currentsong.getAlbum();
	}

	public String getYear() 
	{
		return currentsong.getYear();
	}

	public String getComment()
	{
		return currentsong.getComment();
	}

	public long getDuration()
	{
		return currentsong.getDuration();
	}

	public void registerObserver(IObserver o)
	{
		this.observer = o;
	}

}
