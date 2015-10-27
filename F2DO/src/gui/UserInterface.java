package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import object.Task;
import logic.LogicController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class UserInterface extends Application {
	private static BorderPane _root = new BorderPane();
	private static Scene _defaultScene = new Scene(_root, 650, 480);
	private static VBox _vbox = new VBox();
	private static VBox _tables = new VBox();
	
	private static Button _taskButton = new Button();
	private static Button _floatingButton = new Button();
	private static TextField _field = new TextField();
	private static TextArea _feedBack = new TextArea();
	//private static int commandIndex;
	
	private static UITable _taskTable = new UITable(false);
	private static UITable _floatingTable = new UITable(true);
	
	private static int _ctrlUCount = 0;
	private static int _ctrlRCount = 0;
	
	private static final BooleanProperty _ctrlPressed = new SimpleBooleanProperty(false);
	private static final BooleanProperty _uPressed = new SimpleBooleanProperty(false);
	private static final BooleanProperty _rPressed = new SimpleBooleanProperty(false);
	private static final BooleanProperty _ePressed = new SimpleBooleanProperty(false);
	private static final BooleanBinding _ctrlAndUPressed = _ctrlPressed.and(_uPressed);
	private static final BooleanBinding _ctrlAndRPressed = _ctrlPressed.and(_rPressed);
	private static final BooleanBinding _ctrlAndEPressed = _ctrlPressed.and(_ePressed);
	//private static ArrayList<String> commandHistory = new ArrayList<String>();
	
	private static ArrayList<Task> _displayList = new ArrayList<Task>();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		_taskButton.setText("Tasks & Events");
		_floatingButton.setText("Floating Tasks");
		
		_field.setPromptText("Enter your command..");
		setFeedback(_feedBack);
		
		_vbox.setAlignment(Pos.CENTER);
		_vbox.setSpacing(2);
		_vbox.getChildren().addAll(_feedBack, _field);     
        BorderPane.setMargin(_vbox, new Insets(10, 20, 20, 20));
        _root.setBottom(_vbox);
        
        updateTables();
        BorderPane.setMargin(_tables, new Insets(10,20,0,20));
        BorderPane.setAlignment(_tables, Pos.CENTER);
        _tables.getChildren().addAll(_floatingButton, _floatingTable, _taskButton, _taskTable);
        _tables.setSpacing(5);
        _root.setCenter(_tables);
        
        setKeyCombinationListener();
        
        // Event handler for input processing
        setKeyPressed(_field, _feedBack, _taskTable, primaryStage);
        
        String css = UserInterface.class.getResource("style.css").toExternalForm();
        _defaultScene.getStylesheets().add(css);
        primaryStage.setScene(_defaultScene);
        primaryStage.show();
	}
	
	/**
	 * Update tables.
	 */
	private static void updateTables() {
		ArrayList<Task> nonFloatingList = LogicController.getNonFloatingList();
		ArrayList<Task> floatingList = LogicController.getFloatingList();
		
		_displayList.clear();
		_displayList.addAll(nonFloatingList);
		_displayList.addAll(floatingList);
		
		_taskTable.updateTable(nonFloatingList, floatingList);
		_floatingTable.updateTable(nonFloatingList, floatingList);
	}
	
	/**
	 * Set the design of feedback.
	 * @param feedback
	 */
	private void setFeedback(TextArea feedBack) {
		feedBack.setFont(Font.font ("Verdana", FontWeight.SEMI_BOLD, 12));
		feedBack.setPrefRowCount(3);
		feedBack.setText("Welcome to F2DO, your personalised task manager(:\n"
				+ "Type " + "\"Help\"" + " for a list of commands to get started.");
		feedBack.setMouseTransparent(true);
	}
	
	/**
	 * Create key combination handler.
	 * CTRL+U: undo listener.
	 * CTRL+R: redo listener.
	 * CTRL+E: exit listener.
	 */
	private void setKeyCombinationListener() {
		// Undo listener
		_ctrlAndUPressed.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				_ctrlUCount += 1;
				 
				 if ((_ctrlUCount % 2) == 0) {
					 _ctrlUCount = 0;
					 String feedbackMsg = LogicController.undo();
					 _feedBack.setText(feedbackMsg);
					 updateTables();
				 }
			}
        });
		
		// Redo listener
		_ctrlAndRPressed.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				_ctrlRCount += 1;
				 
				 if ((_ctrlRCount % 2) == 0) {
					 _ctrlRCount = 0;
					 String feedbackMsg = LogicController.redo();
					 _feedBack.setText(feedbackMsg);
					 updateTables();
				 }
			}
        });
		
		// Exit listener
		_ctrlAndEPressed.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.exit(1);
			}
        });
        
        _root.setOnKeyPressed((KeyEvent event) -> {
        	if (event.getCode() == KeyCode.CONTROL) {
        		_ctrlPressed.set(true);
            } else if (event.getCode() == KeyCode.U) {
            	_uPressed.set(true);
            } else if (event.getCode() == KeyCode.R) {
            	_rPressed.set(true);
            } else if (event.getCode() == KeyCode.E) {
            	_ePressed.set(true);
            }
        });
        
        _root.setOnKeyReleased((KeyEvent event) -> {
        	if (event.getCode() == KeyCode.CONTROL) {
        		_ctrlPressed.set(false);
            } else if (event.getCode() == KeyCode.U) {
            	_uPressed.set(false);
            } else if (event.getCode() == KeyCode.R) {
            	_rPressed.set(false);
            } else if (event.getCode() == KeyCode.E) {
            	_ePressed.set(false);
            }
        });
	}
	
	/**
	 * Set the event handler when the key is pressed.
	 * @param field
	 * @param feedback
	 * @param table
	 */
	private void setKeyPressed(TextField field, TextArea feedback, TableView<Integer> table, Stage primaryStage) {
		field.setOnKeyPressed((KeyEvent event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				
				String userInput = field.getText();
			
				//commandHistory.add(userInput);
				//commandIndex = commandHistory.size() - 1;
				
				field.clear();
				
				String feedbackMsg = LogicController.process(userInput, _displayList);
				
				// help section still has some bugs at the moment -- not linked with logic component yet
				// if user types "Help", the scene changes - the two tables will be removed and the cheat sheet
				// will be displayed in the existing feedback box or alternatively, inside a list.

				if (feedbackMsg == "help") {
					feedback.setPrefRowCount(50); 
					try {
						setCheatSheetContent();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					feedback.setText(feedbackMsg);
					updateTables();
					//updateTable(_taskTable, false);
					//updateTable(_floatingTable, true);
				}			
				feedback.setText(feedbackMsg);
				updateTables();
				//updateTable(_taskTable, false);
				//updateTable(_floatingTable, true);
			}
			/*else if (event.getCode() == KeyCode.UP) {
				
				//if (!commandHistory.isEmpty()) {
				//	field.setText(commandHistory.get(commandIndex));
				//	int length = commandHistory.get(commandIndex).length();
					commandIndex--;
					
					Platform.runLater( new Runnable() {
					    @Override
					    public void run() {
					        field.positionCaret(length);
					    }
					});
					
					if (commandIndex < 0) {
						commandIndex = 0;
					}
				}
			}
			else if (event.getCode() == KeyCode.DOWN) {
				
				if (!commandHistory.isEmpty()) {
					field.setText(commandHistory.get(commandIndex + 1));
					int length = commandHistory.get(commandIndex + 1).length();
					commandIndex++;
					
					Platform.runLater( new Runnable() {
					    @Override
					    public void run() {
					        field.positionCaret(length);
					    }
					});
					
					if (commandIndex > commandHistory.size()) {
						commandIndex = 0;
					}
				}
			}*/
			/* ---Unsuccessful at the moment ---*/
			else if (event.getCode() == KeyCode.TAB) {
				exit(primaryStage);
			}
		});
	}
				
	private void setCheatSheetContent() throws IOException  {
		String text;
		StringBuilder stringBuilder = new StringBuilder();
		
		FileReader in = new FileReader("cheatsheet.txt");
	    BufferedReader br = new BufferedReader(in);

	    while ((text = br.readLine()) != null) {
	    	stringBuilder.append(text).append("\n");
	    }
	    br.close();
	}
	
	private void exit(Stage primaryStage) {
		primaryStage.close();
	}
}