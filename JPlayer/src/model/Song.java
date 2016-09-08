package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Timer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import view.ObserverInterface;

public class Song implements Runnable, SongInterface, Serializable
{
	// 歌曲信息
	private String songName;
	private String artist;
	private String album;
	private String year;
	private String comment;
	private long duration;
	
	// 文件信息
	private String filePath;
	private AudioInputStream audioInputStream;// 文件流
    private AudioFormat audioFormat;// 文件格式
    private SourceDataLine sourceDataLine;// 输出设备

    
    // 播放信息
	private boolean isStop;//是否该停止
	private boolean hasStop;//是否停止播放了
	private String control = ""; // 只是需要一个对象而已，这个对象没有实际意义  
	private boolean isPause = false;
	
	// 观察者
	private ObserverInterface observer;
	
	public Song()
	{
		
	}
	
	public Song(String filePath)
	{
		this.filePath = filePath;
		reload();
	}
	
	public void registerObserver(ObserverInterface o)
	{
		this.observer = o;
	}
	
	protected void reload()
	{
		File file = new File(filePath);
		AudioFileFormat baseFileFormat = null;
		AudioFormat baseFormat = null;
		try 
		{
			baseFileFormat = AudioSystem.getAudioFileFormat(file);
		} 
		catch (UnsupportedAudioFileException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		baseFormat = baseFileFormat.getFormat();
		// TAudioFileFormat properties
		if (baseFileFormat instanceof TAudioFileFormat)
		{
		    Map properties = ((TAudioFileFormat)baseFileFormat).properties();
		    songName = (String) properties.get("title");
		    artist = (String) properties.get("author");
		    album = (String) properties.get("album");
		    year = (String) properties.get("date");
		    comment = (String) properties.get("comment");
		    duration = (long) properties.get("duration");
		}
		isStop = true;
		hasStop = true;
	}
	// 播放
	public void playSong()
	{
		try
        {
            File file = new File(filePath);
            
            isStop = true;
            while (!hasStop) 
            {
                try 
                {
                    Thread.sleep(10);
                } 
                catch (Exception e)
                {
                }
            }
            // 取得文件输入流
            audioInputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioInputStream.getFormat();
            // 转换mp3文件编码
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) 
            {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        audioFormat.getSampleRate(), 16, audioFormat
                                .getChannels(), audioFormat.getChannels() * 2,
                        audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
                        audioInputStream);
            }
 
            // 打开输出设备
            DataLine.Info dataLineInfo = new DataLine.Info(
                    SourceDataLine.class, audioFormat,
                    AudioSystem.NOT_SPECIFIED);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
 
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
       
        isStop = false;
		Thread playThread = new Thread(this);
        playThread.start();
        hasStop = false;
	}
	public void stopSong()
	{
		isStop = true;
	}
	public void setPause(boolean isPause)
	{
		if (!isPause) {  
            synchronized (control) {  
                control.notifyAll();  
            }  
        }  
        this.isPause = isPause;  
	}
	public boolean isPause()
	{
		return this.isPause;
	}
	public boolean isStop()
	{
		return isStop;
	}
	public boolean hasStop()
	{
		return hasStop;
	}
	
	// 歌曲属性
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
		reload();
	}
	public String getFilePath()
	{
		return filePath;
	}
	public String getSongName()
	{
		return songName;
	}
	public String getArtist()
	{
		return artist;
	}
	public String getAlbum()
	{
		return album;
	}
	public String getYear()
	{
		return year;
	}
	public String getComment()
	{
		return year;		
	}
	public long getDuration()
	{
		return duration;
	}
	// Runnable接口实现
	public void run()
	{
		byte tempBuffer[] = new byte[320];
		try
		{
			int cnt;
			// 读取数据到缓存数据
			while ((cnt = audioInputStream.read(tempBuffer, 0,
					tempBuffer.length)) != -1)
			{
				
				if (isStop)
					break;
				synchronized (control)
				{  
	                if (isPause) 
	                {  
	                    try 
	                    {  
	                        control.wait();  
	                    }
	                    catch (InterruptedException e) 
	                    {  
	                        e.printStackTrace();  
	                    }  
	                }  
				}
				if (cnt > 0) 
				{
                    // 写入缓存数据
                    sourceDataLine.write(tempBuffer, 0, cnt);
                }
            }
            // Block等待临时数据被输出为空
            sourceDataLine.drain();
            sourceDataLine.close();
            hasStop = true;
            if (!isStop && observer != null)
            	observer.updateView();
        }
		catch (Exception e)
		{
            e.printStackTrace();
            System.exit(0);
        }
	}
	
	// 打印歌曲信息
	public String toString()
	{
		String s = "正在播放：" + songName + "--歌手：" + artist + "--专辑：" + album +
				"--年份：" + year + "--备注" + comment + "--时长" + duration;
		return s;
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedAudioFileException
	{
		Song song = new Song("F:\\BaiduMusic\\Songs\\小苹果 - 筷子兄弟.mp3");
		System.out.println(song);
		song.playSong();
		Timer timer = new Timer(true);
		timer.schedule(
				new java.util.TimerTask() { public void run()
				{ song.setPause(true); }},40*1000);
		Timer timer1 = new Timer(true);
		timer1.schedule(
				new java.util.TimerTask() { public void run()
				{ song.setPause(false); }},50*1000);
	}
}


