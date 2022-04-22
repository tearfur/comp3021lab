import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringProcessingSystem {
	private static String originalText;
	private static String modifiedText;
	private static final Scanner reader=new Scanner(System.in); //static reader, no need to open reader in the methods of the class

	public static void split() {
		System.out.print("Please input a delimiter: ");
		String target = reader.next();

		System.out.println(originalText.replace(Pattern.quote(target), System.lineSeparator()));
	}

	public static void removeSubstring() {
		System.out.print("Please input string to remove: ");
		String target = reader.next();

		System.out.println("String before removing '" + target + "': " + originalText);

		modifiedText = originalText.replaceFirst(Pattern.quote(target), "");
		if (!modifiedText.equals(originalText))
			System.out.println("String after removing '" + target + "': " + modifiedText);
		else
			System.out.println("target is not found");
	}


	public static void shiftString() {
		System.out.print("Please input amount of shift: ");
		int shiftAmount = reader.nextInt(), cutoff = originalText.length() - Math.floorMod(shiftAmount, originalText.length());

		modifiedText = originalText.substring(cutoff) + originalText.substring(0, cutoff);
		System.out.println("After shifting \"" + originalText + "\" by " + shiftAmount + ": \"" + modifiedText + '"');
	}


	public static void countVowels() {
		Matcher matcher = Pattern.compile("[aeiouAEIOU]").matcher(originalText);
		int count = 0;
		while (matcher.find())
			++count;
		System.out.println("number of vowels in \"" + originalText + "\": " + count);
	}

	public static void ceaserCipher() {
		System.out.print("Please input amount of shift: ");
		int shift = reader.nextInt();
		StringBuilder builder = new StringBuilder(originalText.toUpperCase(Locale.ENGLISH));

		for (int i = 0; i < builder.length(); ++i) {
			int c = builder.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				c = Math.floorMod(c - 'A' + shift, 26) + 'A';
				builder.setCharAt(i, (char)c);
			}
		}
		System.out.println("ciphertext: " + builder);
	}

	public static void main(String args[]){

		System.out.println("Welcome to the String Handling System!");
		System.out.print("Please input a string you want to process: ");

		Scanner reader = new Scanner(System.in); // Scanner is used for Java input
		originalText = reader.nextLine();

		String option = "";

		while (!option.equals("Q")) {
			System.out.println("=========== Options ============");
			System.out.println("1: Split the string");
			System.out.println("2: Remove all substring from string");
			System.out.println("3: Shift the string");
			System.out.println("4: Count number of vowels");
			System.out.println("5: Ceaser cipher");
			System.out.println("================================");
			System.out.println("Please choose an option (type in Q if you want to quit): ");
			option = reader.next();

			switch (option){
				case "1":
					split();
					break;
				case "2":
					removeSubstring();
					break;
				case "3":
					shiftString();
					break;
				case "4":
					countVowels();
					break;
				case "5":
					ceaserCipher();
					break;
				default:
					if (option.equals("Q"))
						System.out.println("Goodbye!");
					else
						System.out.println("Invalid Option! Please try again: ");
					break;
			}
		}
		reader.close();
	}
}