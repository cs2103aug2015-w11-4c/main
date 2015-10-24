package storage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentSkipListMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import object.Task;

/**
 * StorageHelper class maintains reading and writing of JSON file.
 * @author 
 *
 */
public class StorageHelper {
	private static SimpleDateFormat _dateFormat = 
			new SimpleDateFormat("EEE dd MMM HH:mm:ss zzz yyyy");
	
	/**
	 * Write the task list into JSON file.
	 * @param file - file to be written
	 * @param taskList - task list
	 * @return true if the task list is written into the file successfully; false otherwise
	 */
	public static boolean writeJsonFile(File file, 
			ConcurrentSkipListMap<Integer, Task> taskList) {

		try {
			ObjectMapper objMapper = new ObjectMapper();
			
			objMapper.setDateFormat(_dateFormat);
			objMapper.writeValue(file, taskList);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	/**
	 * Read the task list stored from the JSON file.
	 * @param file - file to be retrieved
	 * @return the task list
	 */
	public static ConcurrentSkipListMap<Integer, Task> readJsonFile(File file) {
		ConcurrentSkipListMap<Integer, Task> taskList = 
				new ConcurrentSkipListMap<Integer, Task>();
		
		try {
			ObjectMapper objMapper = new ObjectMapper();
			TypeReference<ConcurrentSkipListMap<Integer, Task>> typeRef = 
					new TypeReference<ConcurrentSkipListMap<Integer, Task>>(){};
			
			objMapper.setDateFormat(_dateFormat);
			taskList = objMapper.readValue(file, typeRef);
			
		} catch (Exception e) {
			e.printStackTrace();
			return taskList;
		}
		return taskList;
	}
	
	/**
	 * Create empty JSON file.
	 * @param file - file to be created
	 * @return true if the file is created successfully; false otherwise
	 */
	public static boolean createJsonFile(File file) {
		try {
			ConcurrentSkipListMap<Integer, Task> taskList = 
					new ConcurrentSkipListMap<Integer, Task>();
			ObjectMapper objMapper = new ObjectMapper();
			
			objMapper.writeValue(file, taskList);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}
}
