package storage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import objects.*;
import parser.DateTime;
import type.TaskType;

@SuppressWarnings("serial")
public class StorageHelper extends Storage {

	public static ArrayList<JSONObject> jsonList(ArrayList<Task> taskList){
		ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();
		JSONObject obj;
		for (int i = 0; i < taskList.size(); i++) {
			obj = new JSONObject();
			try {
				obj.put("TaskID", taskList.get(i).getTaskID());
				obj.put("TaskName", taskList.get(i).getTaskName());
				obj.put("TaskType", taskList.get(i).getTaskType());

				if (taskList.get(i).getStartDate()  != null){
					obj.put("StartDate", taskList.get(i).getStartDate());
				} else {
					obj.put("StartDate", JSONObject.NULL);
				}
				//obj.put("TaskName", JSONObject.NULL);
				if (taskList.get(i).getEndDate()  != null){
					obj.put("EndDate", taskList.get(i).getEndDate());
				} else {
					obj.put("EndDate", JSONObject.NULL);
				}

			} catch (JSONException e) {
				System.out.println("JSONException detected!");
				e.printStackTrace();	
			}
			jsonList.add(obj);
		}

		return jsonList;

	}

	public void jsonListToString(ArrayList<JSONObject> jsonList){
		StringWriter out;
		JSONObject obj;
		for (int i = 0;i<jsonList.size();i++){
			out = new StringWriter();
			obj = new JSONObject();
			obj = jsonList.get(i);
			try {
				System.out.println(obj.toString());
			} catch (Exception e) {
				System.out.println("Error printing JSON Object");
			}
			String jsonText = out.toString();
			System.out.println(jsonText);
		}
	}

	public static boolean saveTojsonFile(ArrayList<JSONObject> jsonList){
		boolean isSaveSuccess = false;
		JSONObject obj;
		FileWriter file;
		try {
			file = new FileWriter(filePath);

			for (int i = 0;i<jsonList.size();i++){
				obj = new JSONObject();
				obj = jsonList.get(i);
				file.write(obj.toString(4));
				//System.out.println(obj.toString());
				isSaveSuccess = true;
			}
			file.flush();
			file.close();
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return isSaveSuccess;
	}

	public static ArrayList<Task> readFromjsonFile(){
		ArrayList<Task> taskList = new ArrayList<Task>();
		Date sdate = null,edate = null;
		FileReader reader = null;
		try {
			reader = new FileReader(filePath);
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		}

		JSONTokener tokener = new JSONTokener(reader);

			do{
				JSONObject obj;
				try{
				obj  = new JSONObject(tokener);	
			} catch (Exception e) {
				// empty file will throw exception
				break;
			}
	
				System.out.println(obj.toString());
				Task task = null;
				if (obj.get("TaskType").equals(TaskType.FLOATING.toString())){
					task = new TaskFloating((int)obj.get("TaskID"), obj.get("TaskName").toString(), 0);
					task.setTaskType(TaskType.FLOATING);
					taskList.add(task);
				} else if (obj.get("TaskType").equals(TaskType.DEADLINE.toString())) {
					String endDateString = obj.get("EndDate").toString();
					endDateString = endDateString.substring(4, endDateString.length());
					try {
						edate = DateTime.parse(endDateString);
					} catch (JSONException e) {
						e.printStackTrace();
					}
//					Fri Oct 23 12:00:00 SGT 2015 - saved format
					task = new TaskDeadLine((int)obj.get("TaskID"), obj.get("TaskName").toString(),edate, 0);
					task.setTaskType(TaskType.DEADLINE);
					taskList.add(task);
				} else if (obj.get("TaskType").equals(TaskType.EVENT.toString()) ){
					String endDateString = obj.get("EndDate").toString();
					endDateString = endDateString.substring(4, endDateString.length());
					String startDateString = obj.get("StartDate").toString();
					startDateString = startDateString.substring(4, startDateString.length());
					try {
						sdate = DateTime.parse(startDateString);
						edate = DateTime.parse(endDateString);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					task = new TaskEvent((int)obj.get("TaskID"), obj.get("TaskName").toString(),sdate,edate, 0);
					task.setTaskType(TaskType.EVENT);
					taskList.add(task);
				} else {
					//do nothing
				}
	
				tokener.nextTo("{");
			} while (tokener.more());


		//System.out.println("SIZE = "+taskList.size());
		//for (int i = 0 ;i <taskList.size() ;i++){
			
			//System.out.println("LOL "+taskList.get(i).getTaskName());
		//}
		
		return taskList;
	}	
	
}
