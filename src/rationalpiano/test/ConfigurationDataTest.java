package rationalpiano.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import rationalpiano.persistence.ConfigurationData;

/**
 * A simple test of the ConfigurationData class
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-26
 * @version 1.0
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class ConfigurationDataTest {

	@Test
	public void testLoadData() {
		ConfigurationData persy = new ConfigurationData();
		
		String filename = "test/testload.cfg";
		
		try {
			persy.loadData(filename);
		} catch (IOException e1) {
			e1.printStackTrace();
			fail("IOException");
		}
	}

	@Test
	public void testSaveData() {
		ConfigurationData persy = new ConfigurationData();
		
		boolean defaultFs = persy.fullscreen;
		
		persy.fullscreen = !persy.fullscreen;
		
		String filename = "test/testsave.cfg";
		
		try {
			persy.saveData(filename);
		} catch (IOException e1) {
			e1.printStackTrace();
			fail("IOException");
		}
	}
	
	@Test
	public void testSaveLoadData() {
		ConfigurationData persy = new ConfigurationData();
		
		boolean defaultFs = persy.fullscreen;
		
		persy.fullscreen = !persy.fullscreen;
		
		String filename = "test/testloadsave.cfg";
		
		try {
			persy.saveData(filename);
		} catch (IOException e1) {
			e1.printStackTrace();
			fail("IOException");
		}
		
		persy = new ConfigurationData();
		
		try {
			persy.loadData(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("File not found!");
		}
		
		if(persy.fullscreen == defaultFs){
			fail("still default value");
		}
	}

}
