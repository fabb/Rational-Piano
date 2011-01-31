package rationalpiano.test;

import org.junit.Test;

import processing.core.PApplet;
import rationalpiano.graphic.GraphicControls;

public class GraphicControlsTest extends PApplet {

	private static final long serialVersionUID = 1L;
	private GraphicControls graphiccontrols;
	private int notecount = 25;
	private int notestart = 20;
	private double w = 0;
	private double winc = 0.01;

	@Test
	public void testGraphicControls() {
		PApplet.main(new String[] { "RationalPiano.Test.GraphicControlsTest" });
		while(true);
	}

	@Override
	public void setup() {
		boolean fullscreen = false;
		double vertical_scaling = 1;
		int width = 1024/88 * notecount;
		//PApplet.println(width);
		int height = 300;
		float framerate = 30;
		double lineBend = 0.1;
		int backgroundColorHue = 0;
		int backgroundColorSaturation = 0;
		int backgroundColorBrightness = 0;
		int lineColorHueInactive = 180;
		int lineColorHueActive = 80;
		int lineColorSaturation = 255;
		int lineColorBrightness = 255;
		
		graphiccontrols = new GraphicControls(this, null, fullscreen, vertical_scaling, width, height, framerate, notecount, notestart, lineBend, backgroundColorHue, backgroundColorSaturation, backgroundColorBrightness, lineColorHueInactive, lineColorHueActive, lineColorSaturation, lineColorBrightness);
	}
	
	@Override
	public void draw() {
		for(int i = notestart; i<=notestart+notecount; i++){
			try{
				if (i%2 == 0){
					graphiccontrols.getGraphicVisualizationElementArray().getElement(i).setVolume(1/(1+100*w));
				}else{
					graphiccontrols.getGraphicVisualizationElementArray().getElement(i).setVolume(1/(1+100*(1-w)));
				}
				
				if (i%3 == 0){
					graphiccontrols.getGraphicVisualizationElementArray().getElement(i).setActive(true);
				}
			}catch(NullPointerException e){
			}
		}
			
		w  = w + winc;
		if(w <= 0 || w >= 1) winc=-winc;
		graphiccontrols.draw();
	}
	
	@Override
	public void stop() {
		super.stop();
	}
}
