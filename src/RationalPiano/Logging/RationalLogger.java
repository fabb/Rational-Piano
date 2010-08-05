package RationalPiano.Logging;

import java.io.IOException;
import java.util.logging.*;

/**
 * A logger for all log-worthy messages in the whole application
 * 
 * @author Fabian Ehrentraud
 * @date 2010-07-27
 * @version 1.01
 * @licence Licensed under the Open Software License (OSL 3.0)
 */
public class RationalLogger {

	private static FileHandler logFile;
	private static SimpleFormatter formatter;

	/**
	 * Initiate the top logger with the specified log level.
	 * @param loglevel Log Level to use.
	 * @param logToFile Whether the log should also be written to a text file with filename logFileName.
	 * @param logFileName Filename to write the log to.
	 * @throws IOException When the file logFileName couldn't be opened.
	 */
	public static void initiate(Level loglevel, boolean logToFile, String logFileName) throws IOException {
		Logger logger = Logger.getLogger(""); //top logger

		logger.setLevel(loglevel);
		
		//the default ConsoleHandler still has Level.info ando won't let lower loglevels pass. the FileHandler will, though
		//workaround for setting the loglevel of the ConsoleHandler
		for (Handler handler : logger.getHandlers()) {
			handler.setLevel(java.util.logging.Level.ALL);
	    }

		if(logToFile == true){
			formatter = new SimpleFormatter();
			logFile = new FileHandler(logFileName);
			logFile.setFormatter(formatter);
			logger.addHandler(logFile);
		}
			

		//logger.addHandler(new ConsoleHandler()); //it's already there by standard, this would add it twice
	}
}
