package RationalPiano.Persistence;

//import java.beans.PropertyEditor; //this would be an alternative to the reflection way used in this class

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

import RationalPiano.NoteOut.INoteOutput.outputModes;
import RationalPiano.Persistence.Annotations.FieldDescription;
import RationalPiano.Persistence.Annotations.FieldDoubleMinMax;
import RationalPiano.Persistence.Annotations.FieldFloatMinMax;
import RationalPiano.Persistence.Annotations.FieldIntegerMinMax;

/**
 * Holds all configuration parameters needed at the initialization phase.
 * Provides methods to read and write that configuration from/to a human readable text file.
 * 
 * @author Fabian Ehrentraud
 * @date 2011-01-29
 * @version 1.051
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class ConfigurationData {
	
	//configuration data - no native types!! - must have default values!
	@FieldDescription(description="true = launch in fullscreen; false = launch in window; In full screen mode, the window width and height settings are ignored and the screen resolution is used instead")
	public Boolean fullscreen = /**/true/*/false/**/;
	
	@FieldDescription(description="Use the full width, but only the given fraction of the height of the full screen area, aligned to the bottom; Only taken account for in fullscreen mode")
	@FieldDoubleMinMax(min=0, max=1)
	public Double vertical_scaling = /**/1.0/*0.2/**/;
	
	@FieldDescription(description="width of the applet window (only in windowed mode), values higher than the screen width get cropped")
	@FieldIntegerMinMax(min=100, max=10000)
	public Integer width = 1000;
	
	@FieldDescription(description="height of the applet window (only in windowed mode), values higher than the screen height get cropped")
	@FieldIntegerMinMax(min=100, max=10000)
	public Integer height = 500;
	
	@FieldDescription(description="framerate in frames per second")
	@FieldFloatMinMax(min=1, max=200)
	public Float framerate = Float.valueOf(60);
	
	@FieldDescription(description="count of notes to display; the maximum MIDI note number is 127, so notestart + notecount - 1 should be smaller than 128")
	@FieldIntegerMinMax(min=1, max=128)
	public Integer notecount = 88;
	
	@FieldDescription(description="midi note number of the first note, 60 = C4; the maximum MIDI note number is 127, so notestart + notecount - 1 should be smaller than 128")
	@FieldIntegerMinMax(min=0, max=127)
	public Integer notestart = 21;
	
	@FieldDescription(description="nonlinear distortion of the line width translation, 1 means no distortion, smaller values mean stronger distortion where the lines have a tendency to be more wide")
	@FieldDoubleMinMax(min=0, max=1) // > 1 also possible, but not as useful
	public Double lineBend = 0.25;
	
	@FieldDescription(description="one of several modes to select where to send the note messages to (MIDI and OSC)")
	public outputModes outputMode = outputModes.NO_OUTPUT;
	
	@FieldDescription(description="UDP port to send OSC voice messages to")
	@FieldIntegerMinMax(min=0, max=65535)
	public Integer oscport = 12000;
	
	@FieldDescription(description="a part of the wanted midi output device's name where to send the note messages to")
	public String midiOutputDevice = "";
	
	@FieldDescription(description="a part of the wanted midi input device's name where to get note messages from")
	public String midiInputDevice = "";
	
	@FieldDescription(description="midi channel that should get used for the note messages")
	@FieldIntegerMinMax(min=0, max=15)
	public Integer midiChannel = 0;
	
	@FieldDescription(description="attack time of new voices in seconds")
	@FieldDoubleMinMax(min=0, max=60)
	public Double attack = 0.15;
	
	@FieldDescription(description="decay time of held voices in seconds")
	@FieldDoubleMinMax(min=0, max=60)
	public Double decay = 4.0;
	
	@FieldDescription(description="sustain level (where the note visually stays after attack + decay) as a fraction of the initial velocity")
	@FieldDoubleMinMax(min=0, max=1)
	public Double sustain = 0.0;
	
	@FieldDescription(description="release time of released voices in seconds")
	@FieldDoubleMinMax(min=0, max=60)
	public Double release = 1.;
	
	@FieldDescription(description="true = hold the sustain after attack + decay; false = no sustain, release starts right after attack phase (pluck mode)")
	public Boolean holdSustain = true;
	
	@FieldDescription(description="maximum dissonance to calculate a fraction for, higher values take more time to initialize")
	@FieldIntegerMinMax(min=1, max=Integer.MAX_VALUE)
	public Integer maxfrac = 16*16;
	
	@FieldDescription(description="width of the bell curve which gets drawn around each fraction when calculating dissonance")
	@FieldDoubleMinMax(min=0.01, max=1)
	public Double bellWidth = 0.25;
	
	@FieldDescription(description="port to listen at for TUIO cursor messages")
	@FieldIntegerMinMax(min=0, max=65535)
	public Integer tuioPort = 3333; //3333 is the standard port for tuio
	
	@FieldDescription(description="color hue of the background")
	@FieldIntegerMinMax(min=0, max=255)
	public Integer backgroundColorHue = 0;
	
	@FieldDescription(description="color saturation of the background")
	@FieldIntegerMinMax(min=0, max=255)
	public Integer backgroundColorSaturation = 0;
	
	@FieldDescription(description="color brightness of the background")
	@FieldIntegerMinMax(min=0, max=255)
	public Integer backgroundColorBrightness = 0;
	
	@FieldDescription(description="color hue of an inactive line")
	@FieldIntegerMinMax(min=0, max=255)
	public Integer lineColorHueInactive = 180;
	
	@FieldDescription(description="color hue of an active line")
	@FieldIntegerMinMax(min=0, max=255)
	public Integer lineColorHueActive = (lineColorHueInactive-100+256)%256;
	
	@FieldDescription(description="color saturation of a line")
	@FieldIntegerMinMax(min=0, max=255)
	public Integer lineColorSaturation = 255;
	
	@FieldDescription(description="color brightness of a line")
	@FieldIntegerMinMax(min=0, max=255)
	public Integer lineColorBrightness = 255;

	
	private String lastFileName = "";


	private static final Logger logger = Logger.getLogger(ConfigurationData.class.getName());

	/**
	 * Creates a new ConfigurationData with default settings.
	 */
	public ConfigurationData() {
		//nothing to do
	}
	
	/**
	 * Creates a new ConfigurationData and trys to load settings from the specified file; non-readable or missing settings are being changed to default ones.
	 * If the file doesn't exist, creates a configuration file with standard settings.
	 * @param fileName Filename, no path necessary if it's in the same folder as the executable.
	 */
	public ConfigurationData(String fileName){
		try {
			loadData(fileName);
		} catch (FileNotFoundException e) {
			logger.warning("Couldn't load file '" + fileName + "', error: " + e.toString() + "\n" + "Using standard configuration");
		}

		//save configuration again in case there were some settings out of range or missing
		try {
			this.saveData(fileName);
		} catch (IOException ioe) {
			logger.severe("Couldn't write to file '" + fileName + "', error: " + ioe.toString());
			//don't throw new Exception as this object wouldn't get created then
		}
	}
	
	/**
	 * Saves configuration data to the last used file, only valid after saveData(String fileName) has been called before.
	 * @throws IOException Thrown if either subfolders couldn't get created or writing to the file failed.
	 */
	public void saveData() throws IOException {
		saveData(lastFileName);
	}

	/**
	 * Saves configuration data to the specified file.
	 * @param fileName Filename, no path necessary if it's in the same folder as the executable. Nonexistant subfolders get created automatically.
	 * @throws IOException Thrown if either subfolders couldn't get created or writing to the file failed.
	 */
	public void saveData(String fileName) throws IOException {
		logger.info("Trying to save configuration data to file '" + fileName + "'");
		FileWriter fw;
		
		//if path doesn't exist yet, create it
		try{
			new File(fileName).getAbsoluteFile().getParentFile().mkdirs();
		}catch(NullPointerException e){
			throw new IOException("Couldn't create necessary folders for file '" + fileName + "'.");
		}
		
		fw = new FileWriter(fileName);
		
		BufferedWriter out = new BufferedWriter(fw);
		
		Field[] fields = this.getClass().getFields();
		
		for(Field f : fields){
			try {
				String comment = "#";
				
				if(f.getType().isEnum()){
					comment += "Enum ";
				}
					
				comment += f.getType().getSimpleName();
				
				if(f.getAnnotation(FieldDescription.class) != null){
					comment += ", " + f.getAnnotation(FieldDescription.class).description();
				}
				
				if(f.getAnnotation(FieldIntegerMinMax.class) != null){
					comment += ", minimum = " + f.getAnnotation(FieldIntegerMinMax.class).min() + ", maximum = " + f.getAnnotation(FieldIntegerMinMax.class).max();
				}
				
				if(f.getAnnotation(FieldDoubleMinMax.class) != null){
					comment += ", minimum = " + f.getAnnotation(FieldDoubleMinMax.class).min() + ", maximum = " + f.getAnnotation(FieldDoubleMinMax.class).max();
				}
				
				if(f.getAnnotation(FieldFloatMinMax.class) != null){
					comment += ", minimum = " + f.getAnnotation(FieldFloatMinMax.class).min() + ", maximum = " + f.getAnnotation(FieldFloatMinMax.class).max();
				}
				
				if(f.getType().isEnum()){
					comment += ", possible values: ";
					for(outputModes om : outputModes.values()){
						comment += om.toString() + ", ";
					}
					comment = comment.substring(0, comment.length() - 2);
				}
				
				out.write(comment);
				out.newLine();
				
				out.write(f.getName() + " = " + f.get(this).toString());
				out.newLine();
				out.newLine();
			} catch (IllegalArgumentException e) {
				//shouldn't happen
				logger.severe("IllegalArgumentException happened where it shouldn't, please inform the developer of the exact error");
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				//shouldn't happen
				logger.severe("IllegalAccessException happened where it shouldn't, please inform the developer of the exact error");
				e.printStackTrace();
			}
		}

		out.close();
		
		lastFileName = fileName;
	}
	
	/**
	 * Loads configuration data from the specified file.
	 * @param fileName Filename, no path necessary if it's in the same folder as the executable.
	 * @throws FileNotFoundException Thrown if no file with that relative or absolute file name was found.
	 */
	public void loadData(String fileName) throws FileNotFoundException {
		logger.info("Trying to load configuration data from file '" + fileName + "'");
		Scanner scanner = new Scanner(new File(fileName));
		try {
			while(scanner.hasNextLine()) {
				parseValue(scanner.nextLine());
			}
		} finally {
			scanner.close();
		}
	}
	
	/**
	 * Parses a "fieldname = value" pair and sets the field of this object to that value.
	 * A line starting with a '#' gets ignored, spaces before don't matter.
	 * @param line A pair of the field name and the value separated by a = ; whitespaces at the beginning, the end, or each side of the = are ignored.
	 */
	private void parseValue(String line) {
		if(line.trim().equals("") || line.trim().startsWith("#")){
			//ignore comment line
			return;
		}
		
		Scanner scanner = new Scanner(line);
		
		scanner.useDelimiter("=");
		
		if(scanner.hasNext()){ //it better should, otherwise there isn't an = in this line
			try{
				setField(scanner.next().trim(), scanner.next().trim());
			}catch(NoSuchElementException e){
				//wrong formatted line or comment line, just ignore
				logger.warning("NoSuchElementException happened, non-parseable line in configuration file, ignoring line '" + line + "'");
			}
		}
		
		scanner.close();
	}

	/**
	 * Sets the field of this object to the given value.
	 * The method does nothing when either the field does not exist, or the value can't be parsed.
	 * @param field Field of this Class to access. Types can be: Boolean, Integer, Float, Double, String, outputModes; no native types! does not work when Class of the type hasn't got the method valueOf().
	 * @param value The value to set the given field to.
	 */
	private void setField(String field, String value) {
		try {
			Field wField = this.getClass().getField(field);

			if(wField.getType().isEnum()){
				//Enums have no constructor that takes a string, sadly
				Method parseMethod = wField.getType().getMethod("valueOf", new Class[]{String.class}); //throws NoSuchMethodException if the Field hasn't got valueOf(String)
				wField.set(this, parseMethod.invoke(wField, value));
			}else{
				wField.set(this, wField.getType().getConstructor(String.class).newInstance(value));
			}
			
			//TODO find a cleaner way to do the following cropping
			
			if(wField.getAnnotation(FieldIntegerMinMax.class) != null){
				//check for min/max
				if((Integer)wField.get(this) > wField.getAnnotation(FieldIntegerMinMax.class).max()){
					logger.warning("Setting '" + wField.getName() + "' too big, cropping to maximum = " + wField.getAnnotation(FieldIntegerMinMax.class).max());
					wField.set(this, wField.getAnnotation(FieldIntegerMinMax.class).max());
				}else if((Integer)wField.get(this) < wField.getAnnotation(FieldIntegerMinMax.class).min()){
					logger.warning("Setting '" + wField.getName() + "' too small, cropping to minimum = " + wField.getAnnotation(FieldIntegerMinMax.class).min());
					wField.set(this, wField.getAnnotation(FieldIntegerMinMax.class).min());
				}
			}
			
			if(wField.getAnnotation(FieldFloatMinMax.class) != null){
				//check for min/max
				if((Float)wField.get(this) > wField.getAnnotation(FieldFloatMinMax.class).max()){
					logger.warning("Setting '" + wField.getName() + "' too big, cropping to maximum = " + wField.getAnnotation(FieldFloatMinMax.class).max());
					wField.set(this, wField.getAnnotation(FieldFloatMinMax.class).max());
				}else if((Float)wField.get(this) < wField.getAnnotation(FieldFloatMinMax.class).min()){
					logger.warning("Setting '" + wField.getName() + "' too small, cropping to minumum = " + wField.getAnnotation(FieldFloatMinMax.class).min());
					wField.set(this, wField.getAnnotation(FieldFloatMinMax.class).min());
				}
			}
			
			if(wField.getAnnotation(FieldDoubleMinMax.class) != null){
				//check for min/max
				if((Double)wField.get(this) > wField.getAnnotation(FieldDoubleMinMax.class).max()){
					logger.warning("Setting '" + wField.getName() + "' too big, cropping to maximum = " + wField.getAnnotation(FieldDoubleMinMax.class).max());
					wField.set(this, wField.getAnnotation(FieldDoubleMinMax.class).max());
				}else if((Double)wField.get(this) < wField.getAnnotation(FieldDoubleMinMax.class).min()){
					logger.warning("Setting '" + wField.getName() + "' too small, cropping to minimum = " + wField.getAnnotation(FieldDoubleMinMax.class).min());
					wField.set(this, wField.getAnnotation(FieldDoubleMinMax.class).min());
				}
			}
			
			logger.config("    Read setting '" + wField.getName() + "' with value '" + wField.get(this).toString() + "' correctly");
			
		} catch (SecurityException e) {
			//shouldn't happen
			logger.severe("SecurityException happened where it shouldn't, please inform the developer of the exact error");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			//ignore as there is an error in the loaded configuration file
			logger.warning("NoSuchFieldException happened, nonexistant variable set in configuration file, ignoring field '" + field + "' with value '" + value + "'");
		} catch (NoSuchMethodException e) {
			//shouldn't happen
			logger.severe("NoSuchMethodException happened where it shouldn't, please inform the developer of the exact error");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//shouldn't happen
			logger.severe("IllegalArgumentException happened where it shouldn't, please inform the developer of the exact error");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//shouldn't happen
			logger.severe("IllegalAccessException happened where it shouldn't, please inform the developer of the exact error");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			//ignore as there is an error in the loaded configuration file (eg. a nonexistant enum value)
			logger.warning("InvocationTargetException happened, probably an invalid enum value in the configuration file for field '" + field + "'");
		} catch (InstantiationException e) {
			//shouldn't happen as all used types have got a Constructor with one parameter of type String (exept to Enums which get treated separately)
			logger.severe("InstantiationException happened where it shouldn't, please inform the developer of the exact error");
			e.printStackTrace();
		}
	}
}
