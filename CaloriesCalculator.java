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
			//equalsIgnoreCase compares two strings while ignoring if it is capitalized or lower case.
			System.out.print("Enter gender ('m' for male and 'f' for female): ");
			boolean male = scanner.next().equalsIgnoreCase("m");
			
			// read target
			System.out.print("Enter target (1. Lose Weight 2. Maintain Weight 3. Gain Weight): ");
			int target = scanner.nextInt();
			
			
			// calculate calories and per meal limit
			int maxCalories = getMaximumCalories(male, target);
			int perMealLimit = Math.floorDiv(maxCalories, MEALS.length);
			
			// get full day meal and divide to meals
			ArrayList<Food> meal = getFullDayMeal(maxCalories, db);
			ArrayList<Food>[] meals = divideToMeals(meal, perMealLimit);
			
			
			// print results
			System.out.printf("\nMaximum Calories: %d, Per Meal Limit: %d\n", maxCalories , perMealLimit);
			for (int j = 0; j < meals.length; j++) {
				System.out.printf("\n%s [%d calories]:\n", MEALS[j], totalCalories(meals[j]));
				for (Food food : meals[j]) {
					System.out.println(food);
				}
			}
			
			
			// ask if user wnats to exit
			System.out.print("\nDo you want to exit? [y/N]: ");
			if(scanner.next().equalsIgnoreCase("y"))
				break;
			
			System.out.println();
		}
		
		
		scanner.close();
		
		
		
		
		
	}
	
	
	// get maximum calories based on geneder and target
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
	
	
	
	// divide a full day meal to meals
	private static ArrayList<Food>[] divideToMeals(ArrayList<Food> fullDayMeal, int perMealMaxCalories) {
		ArrayList<Food>[] meals = new ArrayList[MEALS.length]; // to sotre meals
		
		
		// create array to keep track of remianing calories for the meal
		int[] remainingCalories = new int[MEALS.length];
		for (int i = 0; i < remainingCalories.length; i++) {
			remainingCalories[i] = perMealMaxCalories;
			meals[i] = new ArrayList<>();
		}
		
		
	
		
		while(!fullDayMeal.isEmpty()) {
			// remove first food
			Food food = fullDayMeal.remove(0);
			
			if(canAddToMeal(food.getCalories(), remainingCalories)) { // check if can be added
				boolean added = false;
				
				// if any meal is empty, add to that so that no meal rem,ains empty at end
				for (int i = 0; i < meals.length; i++) {
					if(meals[i].isEmpty()) {
						meals[i].add(food);
						remainingCalories[i] -= food.getCalories();
						added = true;
						break;
					}
				}
				
				
				// if no meal was empt
				while(!added) {
					// find a random meal where it can be added
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
	
	
	// check if there is a meal which can accomodate given calories
	private  static boolean canAddToMeal(int calories, int[] remainingCalories) {
		for (int i = 0; i < remainingCalories.length; i++) {
			if(calories <= remainingCalories[i])
				return true;
		}
		
		return false;
	}
	
	
	// calculate total calories in a food
	private static int totalCalories(ArrayList<Food> meal) {
		int total = 0;
		for (Food food : meal) {
			total += food.getCalories();
		}
		
		return total;
	}
	
	// get full day meal
	private static ArrayList<Food> getFullDayMeal(int caloriesLimit, Food[][] db) {
		while(true) {
			ArrayList<Food> fullDayMeal = randomizeFullDayMeal(caloriesLimit, db);
			if(fullDayMeal != null)
				return fullDayMeal;
		}
	}
	
	
	// randomizes full day meal to meet the target
	// if any step fail, it returns null
	private  static ArrayList<Food> randomizeFullDayMeal(int caloriesLimit, Food[][] db) {
		
		ArrayList<Food> foods = new ArrayList<>();
		int remainingLimit = caloriesLimit;
		
		// add atleats one food of each category
		for (int i = 0; i < CATEGORIES.length; i++) {
			Food food = findFood(remainingLimit, db[i]);
			
			if(food == null)
				return null;
			
			foods.add(food);
			remainingLimit -= food.getCalories();
		}
		
		
		// add more foods so that the total claories is as close to the limit
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
	
	// find a food in a categpry of food that meets the target
	private  static Food findFood(int maxCalories, Food[] foods) {
		if(!exists(maxCalories, foods)) // if no such food exists
			return null;
		
		// find a random food and add
		while(true) {
			int index = random.nextInt(foods.length);
			if(foods[index].getCalories() <= maxCalories)
				return foods[index];
		}
	}
	
	
	// check if there ecists atleast one food that meets the calories limit
	private  static boolean exists(int maxCalories, Food[] foods) {
		for (int i = 0; i < foods.length; i++) {
			if(foods[i].getCalories() <= maxCalories)
				return true;
		}
		
		return false;
	}
	
	
	// check if there is atleast one food in whole database that meets the calories limit
	private  static boolean exists(int maxCalories, Food[][] db) {
		for (int i = 0; i < db.length; i++) {
			if(exists(maxCalories, db[i]))
				return true;
		}
		
		return false;
	}
	
	
	
	
	// create database of food
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
