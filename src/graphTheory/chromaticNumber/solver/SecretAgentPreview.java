package graphTheory.chromaticNumber.solver;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import graphTheory.chromaticNumber.assets.GravityWell;
import graphTheory.chromaticNumber.assets.Universe;
import graphTheory.chromaticNumber.loader.Driver;

@SuppressWarnings("serial")
public class SecretAgentPreview extends Canvas implements Runnable {

	Thread agentsPreview;

	static boolean running;
	public static boolean drawGradientMap = false;

	double scale;
	int xOffset = 40;
	int yOffset = 15;

	static int numBars;
	static int barTimeout = 3000; // 3 seconds
	static long lastBarTime, secLastTap;
	static double avgTime = 0;
	static double avgMillis;

	public SecretAgentPreview() {

		setSize(800, 800);
		setMaximumSize(new Dimension(800, 800));
		setMinimumSize(getMaximumSize());
		setPreferredSize(getMaximumSize());

		Driver.trace("Starting preview window thread.");
	}
	
	public void start() {
		// start thread
		agentsPreview = new Thread(this);
		agentsPreview.setName("Agents Preview Updater");
		running = true;
		agentsPreview.start();

		invalidate();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;// per second
		double timePerTick = 1000000000 / amountOfTicks;
		double delta = 0;

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//		}

		while (getParent() == null);
		
		BufferStrategy bs = this.getBufferStrategy();
		
		if (bs == null) {
			this.createBufferStrategy(3);
			bs = this.getBufferStrategy();
		}

		while (running) {

			scale = 800 / Universe.getBounds(0);

			long now = System.nanoTime();
			// System.out.println((double) ((now-lastTime)/timePerTick));
			delta += ((now - lastTime) / timePerTick);
			// System.out.println(delta);
			lastTime = now;

			while (delta >= 1) {

				delta--;
			}

			render(bs);
			System.out.print("");
		}

		bs.dispose();
	}

	public void render(BufferStrategy bs) {

		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setFont(new Font("Serif", Font.PLAIN, (int) (0.01 * scale * Universe.getBounds(0))));
		// System.out.println("setting font size to be "+(int)
		// (0.01*scale*Universe.getBounds(0)));
		
		if (!drawGradientMap) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, (int) getBounds().getWidth(), (int) getBounds().getHeight());
		} else {
			g2d.drawImage(SecretAgents.getGradientMap(), 0, 0, 
					(int) (SecretAgents.getGradientMap().getWidth()*scale),
					(int) (SecretAgents.getGradientMap().getHeight()*scale), null);
		}

		try {
			// Draw wells
			for (int i = 0; i < Universe.getWells().size(); i++) {
				g.setColor(Universe.getWells().get(i).getColour());
				g2d.drawOval(
						(int) ((Universe.getWells().get(i).getLocation()[0]
								- (int) GravityWell.getRadius()) * scale),
						(int) ((Universe.getWells().get(i).getLocation()[1]
								- (int) GravityWell.getRadius()) * scale),
						(int) ((GravityWell.getRadius() * 2) * scale),
						(int) ((GravityWell.getRadius() * 2) * scale));

				if (Universe.getWells().get(i).getCapturedAgents().size() > 0) {
					g2d.drawString("" + Universe.getWells().get(i).getCapturedAgents().size(),
							(int) (Universe.getWells().get(i).getLocation()[0] * scale),
							(int) (Universe.getWells().get(i).getLocation()[1] * scale));

					g2d.drawString(
							"" + Universe.getWells().get(i).getMinComfort() + ", "
									+ Universe.getWells().get(i).getMaxComfort(),
							(int) (Universe.getWells().get(i).getLocation()[0] * scale),
							(int) (Universe.getWells().get(i).getLocation()[1] * scale) + 8);
				}

			}

			// Draw agents
			int spotWidth = 4;
			for (int i = 0; i < Universe.getAgents().size(); i++) {
				if (!Universe.getAgents().get(i).isCaptured()) {
					
					
					for (int wellNum = 0; wellNum < Universe.getWells().size(); wellNum++) {
						//if ((Universe.getIsTorricDistShorter(Universe.getWells().get(wellNum).getLocation(), Universe.getAgents().get(i).getLocationArray(), 0) ||
						//		Universe.getIsTorricDistShorter(Universe.getWells().get(wellNum).getLocation(), Universe.getAgents().get(i).getLocationArray(), 1))) {
						//	g.setColor(Color.blue);
						//} else {
//							g.setColor(Color.green);
//						//}
//						g2d.drawLine((int) (Universe.getAgents().get(i).getLocation(0) * scale),
//								(int) (Universe.getAgents().get(i).getLocation(1) * scale),
//								(int) ((Universe.getAgents().get(i).getLocation(0) * scale) + 
//										2 * Universe.getAgents().get(i).getForce(wellNum, 0)),
//								(int) ((Universe.getAgents().get(i).getLocation(1) * scale) + 
//										2 * Universe.getAgents().get(i).getForce(wellNum, 1)));
//						//
					}
					
					g.setColor(Color.RED);
					g2d.fillRect((int) ((Universe.getAgents().get(i).getLocation(0) - spotWidth / 2) * scale),
							(int) ((Universe.getAgents().get(i).getLocation(1) - spotWidth / 2) * scale),
							(int) (spotWidth * scale), (int) (spotWidth * scale));
					g2d.drawString("" + Universe.getAgents().get(i).getVertexAssociation(),
							(int) (Universe.getAgents().get(i).getLocation(0) * scale),
							(int) (Universe.getAgents().get(i).getLocation(1) * scale));
				}
			}

			// draw HUD
			g2d.setFont(new Font("Serif", Font.PLAIN, 8));
			g.setColor(Color.WHITE);
			g2d.drawString("Agents: " + SecretAgents.getNumAgents(), 2, 10);
			g2d.drawString("Colours: " + SecretAgents.getNumColours(), 2, 20);
			g2d.drawString("Internal Iteration: " + SecretAgents.getCurrentInternalIterationNum(), 2, 30);
			g2d.drawString("Current Trial: " + SecretAgents.getCurrentIterationNum(), 2, 40);
			if (drawGradientMap)
				g2d.drawString("Num colours to map: "+SecretAgents.getNumMapLocsRemaining(), 2, 50);
		} catch (IndexOutOfBoundsException e) {
		} catch (NullPointerException e) {
		}

		g.dispose();
		g2d.dispose();
		
		bs.show();
	}

	public void calcTime() {
		if (System.currentTimeMillis() - lastBarTime > barTimeout) {
			lastBarTime = System.currentTimeMillis();
			numBars = 1;
		} else if (numBars > 600) {
			numBars = 600;
		}
		avgTime = ((numBars - 1) * avgTime + (System.currentTimeMillis() - lastBarTime)) / numBars;
		lastBarTime = System.currentTimeMillis();
		numBars++;
	}

	public static void setRunning(boolean newRunning) {
		running = newRunning;
	}
}
