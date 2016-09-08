package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import model.Song;
import model.SongInterface;
import model.SongList;
import model.SongMgr;

public class PlayerView  extends JFrame implements ObserverInterface
{
	JButton previousButton;
	JButton startButton;
	JButton nextButton;
	JLabel singerlabel;
	JLabel songlabel;
	JTable table;
	TableModel tablemodel;
	List list;
	SongList songlist;
	JButton addButton;
	JButton orderButton;
	SongMgr mgr = SongMgr.getInstance();
	
	public PlayerView()
	{
		// 设置窗体属性
        setLayout(new BorderLayout());
        setTitle("FJPlayer音乐播放器");
        
        previousButton = new JButton("<<");
        startButton = new JButton("|>");
        nextButton = new JButton(">>");
        singerlabel = new JLabel("歌手：");
        songlabel = new JLabel("歌曲名：");
        addButton = new JButton("+");
        orderButton = new JButton("顺序");
        previousButton.setEnabled(false);
        // 歌单列表
        list = new List(15); 
        // 歌曲table
        load();
        table = new JTable(tablemodel);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        JScrollPane spanel = new JScrollPane(table);


        list.addMouseListener(new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {
                // 双击时处理
                if (e.getClickCount() == 2) 
                {
                	SongList songlist = mgr.getMap().get(list.getSelectedItem());
                	tablemodel = new SongTableModel(songlist);
                }
            }
        });
        table.addMouseListener(new MouseAdapter()
        {
        	public void mouseClicked(MouseEvent e) 
            {
        		// 双击时处理
                if (e.getClickCount() == 2) 
                {
                	if (songlist != null)
                	{
	                	if (!songlist.hasStop())
	                		songlist.stopSong();
	                    songlist.playbyindex(table.getSelectedRow());
                	}
                }
            }
        });
        
        previousButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (songlist != null)
            	{
	                if (!songlist.hasStop())
	                	songlist.stopSong();
	                songlist.playprevious();
            	}
            }
        });
        
        startButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (songlist != null)
            	{
	            	if (songlist.isPause())
	            	{
	            		songlist.setPause(false);
	            	}
	            	else
	            	{
	            		songlist.setPause(true);
	            	}
            	}
            }
        });
        
        nextButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (songlist != null)
            	{
	                if (!songlist.hasStop())
	                	songlist.stopSong();
	                songlist.playnext();
            	}
            }
        });
        
        addButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                
            }
        });
        
        orderButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (songlist != null)
            	{
            		songlist.setOrder();
            	}
            }
        });

        
        add(list, "West");
        add(spanel, "Center");
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel uppanel = new JPanel(new GridLayout(1, 2));
        uppanel.add(songlabel);
        uppanel.add(singerlabel);
        JPanel downpanel = new JPanel(new GridLayout(1, 2));
        JPanel leftpanel = new JPanel();
        leftpanel.add(previousButton);
        leftpanel.add(startButton);
        leftpanel.add(nextButton);
        JPanel rightpanel = new JPanel();
        rightpanel.add(addButton);
        rightpanel.add(orderButton);
        downpanel.add(leftpanel);
        downpanel.add(rightpanel);
        panel.add(uppanel);
        panel.add(downpanel);
        add(panel, "South");
        this.setBounds(0, 0, 350, 600);
        this.setIconImage(Toolkit.getDefaultToolkit().createImage("icon.png"));  
	}
	
	
	
	 // 加载
    private void load()
    {
    	try {
			mgr.readConfig();
		} catch (XPathExpressionException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
    	list.removeAll();
    	Map<String, SongList> map = mgr.getMap();
    	for (Map.Entry<String, SongList> entry : map.entrySet())
		{
    		list.add(entry.getKey());
		}
    	
    	songlist = map.get("list1");
    	songlist.registerObserver(this);
    	tablemodel = new SongTableModel(songlist);
    }
    
    public void updateView()
    {
    	if (songlist.getOrder())
    		orderButton.setLabel("顺序");
    	else
    		orderButton.setLabel("随机");
    	if (songlist.isStop())
    	{
    		startButton.setLabel("|>");
    	}
    	else
    	{
    		if (!songlist.hasStop())
    		{
    			startButton.setLabel("||");
    			int index = songlist.getcurrentindex();
    			if (index == 0)
    				previousButton.setEnabled(false);
    			else
    				previousButton.setEnabled(true);
    			if (index == songlist.getSongList().size() - 1)
    				nextButton.setEnabled(false);
    			else
    				nextButton.setEnabled(true);
    			table.setRowSelectionInterval(index,index);
    			songlabel.setText("歌曲名：" + songlist.getCurrentSong().getSongName());
    			singerlabel.setText("歌手：" + songlist.getCurrentSong().getArtist());
    		}
    		else
    		{
    			songlist.playnext();
    		}
    	}
    }
    
    
    public static void main(String args[]) 
    {
    	EventQueue.invokeLater(new Runnable()
    	{
    		public void run()
    		{
    			PlayerView playview = new PlayerView();
    			playview.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    			playview.setVisible(true);
    		}    		
    	});  	 	
    }
}
