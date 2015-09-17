package graphTheory.chromaticNumber.solver;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferStrategy;

import graphTheory.chromaticNumber.assets.Universe;



@SuppressWarnings("serial")
public class SecretAgentPreview extends Canvas implements Runnable{

	Thread agentsPreview;
	
	static boolean running;


	double scale;
	int xOffset = 40;
	int yOffset = 15;


	static int numBars;
	static int barTimeout = 3000; // 3 seconds
	static long lastBarTime, secLastTap;
	static double avgTime = 0;
	static double avgMillis;



	public SecretAgentPreview() {
		
		setSize(750,750);
		setMaximumSize(new Dimension(750,750));
		setMinimumSize(getMaximumSize());
		setPreferredSize(getMaximumSize());

		System.out.println("Starting preview window thread.");

		//start thread
		agentsPreview = new Thread(this);
		agentsPreview.setName("Agents Preview Updater");
		running = true;
		agentsPreview.start();

		invalidate();
	}


	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;//per second
		double timePerTick = 1000000000/amountOfTicks;
		double delta = 0;

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		scale = 750/Universe.getBounds(0);

		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}

		while (running) {

			long now = System.nanoTime();
			//System.out.println((double) ((now-lastTime)/timePerTick));
			delta += ((now-lastTime)/timePerTick);
			//System.out.println(delta);
			lastTime = now;

			while(delta>=1) {
				
				
				delta--;
			}
			
			render(bs);
		}

		bs.dispose();
	}

	public void render(BufferStrategy bs) {

		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK); 
		g2d.fillRect(0, 0, (int) getBounds().getWidth(), (int) getBounds().getHeight());

		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
		// TODO draw here
		for (int i = 0; i < Universe.getWells().size(); i++) {
			g.setColor(Universe.getWells().get(i).getColour());
			g2d.drawOval((int) ((Universe.getWells().get(i).getLocation()[0]-(int) Universe.getWells().get(i).getRadius())*scale),
					(int) ((Universe.getWells().get(i).getLocation()[1]-(int) Universe.getWells().get(i).getRadius())*scale), 
					(int) ((Universe.getWells().get(i).getRadius()*2)*scale), 
					(int) ((Universe.getWells().get(i).getRadius()*2)*scale));
		}
		
		for (int i = 0; i < Universe.getAgents().size(); i++) {
			if (Universe.getAgents().get(i).isCaptured()) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.RED);
			}

			int spotWidth = 4;
			
			g2d.fillRect((int) ((Universe.getAgents().get(i).getLocation(0)-spotWidth/2)*scale), 
					(int) ((Universe.getAgents().get(i).getLocation(1) -spotWidth/2)*scale), 
					(int) (spotWidth*scale), (int) (spotWidth*scale));
			
			g2d.drawString(""+Universe.getAgents().get(i).getVertexAssociation(), 
					(int) (Universe.getAgents().get(i).getLocation(0)*scale), 
					(int) (Universe.getAgents().get(i).getLocation(1)*scale)); 
			
			//System.out.println("["+(int) Universe.getAgents().get(i).getLocation(0)+", "
			//		+ ""+(int) Universe.getAgents().get(i).getLocation(1)+"]");
		}
		
		

		g.dispose();
		g2d.dispose();

		bs.show();
	}


	public void calcTime() {
		if (System.currentTimeMillis()-lastBarTime > barTimeout) {
			lastBarTime = System.currentTimeMillis();
			numBars = 1;
		} else if (numBars > 600) {
			numBars = 600;
		}
		avgTime = ((numBars-1) * avgTime + (System.currentTimeMillis() - lastBarTime))/numBars;
		lastBarTime = System.currentTimeMillis();
		numBars++;
	}

	


	public static void setRunning(boolean newRunning) {
		running = newRunning;
	}
}
