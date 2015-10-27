package history;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.junit.Before;
import org.junit.Test;

import object.Task;
import type.CommandType;

public class HistoryTest {
	
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Before
	public void loggerSetup() {
		FileHandler fileTxt = null;
		
	    try {
			fileTxt = new FileHandler("HistoryLogging.txt");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

	    logger.addHandler(fileTxt);
	    SimpleFormatter formatterTxt = new SimpleFormatter();
	    fileTxt.setFormatter(formatterTxt);
	}
	
	@Test
	public void test() {
		Task task1 = new Task();
		task1.setTaskID(1);
		task1.setStartDate(null);
		task1.setEndDate(null);
		task1.setTaskName("first task");
		CommandType add = CommandType.ADD;
		CommandType del = CommandType.DELETE;
		CommandType done = CommandType.DONE;
		CommandType undone = CommandType.UNDONE;
		
		logger.info("Starting tests");
		assertTrue(History.push(add, task1));
		assertTrue(History.push(done, task1));
		assertTrue(History.push(undone, task1));
		assertTrue(History.push(del, task1));
	    logger.info("Successful end of tests");
	}
}
