package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import model.SongList;

import control.PlayControl;
import model.IPlayable;


public class PlayerView extends JFrame implements IObserver
{
	JButton previousButton;
	JButton startButton;
	JButton nextButton;
	JLabel singerlabel;
	JLabel songlabel;
	JTable table;
	TableModel tablemodel;
	JButton addButton;
	JButton orderButton;

	PlayControl control;
	
	public PlayerView()
	{
		// 设置窗体属性
        setLayout(new BorderLayout());
        setTitle("JPlayer音乐播放器");
        
        previousButton = new JButton("<<");
        startButton = new JButton("|>");
        nextButton = new JButton(">>");
        singerlabel = new JLabel("歌手：");
        songlabel = new JLabel("歌曲名：");
        addButton = new JButton("+");
        orderButton = new JButton("顺序");
        previousButton.setEnabled(false);
        table = new JTable(); 
        JScrollPane spanel = new JScrollPane(table);
        
        table.addMouseListener(new MouseAdapter()
        {
        	public void mouseClicked(MouseEvent e) 
            {
        		// 双击时处理
                if (e.getClickCount() == 2) 
                {
                	if (control != null)
                	{
	                	if (!control.hasStop())
	                		control.stopSong();
	                	control.playbyindex(table.getSelectedRow());
                	}
                }
            }
        });
        
        previousButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (control != null)
            	{
	                if (!control.hasStop())
	                	control.stopSong();
	                control.playprevious();
            	}
            }
        });
        
        nextButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (control != null)
            	{
	                if (!control.hasStop())
	                	control.stopSong();
	                control.playnext();
            	}
            }
        });
        
        startButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (control != null)
            	{
	            	if (control.isPause())
	            	{
	            		control.setPause(false);
	            	}
	            	else
	            	{
	            		control.setPause(true);
	            	}
            	}
            }
        });
        
        addButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                open();
            }
        });
        
        orderButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
            	if (control != null)
            	{
            		control.setOrder();
            	}
            }
        });
       
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
        this.pack();
        this.setIconImage(Toolkit.getDefaultToolkit().createImage("icon.png"));  
	}
	
	 // 打开
    private void open() 
    {
        FileDialog dialog = new FileDialog(this, "Open", 0);
        dialog.setVisible(true);
        String filepath = dialog.getDirectory();
        if (filepath != null) 
        {
        	control = new PlayControl(filepath, this);
        	tablemodel = new SongTableModel(control.getPlaylist());
        	table.setModel(tablemodel);
        	table.getColumnModel().getColumn(0).setPreferredWidth(150);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            this.invalidate();
        }
    }
    
    public void updateView()
    {
    	if (control.getOrder())
    		orderButton.setLabel("顺序");
    	else
    		orderButton.setLabel("随机");
    	if (control.isStop() || control.isPause())
    	{
    		startButton.setLabel("|>");
    	}
    	else
    	{
    		if (!control.hasStop())
    		{
    			if (!control.isPause())
    				startButton.setLabel("||");
    			int index = control.getcurrentindex();
    			if (index == 0)
    				previousButton.setEnabled(false);
    			else
    				previousButton.setEnabled(true);
    			if (index == control.getSongList().size() - 1)
    				nextButton.setEnabled(false);
    			else
    				nextButton.setEnabled(true);
    			table.setRowSelectionInterval(index,index);
    			songlabel.setText("歌曲名：" + control.getCurrentSong().getSongName());
    			singerlabel.setText("歌手：" + control.getCurrentSong().getArtist());
    		}
    		else
    		{
    			control.playnext();
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
