package mealplanning;
//The purpose of this class is to represent a food item.
public class Food {

	//These represent the instance variables for food. 
	private String name;
	private String category;
	private String quanity;
	private int calories;

	//This is the constructor. 
	public Food(String name, String category, String quanity, int calories) {
		this.name = name;
		this.category = category;
		this.quanity = quanity;
		this.calories = calories;
	}
	
	
	// Getter (Method) for getting a private field/getting a value but not exposing the value within the field. 

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getQuanity() {
		return quanity;
	}

	public int getCalories() {
		return calories;
	}

	
	// This string is the representation of all of the food. 
	@Override
	public String toString() {
		return String.format("[%s] %s %s %d Calories", category, quanity, name, calories);
	}

	
}
