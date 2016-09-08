package model;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import view.IObserver;
import view.IObserver;

class MP3Song implements IPlayable, Runnable
{
	// ������Ϣ
	private String songName;
	private String artist;
	private String album;
	private String year;
	private String comment;
	private long duration;
	
	// �ļ���Ϣ
	private String filePath;
	private AudioInputStream audioInputStream;// �ļ���
    private AudioFormat audioFormat;// �ļ���ʽ
    private SourceDataLine sourceDataLine;// ����豸

    
    // ������Ϣ
	private boolean isStop;//�Ƿ��ֹͣ
	private boolean hasStop;//�Ƿ�ֹͣ������
	private String control = ""; // ֻ����Ҫһ��������ѣ��������û��ʵ������  
	private boolean isPause = false;
	
	// �۲���
	private IObserver observer;
	
	// ���캯��
	public MP3Song()
	{
		
	}
	
	public MP3Song(String filePath, IObserver o)
	{
		this.filePath = filePath;
		this.observer = o;
		reload();
	}
	
	public void reload()
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
	
	// ����
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
            // ȡ���ļ�������
            audioInputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = audioInputStream.getFormat();
            // ת��mp3�ļ�����
            if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) 
            {
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        audioFormat.getSampleRate(), 16, audioFormat
                                .getChannels(), audioFormat.getChannels() * 2,
                        audioFormat.getSampleRate(), false);
                audioInputStream = AudioSystem.getAudioInputStream(audioFormat,
                        audioInputStream);
            }
 
            // ������豸
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

	public boolean isPause()
	{
		return isPause;
	}

	public boolean isStop()
	{

		return isStop;
	}

	public boolean hasStop()
	{
		return hasStop;
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
	
	@Override
	public void run()
	{
		byte tempBuffer[] = new byte[320];
		try
		{
			int cnt;
			// ��ȡ���ݵ���������
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
                    // д�뻺������
                    sourceDataLine.write(tempBuffer, 0, cnt);
                }
            }
            // Block�ȴ���ʱ���ݱ����Ϊ��
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
	
	// ��ȡ��������
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
	
		return comment;
	}

	public long getDuration()
	{
		
		return duration;
	}

	// ע��۲���
	public void registerObserver(IObserver o)
	{
		this.observer = observer;
	}

}
