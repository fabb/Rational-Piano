package rationalpiano.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.Test;

import rationalpiano.input.InputMidi;
import rationalpiano.logging.RationalLogger;

public class InputMidiTest {

	@Test
	public void testInputMidi() throws IOException {
		RationalLogger.initiate(Level.CONFIG, false, null);
		InputMidi inputmidi = new InputMidi(null, null, null, "EDIROL PCR 1");
		while(true);
		//fail("Not yet implemented");
	}

}
