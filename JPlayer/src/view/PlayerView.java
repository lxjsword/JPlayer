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
		// ���ô�������
        setLayout(new BorderLayout());
        setTitle("FJPlayer���ֲ�����");
        
        previousButton = new JButton("<<");
        startButton = new JButton("|>");
        nextButton = new JButton(">>");
        singerlabel = new JLabel("���֣�");
        songlabel = new JLabel("��������");
        addButton = new JButton("+");
        orderButton = new JButton("˳��");
        previousButton.setEnabled(false);
        // �赥�б�
        list = new List(15); 
        // ����table
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
                // ˫��ʱ����
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
        		// ˫��ʱ����
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
	
	
	
	 // ����
    private void load()
    {
    	try {
			mgr.readConfig();
		} catch (XPathExpressionException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
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
    		orderButton.setLabel("˳��");
    	else
    		orderButton.setLabel("���");
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
    			songlabel.setText("��������" + songlist.getCurrentSong().getSongName());
    			singerlabel.setText("���֣�" + songlist.getCurrentSong().getArtist());
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
