/**
 * 
 */
package br.unicamp.cogsys.ccbca.applications.sumooutputdashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author andre
 *
 */
public class LeitorSumoSummaryOutput implements Runnable
{
	private String sumoSummaryOutputFilePath;
	private SumoSummaryOutputChanged summaryOutputChangedListener;
	
	private Double clockTime = 0.0d;
	private Double meanTravelTime = 0.0d;
	
	/**
	 * @param sumoSummaryOutputFilePath
	 */
	public LeitorSumoSummaryOutput(String sumoSummaryOutputFilePath) 
	{
		super();
		this.sumoSummaryOutputFilePath = sumoSummaryOutputFilePath;
	}

	@Override
	public void run() 
	{			
		while(true)
		{
			try 
			{
				File file = new File(sumoSummaryOutputFilePath);
				if(file.exists())
				{
					File copy = new File(sumoSummaryOutputFilePath.substring(0, sumoSummaryOutputFilePath.lastIndexOf("/"))+"/output_sumo_copy.xml");
					if(copy.exists())
						copy.delete();
					copyFile(file, copy);
					
					RandomAccessFile f = new RandomAccessFile(sumoSummaryOutputFilePath.substring(0, sumoSummaryOutputFilePath.lastIndexOf("/"))+"/output_sumo_copy.xml", "rw");
					long length = f.length() - 1;
					byte b;
					do 
					{                     
					  length -= 1;
					  f.seek(length);
					  b = f.readByte();
					} while(b != 10);
					f.setLength(length+1);	
					f.writeBytes("</summary>");
					f.close();
					
					DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = dBuilder.parse(copy);
					
					if (doc.hasChildNodes()) 
					{
						printNote(doc.getChildNodes());
					}
				}
			} catch (Exception e) 
			{
				System.out.println(e.getMessage());				
			}

			try 
			{
				Thread.sleep(1000);
			} catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
    public void copyFile(File source, File destination) throws IOException 
    {
    	FileInputStream input = new FileInputStream(source);
        OutputStream output = null;
        try {
            output = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int bytesRead = input.read(buffer);
            while (bytesRead >= 0) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } catch (Exception e) {
            //
        } finally {
            input.close();
            output.close();
        }
        input = null;
        output = null;
    }
	
	private void printNote(NodeList nodeList) 
	{

		for (int count = 0; count < nodeList.getLength(); count++) 
		{

			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				if (tempNode.hasAttributes()) 
				{
					NamedNodeMap nodeMap = tempNode.getAttributes();
					
					Node nodeTime = nodeMap.item(7);
					if(nodeTime.getNodeName().equalsIgnoreCase("time"))
					{
						Double timeNow = Double.valueOf(nodeTime.getNodeValue());
						if(timeNow > clockTime)
						{
							clockTime = timeNow;
							Node nodeMeanTravelTime = nodeMap.item(4);
							meanTravelTime = Double.valueOf(nodeMeanTravelTime.getNodeValue());
							summaryOutputChangedListener.changedSumoSummaryOutput(meanTravelTime);
						}						
					}				
				}

				if (tempNode.hasChildNodes()) 
				{
					// loop again if has child nodes
					printNote(tempNode.getChildNodes());
				}
			}
		}
	}
	
	public interface SumoSummaryOutputChanged
	{
		public void changedSumoSummaryOutput(Double newValue);
	}

	/**
	 * @return the summaryOutputChangedListener
	 */
	public synchronized SumoSummaryOutputChanged getSummaryOutputChangedListener() 
	{
		return summaryOutputChangedListener;
	}

	/**
	 * @param summaryOutputChangedListener the summaryOutputChangedListener to set
	 */
	public synchronized void setSummaryOutputChangedListener(SumoSummaryOutputChanged summaryOutputChangedListener) 
	{
		this.summaryOutputChangedListener = summaryOutputChangedListener;
	}

}
