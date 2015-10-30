package type;

public enum CommandType {
	ADD,
	EDIT,
	DELETE,
	SEARCH,
	SHOW,
	DONE,
	UNDONE,
	HELP,
	HOME,
	INVALID, 
	CAT;
	
	public static CommandType toCmd(String word) {
		try {
			
			if (word.equals("DEL")) {
				return DELETE;
			}
			
			return valueOf(word); 
		} catch (Exception e) {
			return INVALID; 
		}
	}
}
