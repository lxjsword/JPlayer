package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import view.PlayerView;

public class SongMgr
{
	// 单例歌曲管理器
	private static SongMgr uniqueMgr;
	// 歌曲列表
	Map<String, SongList> listMap;
	// 配置文件路径
	private final String configPath = "songlist/config.xml"; 
	
	private SongMgr()
	{
		listMap = new HashMap<String, SongList>();
	}
	
	public Map<String, SongList> getMap()
	{
		return listMap;
	}
	
	public void addlist(String lname, SongList list)
	{
		listMap.put(lname, list);
	}
	
	public void removelist(String lname)
	{
		listMap.remove(lname);
	}
	
	// 获取单例实例
	public static SongMgr getInstance()
	{
		if (uniqueMgr  == null)
		{
			uniqueMgr = new SongMgr();
		}
		return uniqueMgr;
	}

	// 读取配置文件
	public void readConfig() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        factory.setNamespaceAware(false);  
        DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		Document doc;
		doc = builder.parse(configPath);
		XPathFactory xpfactory = XPathFactory.newInstance();
		XPath path = xpfactory.newXPath();
		NodeList nodes = (NodeList) path.evaluate("/configuration/list", doc, XPathConstants.NODESET);
		
		for (int i = 0; i < nodes.getLength(); i++)
		{ 
			NodeList lnodes = nodes.item(i).getChildNodes();
			String name = lnodes.item(0).getTextContent();
			SongList songlist = null;
			String apath = lnodes.item(1).getTextContent();
			try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(lnodes.item(1).getTextContent())))
			{
				songlist = (SongList)in.readObject();
			} 
			catch (IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			listMap.put(name, songlist);
		}

	}
	
	// 写配置文件
	public void writeConfig() throws ParserConfigurationException, FileNotFoundException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        factory.setNamespaceAware(false);  
        DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("configuration");
		doc.appendChild(root);
		for (Map.Entry<String, SongList> entry : listMap.entrySet())
		{
			Element list = doc.createElement("list");
			Element name = doc.createElement("name");
			Element path = doc.createElement("path");
			String keyname = entry.getKey();
			Text tname = doc.createTextNode(keyname);
			Text tpath = doc.createTextNode("songlist/"+keyname+".dat");
			root.appendChild(list);
			list.appendChild(name);
			list.appendChild(path);
			name.appendChild(tname);
			path.appendChild(tpath);
		}
		FileOutputStream fos = new FileOutputStream(configPath); 
        OutputStreamWriter osw = new OutputStreamWriter(fos); 
        try 
        { 
            
            Source source = new DOMSource(doc); 
            Result res = new StreamResult(osw); 
            Transformer xformer = TransformerFactory.newInstance().newTransformer(); 
            xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); 
            xformer.transform(source, res); 
        }
        catch (TransformerConfigurationException e)
        { 
               e.printStackTrace(); 
        } catch (TransformerException e)
        { 
           e.printStackTrace(); 
        } 
		writelist();
	}
	
	// 写歌曲列表
	private void writelist()
	{
		for (Map.Entry<String, SongList> entry : listMap.entrySet())
		{
			String name = entry.getKey();
			SongList list = entry.getValue();
			String path = new String("songlist/"+name+".dat");
			try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path)))
			{
				out.writeObject(list);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException
	{
		SongMgr mgr = SongMgr.getInstance();
		SongList songlist = new SongList();
		songlist.addSong("F:\\BaiduMusic\\Songs");
		mgr.addlist("list1", songlist);
		mgr.writeConfig();
//		mgr.readConfig();
//		SongList slist = mgr.getMap().get("list1");
//		System.out.println(slist.getListSize());	
	}
	
}
