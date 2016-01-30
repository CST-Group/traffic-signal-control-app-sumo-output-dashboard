/**
 * 
 */
package br.unicamp.cogsys.ccbca.applications.sumooutputdashboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;

import br.unicamp.cogsys.ccbca.applications.sumooutputdashboard.LeitorSumoSummaryOutput.SumoSummaryOutputChanged;

/**
 * @author andre
 *
 */
public class SumoOutputDashboard extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SumoOutputDashboard(String title,String sumoSummaryOutputFilePath)
	{
		super(title);
		setDefaultCloseOperation(3);
        setContentPane(createGraphPanel(sumoSummaryOutputFilePath));
	}
	
	public JPanel createGraphPanel(String sumoSummaryOutputFilePath)
	{
        return new GraphPanel(sumoSummaryOutputFilePath);
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{	
		if(args.length != 1)
		{
			System.out.println("");
			System.out.println("usage SumoOutputDashboard <P1>");
			System.out.println("P1: path of the sumo output file");
			return;
		}
		
		SumoOutputDashboard sod = new SumoOutputDashboard("Sumo Output Dashboard",args[0]);
        sod.pack();
        sod.setVisible(true);

	}
	
	private class GraphPanel extends JPanel implements SumoSummaryOutputChanged
	{
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
        private DefaultValueDataset dataset = new DefaultValueDataset(0.0D);
        private LeitorSumoSummaryOutput leitorSumoSummaryOutput;
        private Double valorMaximo = 100.0d;
        private StandardDialScale localStandardDialScale;
        private StandardDialRange localStandardDialRange1;
        private StandardDialRange localStandardDialRange2;
        private StandardDialRange localStandardDialRange3;
        private JFreeChart localJFreeChart;
        private ChartPanel localChartPanel;

        public GraphPanel(String sumoSummaryOutputFilePath) 
        {
            super();            
            localJFreeChart = createStandardDialChart("Sumo Output Dashboard","Mean Travel Time", this.dataset,0.0D, valorMaximo, valorMaximo/10.0D, (int)(valorMaximo/20));
                      
            localChartPanel = new ChartPanel(localJFreeChart);
            localChartPanel.setPreferredSize(new Dimension(400, 400));
            add(localChartPanel);
            
            
            leitorSumoSummaryOutput = new LeitorSumoSummaryOutput(sumoSummaryOutputFilePath);
            leitorSumoSummaryOutput.setSummaryOutputChangedListener(this);
            Thread t = new Thread(leitorSumoSummaryOutput);
            t.start();
            
        }

        public JFreeChart createStandardDialChart(String title,String variableNumber, ValueDataset valueDataset, double begin, double end, double step,int division)
        {
            DialPlot localDialPlot = new DialPlot();
            localDialPlot.setDataset(valueDataset);
            localDialPlot.setDialFrame(new StandardDialFrame());
            localDialPlot.setBackground(new DialBackground());
            DialTextAnnotation localDialTextAnnotation = new DialTextAnnotation(variableNumber);
            localDialTextAnnotation.setFont(new Font("Dialog", 1, 14));
            localDialTextAnnotation.setRadius(0.7D);
            localDialPlot.addLayer(localDialTextAnnotation);
            DialValueIndicator localDialValueIndicator = new DialValueIndicator(0);
            localDialPlot.addLayer(localDialValueIndicator);
            localStandardDialScale = new StandardDialScale(begin, end, -120.0D, -300.0D, step, division);
            localStandardDialScale.setTickRadius(0.88D);
            localStandardDialScale.setTickLabelOffset(0.15D);
            localStandardDialScale.setTickLabelFont(new Font("Dialog", 0, 14));
            localDialPlot.addScale(0, localStandardDialScale);
            localDialPlot.addPointer(new DialPointer.Pin());
            DialCap localDialCap = new DialCap();
            localDialPlot.setCap(localDialCap);
            
            localStandardDialRange1 = new StandardDialRange(end*0.8d, end, Color.red);
            localStandardDialRange1.setInnerRadius(0.52D);
            localStandardDialRange1.setOuterRadius(0.55D);
            localDialPlot.addLayer(localStandardDialRange1);
            localStandardDialRange2 = new StandardDialRange(end*0.4d, end*0.8d, Color.orange);
            localStandardDialRange2.setInnerRadius(0.52D);
            localStandardDialRange2.setOuterRadius(0.55D);
            localDialPlot.addLayer(localStandardDialRange2);
            localStandardDialRange3 = new StandardDialRange(0.0D, end*0.4d, Color.green);
            localStandardDialRange3.setInnerRadius(0.52D);
            localStandardDialRange3.setOuterRadius(0.55D);
            localDialPlot.addLayer(localStandardDialRange3);
            GradientPaint localGradientPaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170,220));
            DialBackground localDialBackground = new DialBackground(localGradientPaint);
            localDialBackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
            localDialPlot.setBackground(localDialBackground);
            localDialPlot.removePointer(0);
            DialPointer.Pointer localPointer = new DialPointer.Pointer();
            localPointer.setFillPaint(Color.yellow);
            localDialPlot.addPointer(localPointer);
            
            return new JFreeChart(title, localDialPlot);
        }

		@Override
		public void changedSumoSummaryOutput(Double newValue) 
		{
			this.dataset.setValue(newValue);
			
			if(newValue > valorMaximo)
			{
				valorMaximo = newValue;
				
				localStandardDialScale.setUpperBound(newValue);
		        localStandardDialRange1.setBounds(newValue*0.8d, newValue);
		        localStandardDialRange2.setBounds(newValue*0.4d, newValue*0.8d);
		        localStandardDialRange3.setBounds(0.0d, newValue*0.4d);
				
			}
			
		}
    }
}
