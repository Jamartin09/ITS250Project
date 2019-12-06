package mealplanning;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CaloriesCalculator {
	
	//These are are Constants i.e CATEGORIES/MEALS/LOSE_WEIGHT/MAINTAIN_WEIGHT/GAIN_WEIGHT
	private static final String[] CATEGORIES = {"Meats", "Fruits", "Vegetables", "Grain", "Dairy"}, MEALS = {"Breakfast", "Lunch", "Dinner"};
	private static final int LOSE_WEIGHT = 1, MAINTAIN_WEIGHT = 2 , GAIN_WEIGHT = 3;

	
	
	private static Random random = new Random();

	public static void main(String[] args) {
		Food[][] db = createDatabase();
		
		Scanner scanner = new Scanner(System.in);
		
		
		
		while(true) {

			// System output for selecting the gender. 
			//equalsIgnoreCase method compares two strings while ignoring if it is capitalized or lower case.
			//method will return true if not equal to null and represents equiv. string ignoring case. 
			System.out.print("Enter gender ('m' for male and 'f' for female): ");
			boolean male = scanner.next().equalsIgnoreCase("m");
			//even with ignoring cases both strings are not equal. 
			
			// Read the target for what your goal with weight is.
			//Scans the next token of the input as an int
			System.out.print("Enter target (1. Lose Weight 2. Maintain Weight 3. Gain Weight): ");
			int target = scanner.nextInt();
			//Returns the int scanned from the input.
			
			
			//This is where we calculate both the calories and per meal limit.
			//get maximum Calories from the 
			int maxCalories = getMaximumCalories(male, target);
			int perMealLimit = Math.floorDiv(maxCalories, MEALS.length);
			//Math.floorDiv is a build-in math function that returns the largest int value that is less or equal to the algebraic quotient. 
			

			//Get the full day meal and then it is going to divide it meals
			//We are using a resizable-array implementation of the list interface. 
			ArrayList<Food> meal = getFullDayMeal(maxCalories, db);
			ArrayList<Food>[] meals = divideToMeals(meal, perMealLimit);
			
			
			//This is where the results will be printed
			System.out.printf("\nMaximum Calories: %d, Per Meal Limit: %d\n", maxCalories , perMealLimit);
			for (int j = 0; j < meals.length; j++) {
				//.length is used to find the size of a single dimension array
				// j++ --> j=j+1
				System.out.printf("\n%s [%d calories]:\n", MEALS[j], totalCalories(meals[j]));
				//Shows the meals and how many calories everything has. 
				for (Food food : meals[j]) {
					System.out.println(food);
				}
			}
			
			
			//Ask the user if they would like to exit. 
			System.out.print("\nDo you want to exit? [y/n]: ");
			if(scanner.next().equalsIgnoreCase("y"))
				break;
			//If the user says yes then the loop ends. 
			
			System.out.println();
		}
		
		
		scanner.close();
		
		
		
		
		
	}
	
	
	// Get the maximum calorie amount based on the gender and also the target.
	//males trying to lose weight max calories = 2000. males trying to maintain weight max calories = 2500. Males trying to gain weight max calories = 3000
	//females trying to lose weight the max calories = 1500. Females trying to maintain weight the max calories = 2000. 
	private static int getMaximumCalories(boolean male, int target) {
		if(target == LOSE_WEIGHT)
			return male ? 2000 : 1500;
		else if(target == MAINTAIN_WEIGHT)
			return male ? 2500 : 2000;
		else if(target == GAIN_WEIGHT)
			return male ? 3000 : 2500;
		else
			return 3000;
	}
	
	
	
	//This divides a full day's meal into separate meals
	private static ArrayList<Food>[] divideToMeals(ArrayList<Food> fullDayMeal, int perMealMaxCalories) {
		ArrayList<Food>[] meals = new ArrayList[MEALS.length]; // This is to store the actual meals
		
		
		//This creates array to keep track of the remaining calories for the meal
		int[] remainingCalories = new int[MEALS.length];
		for (int i = 0; i < remainingCalories.length; i++) {
			remainingCalories[i] = perMealMaxCalories;
			meals[i] = new ArrayList<>();
		}
		
		
	
		
		while(!fullDayMeal.isEmpty()) {
			// This removes the first food
			Food food = fullDayMeal.remove(0);
			
			if(canAddToMeal(food.getCalories(), remainingCalories)) { // This checks to see if a food can be added based on the amount of calories
				boolean added = false;
				
				// If any meal is empty, add to it so that there is no meal that will be empty at end.
				for (int i = 0; i < meals.length; i++) {
					if(meals[i].isEmpty()) {
						meals[i].add(food);
						remainingCalories[i] -= food.getCalories();
						added = true;
						break;
					}
				}
				
				
				// If there is no meal that is empty
				while(!added) {
					// Find a random meal where it can be added
					int meal = random.nextInt(meals.length);
					if(remainingCalories[meal] >= food.getCalories()) {
						meals[meal].add(food);
						remainingCalories[meal] -= food.getCalories();
						added = true;
					}
				}
				
				
			}
		}
		
		
		return meals;
		
	}
	
	
	//This checks to see if there is a meal with can accommodate the given calories
	private  static boolean canAddToMeal(int calories, int[] remainingCalories) {
		for (int i = 0; i < remainingCalories.length; i++) {
			if(calories <= remainingCalories[i])
				return true;
		}
		
		return false;
	}
	
	
	// This calculates the total calories in a meal. 
	private static int totalCalories(ArrayList<Food> meal) {
		int total = 0;
		for (Food food : meal) {
			total += food.getCalories();
		}
		
		return total;
	}
	
	// Get a full days meal
	private static ArrayList<Food> getFullDayMeal(int caloriesLimit, Food[][] db) {
		while(true) {
			ArrayList<Food> fullDayMeal = randomizeFullDayMeal(caloriesLimit, db);
			if(fullDayMeal != null)
				return fullDayMeal;
		}
	}
	
	//This randomizes a full days meal to meet the target.
	//If any step fails it returns to null.
	private  static ArrayList<Food> randomizeFullDayMeal(int caloriesLimit, Food[][] db) {
		
		ArrayList<Food> foods = new ArrayList<>();
		int remainingLimit = caloriesLimit;
		
		// Add at least one food of each category
		for (int i = 0; i < CATEGORIES.length; i++) {
			Food food = findFood(remainingLimit, db[i]);
			
			if(food == null)
				return null;
			
			foods.add(food);
			remainingLimit -= food.getCalories();
		}
		
		
		//This will add more foods so that the total calories is as close to the limit as possible.
		while(exists(remainingLimit, db)) {
			int categrory = random.nextInt(CATEGORIES.length);
			Food food = findFood(remainingLimit, db[categrory]);
			
			if(food == null)
				return null;
			
			foods.add(food);
			remainingLimit -= food.getCalories();
		}
		
		
		return foods;
		
		
	}
	
	//Find a food in a category of the food that will meet the target.
	private  static Food findFood(int maxCalories, Food[] foods) {
		if(!exists(maxCalories, foods)) //If no such food exist
			return null;
		
		//Find a random food andd add
		while(true) {
			int index = random.nextInt(foods.length);
			if(foods[index].getCalories() <= maxCalories)
				return foods[index];
		}
	}
	
	
	//Check to see if there exists at least one food that will meet the calorie limit
	private  static boolean exists(int maxCalories, Food[] foods) {
		for (int i = 0; i < foods.length; i++) {
			if(foods[i].getCalories() <= maxCalories)
				return true;
		}
		
		return false;
	}
	
	
	//This checks to see if there is at least one food in the entire database that will meet the calorie limit.
	private  static boolean exists(int maxCalories, Food[][] db) {
		for (int i = 0; i < db.length; i++) {
			if(exists(maxCalories, db[i]))
				return true;
		}
		
		return false;
	}
	
	
	
	
	// This is the actual database for the food. 
	public static Food[][] createDatabase() {
		Food[][] foods = new Food[CATEGORIES.length][];
		
		int index = 0;
		foods[0] = new Food[8];
		foods[0][index++] = new Food("Pork", CATEGORIES[0], "3 ounces", 206);
		foods[0][index++] = new Food("Beef", CATEGORIES[0], "3 ounces", 213);
		foods[0][index++] = new Food("Lamb", CATEGORIES[0], "3 ounces", 250);
		foods[0][index++] = new Food("Chicken", CATEGORIES[0], "3 ounces", 335);
		foods[0][index++] = new Food("Turkey", CATEGORIES[0], "6 ounces", 324);
		foods[0][index++] = new Food("Venison", CATEGORIES[0], "6 ounces", 268);
		foods[0][index++] = new Food("Bison", CATEGORIES[0], "7 ounces", 286);
		foods[0][index++] = new Food("Duck", CATEGORIES[0], "Half Cup", 236);
		
		index = 0;
		foods[1] = new Food[10];
		foods[1][index++] = new Food("Apple", CATEGORIES[1], "1", 95);
		foods[1][index++] = new Food("Grapes", CATEGORIES[1], "30", 101);
		foods[1][index++] = new Food("Orange", CATEGORIES[1], "1.5", 92);
		foods[1][index++] = new Food("Blueberries", CATEGORIES[1], "1.25", 84);
		foods[1][index++] = new Food("Cantaloupe", CATEGORIES[1], "1", 63);
		foods[1][index++] = new Food("Grapefruit", CATEGORIES[1], "1", 103);
		foods[1][index++] = new Food("Mango", CATEGORIES[1], "1", 99);
		foods[1][index++] = new Food("Peach", CATEGORIES[1], "1", 51);
		foods[1][index++] = new Food("Pear", CATEGORIES[1], "1", 101);
		foods[1][index++] = new Food("Pomegranate", CATEGORIES[1], "1 cup", 144);
		
		index = 0;
		foods[2] = new Food[10];
		foods[2][index++] = new Food("Broccoli", CATEGORIES[2], "1 Bunch", 207);
		foods[2][index++] = new Food("Cabbage", CATEGORIES[2], "1", 227);
		foods[2][index++] = new Food("Corn", CATEGORIES[2], "1 Cup", 562);
		foods[2][index++] = new Food("Creamed Spinach", CATEGORIES[2], "1 Cup", 148);
		foods[2][index++] = new Food("Parsnip", CATEGORIES[2], "1", 128);
		foods[2][index++] = new Food("Peas", CATEGORIES[2], "1 Cup", 79);
		foods[2][index++] = new Food("Potato", CATEGORIES[2], "1", 164);
		foods[2][index++] = new Food("Sweet Potato", CATEGORIES[2], "1", 112);
		foods[2][index++] = new Food("Squash", CATEGORIES[2], "1", 88);
		foods[2][index++] = new Food("Leek", CATEGORIES[2], "1", 54);
		
		index = 0;
		foods[3] = new Food[10];
		foods[3][index++] = new Food("Amaranth", CATEGORIES[3], "Half Cup", 126);
		foods[3][index++] = new Food("Barley", CATEGORIES[3], "Half Cup", 97);
		foods[3][index++] = new Food("Brown Rice", CATEGORIES[3], "Half Cup", 109);
		foods[3][index++] = new Food("Oats", CATEGORIES[3], "Half Cup", 83);
		foods[3][index++] = new Food("Quinoa", CATEGORIES[3], "Half Cup", 111);
		foods[3][index++] = new Food("Spelt", CATEGORIES[3], "Half Cup", 123);
		foods[3][index++] = new Food("Wild Rice", CATEGORIES[3], "Half Cup", 83);
		foods[3][index++] = new Food("Rye Bread", CATEGORIES[3], "2 Slices", 166);
		foods[3][index++] = new Food("Whole Wheat Spaghetti", CATEGORIES[3], "Half Cup", 100);
		foods[3][index++] = new Food("Wheat Bread", CATEGORIES[3], "2 Slices", 140);
		
		index = 0;
		foods[4] = new Food[10];
		foods[4][index++] = new Food("Almond Milk", CATEGORIES[4], "1 Cup", 40);
		foods[4][index++] = new Food("Chocolate Mousse", CATEGORIES[4], "Half Cup", 455);
		foods[4][index++] = new Food("Cottage Cheese", CATEGORIES[4], "1 Cup", 206);
		foods[4][index++] = new Food("Hot Chocolate", CATEGORIES[4], "1 Cup", 237);
		foods[4][index++] = new Food("Milk", CATEGORIES[4], "1 Cup", 149);
		foods[4][index++] = new Food("Plain Yogurt", CATEGORIES[4], "1 Cup", 138);
		foods[4][index++] = new Food("Soy Milk", CATEGORIES[4], "1 Cup", 109);
		foods[4][index++] = new Food("Whole Milk", CATEGORIES[4], "1 Cup", 149);
		foods[4][index++] = new Food("Yogurt", CATEGORIES[4], "1 Cup", 138);
		foods[4][index++] = new Food("Custard", CATEGORIES[4], "Half Cup", 172);
		
		return foods;
	}
	
	
	
	
	
	
}
