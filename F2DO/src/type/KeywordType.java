package type;

public enum KeywordType {
	AT,
	ON,
	FROM,
	IN,
	BY,
	DUE,
	TODAY,
	TOMORROW,
	TONIGHT,
	THIS,
	INVALID;
	
	public static KeywordType toType(String word) {
		try {
			word = word.toUpperCase();
			
			if (word.equals("TMR")) {
				return TOMORROW;
			}
			
			return valueOf(word); 
		} catch (Exception e) {
			return INVALID; 
		}
	}
}
