/*
 * RUNI version of the Scrabble game.
 */
public class Scrabble {

	// Note 1: "Class variables", like the five class-level variables declared below,
	// are global variables that can be accessed by any function in the class. It is
	// customary to name class variables using capital letters and underline characters.
	// Note 2: If a variable is declared "final", it is treated as a constant value
	// which is initialized once and cannot be changed later.

	// Dictionary file for this Scrabble game
	static final String WORDS_FILE = "dictionary.txt";

	// The "Scrabble value" of each letter in the English alphabet.
	// 'a' is worth 1 point, 'b' is worth 3 points, ..., z is worth 10 points.
	static final int[] SCRABBLE_LETTER_VALUES = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3,
												  1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10 };

	// Number of random letters dealt at each round of this Scrabble game
	static int HAND_SIZE = 10;

	// Maximum number of possible words in this Scrabble game
	static int MAX_NUMBER_OF_WORDS = 100000;

    // The dictionary array (will contain the words from the dictionary file)
	static String[] DICTIONARY = new String[MAX_NUMBER_OF_WORDS];

	// Actual number of words in the dictionary (set by the init function, below)
	static int NUM_OF_WORDS;

	// Populates the DICTIONARY array with the lowercase version of all the words read
	// from the WORDS_FILE, and sets NUM_OF_WORDS to the number of words read from the file.
	public static void init() {
		// Declares the variable in to refer to an object of type In, and initializes it to represent
		// the stream of characters coming from the given file. Used for reading words from the file.  
		In in = new In(WORDS_FILE);
        System.out.println("Loading word list from file...");
        NUM_OF_WORDS = 0;
		while (!in.isEmpty()) {
			// Reads the next "token" from the file. A token is defined as a string of 
			// non-whitespace characters. Whitespace is either space characters, or  
			// end-of-line characters.
			DICTIONARY[NUM_OF_WORDS++] = in.readString().toLowerCase();
		}
        System.out.println(NUM_OF_WORDS + " words loaded.");
	}

	// Checks if the given word is in the dictionary.
	public static boolean isWordInDictionary(String word) {
		for (int i = 0; i < NUM_OF_WORDS; i++) { //goes over the entire dictionay
			if (DICTIONARY[i].equals(word)) //searches if the word is in the dictionary
				return true;
		}
		return false;
	}
	
	//Returns true if the sequence "runi" is inside the String
	//and false otherwise
	public static boolean runiFound (String word) {
		String temp = word.toLowerCase();
		int count = 0;
		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) != 'r' && temp.charAt(i) != 'u'
			&& temp.charAt(i) != 'n' && temp.charAt(i) != 'i') //sequence interrupted
				count = 0;
			if (temp.charAt(i) == 'r' && count == 0) 
				count++;
			if (temp.charAt(i) == 'u' && count == 1) 
				count++;
			if (temp.charAt(i) == 'n' && count == 2) 
				count++;
			if (temp.charAt(i) == 'i' && count == 3) 
				count++;
			if (count == 4) //found the sequence runi
				return true;
		}
		return false;
	}
	
	// Returns the Scrabble score of the given word.
	// If the length of the word equals the length of the hand, adds 50 points to the score.
	// If the word includes the sequence "runi", adds 1000 points to the game.
	public static int wordScore(String word) {
		int points = 0;
		for (int i = 0; i < word.length(); i++) {
			points = points + SCRABBLE_LETTER_VALUES[(int)(word.charAt(i)) - 97]; //adds points per letter
		}
		points = points * word.length(); //multiplies the score by the word length
		if (word.length() == HAND_SIZE) //special case
			points = points + 50;
		if (runiFound(word)) //special case
			points = points + 1000;
		return points;
	}

	// Creates a random hand of length (HAND_SIZE - 2) and then inserts
	// into it, at random indexes, the letters 'a' and 'e'
	// (these two vowels make it easier for the user to construct words)
	public static String createHand() {
		String hand = MyString.randomStringOfLetters(HAND_SIZE - 2);
		hand = hand.toLowerCase();
		hand = MyString.insertRandomly('a', hand); //inserts 'a' randomly
		hand = MyString.insertRandomly('e', hand); //inserts 'e' randomly
		return hand;
	}
	
    // Runs a single hand in a Scrabble game. Each time the user enters a valid word:
    // 1. The letters in the word are removed from the hand, which becomes smaller.
    // 2. The user gets the Scrabble points of the entered word.
    // 3. The user is prompted to enter another word, or '.' to end the hand. 
	public static void playHand(String hand) {
		int score = 0;
		// Declares the variable in to refer to an object of type In, and initializes it to represent
		// the stream of characters coming from the keyboard. Used for reading the user's inputs.   
		In in = new In();
		while (hand.length() > 0) {
			System.out.println("Current Hand: " + MyString.spacedString(hand));
			System.out.println("Enter a word, or '.' to finish playing this hand:");
			// Reads the next "token" from the keyboard. A token is defined as a string of 
			// non-whitespace characters. Whitespace is either space characters, or  
			// end-of-line characters.
			String input = in.readString();
			if (input.equals(".")) //finished playing current hand
				break;
			if (isWordInDictionary(input) && MyString.subsetOf(input, hand)) { //valid word
				score = score + wordScore(input);
				System.out.println(input + " earned " + wordScore(input) + " points. Score: " + score + " points\n"); //wtf
				hand = MyString.remove(hand, input); //removes used letters
			}
			else {
				if (!MyString.subsetOf(input, hand)) { //used letters that aren't in hand
					System.out.println("Invalid word. Try again."); 
				} else if (!isWordInDictionary(input)) { //word not found in dictionary
					System.out.println("No such word in the dictionary. Try again."); 
				}
			}
		}
		if (hand.length() == 0) 
	        System.out.println("Ran out of letters. Total score: " + score + " points");
		else 
			System.out.println("End of hand. Total score: " + score + " points");
	}

	// Plays a Scrabble game. Prompts the user to enter 'n' for playing a new hand, or 'e'
	// to end the game. If the user enters any other input, writes an error message.
	public static void playGame() {
		// Initializes the dictionary
    	init();
		// The variable in is set to represent the stream of characters 
		// coming from the keyboard. Used for getting the user's inputs.  
		In in = new In();
		String hand = "";
		while(true) {
			System.out.println("Enter n to deal a new hand, or e to end the game:");
			// Gets the user's input, which is all the characters entered by 
			// the user until the user enter the ENTER character.
			String input = in.readString();
			if (input.equals("n")) { //new hand to be created
				hand = createHand();
				HAND_SIZE = hand.length();
				playHand(hand);
			}
			if (input.equals("e")) //game over
				break;
		}
	}

	public static void main(String[] args) {
		//testBuildingTheDictionary(); 
		//testScrabbleScore();
		//testCreateHands();  
		//testPlayHands();
		playGame();
	}

	public static void testBuildingTheDictionary() {
		init();
		// Prints a few words
		for (int i = 0; i < 5; i++) {
			System.out.println(DICTIONARY[i]);
		}
		System.out.println(isWordInDictionary("mango"));
	}
	
	public static void testScrabbleScore() {
		System.out.println(wordScore("bee"));	
		System.out.println(wordScore("babe"));
		System.out.println(wordScore("friendship"));
		System.out.println(wordScore("running"));
	}
	
	public static void testCreateHands() {
		System.out.println(createHand());
		System.out.println(createHand());
		System.out.println(createHand());
	}

	public static void testPlayHands() {
		init();
		//playHand("ocostrza");
		//playHand("arbffip");
		//playHand("aretiin");
	}
}
