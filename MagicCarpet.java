import java.io.*;

public class MagicCarpet {
	private static final int NO_SOLUTION = -1;
	private static final int UPPER_LEFT = 0;
	private static final int UPPER_RIGHT = 1;
	private static final int LOWER_LEFT = 2;
	private static final int LOWER_RIGHT = 3;
	private static final int MIDDLE = 4;
	
	private int n;
	private int b;
	private int h;
	private long quarterSize;
	private boolean even;
	private boolean middleEmpty = true;
	private long[] boxesInQuarter = new long[4];
	private long[] huntersInQuarter = new long[4];
	private long[] vacantInQuarter = new long[4];
	
	public static void main(String[] args) throws IOException {
		for(int j = 1; j <= 2; ++j) {
			File fromFile = new File("task-" + j + ".txt");
			File toFile = new File("task-" + j + "_out.txt");
			BufferedReader br = new BufferedReader(new FileReader(fromFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(toFile));
		
			int tests = Integer.parseInt(br.readLine());
			for(int i = 1; i <= tests; ++i) {
				long huntersAdded = findSolution(br);
				writeToFile(bw, i, tests, huntersAdded);
			}
			br.close();
			bw.close();
		}
	}
	
	public static long findSolution(BufferedReader br) throws IOException {
		MagicCarpet carpet = buildCarpet(br);
		long result = carpet.result();
		int middle = (carpet.isMiddleEmpty() && !carpet.isEven()) ? 1 : 0;
		return result == NO_SOLUTION ? result : result + middle;
	}
	
	public static MagicCarpet buildCarpet(BufferedReader br) throws IOException {
		String[] input;
		int n, b, h;
		input = br.readLine().split(" ");
		n = Integer.parseInt(input[0]);
		b = Integer.parseInt(input[1]);
		h = Integer.parseInt(input[2]);
		MagicCarpet carpet = new MagicCarpet(n, b, h);
		readInput(br, carpet);
		carpet.calcVacant();
		return carpet;
	}
	
	public static void readInput(BufferedReader br, MagicCarpet carpet) throws IOException {
		int x,y;
		int b = carpet.getAmountOfBoxes();
		int h = carpet.getAmountOfHunters();
		String[] input;
		for(int i = 0; i < b + h; ++i) {
			boolean isBox = i < b;
			input = br.readLine().split(" ");
			x = Integer.parseInt(input[0]) - 1;
			y = Integer.parseInt(input[1]) - 1;
			carpet.classifyToQuarters(isBox, x, y);
		}
	}
	
	public static void writeToFile(BufferedWriter bw, int i, int tests, long huntersAdded) throws IOException {
    	if(i != tests) {
    		bw.write("Case #" + i + ": " + huntersAdded + "\n");
    	}
    	else {
    		bw.write("Case #" + i + ": " + huntersAdded);
    	}
	}
	
	public MagicCarpet(int n, int b, int h) {
		this.n = n;
		this.b = b;
		this.h= h;
		this.quarterSize = n/2;
		this.even = n % 2 == 0;
	}

	public int getAmountOfBoxes() {
		return this.b;
	}
	
	public int getAmountOfHunters() {
		return this.h;
	}
	
	public boolean isEven() {
		return this.even;
	}
	
	public boolean isMiddleEmpty() {
		return this.middleEmpty;
	}
	
	public void calcVacant() {
		long quarterArea = (long)Math.pow((this.quarterSize), 2);
		long addIfOdd = this.even ? 0 : n/2;
		for(int i = 0; i < 4; ++i) {
			this.vacantInQuarter[i] = quarterArea + addIfOdd - this.huntersInQuarter[i] - this.boxesInQuarter[i];
		}
	}
	
	public void classifyToQuarters (boolean isBox, int x, int y) {
		int q = this.findQuarter(x,y);
		if(q == MIDDLE) {
			this.middleEmpty = false;
		}
		else if(isBox) {
			++this.boxesInQuarter[q];
		}
		else {
			++this.huntersInQuarter[q];
		}
	}
	
	public int findQuarter(int x, int y) {
		if(this.even) {
			return findQuarterEven(x, y);
		}
		return findQuarterOdd(x, y);
	}
		
	public int findQuarterEven(int x, int y) {	
		if(y < this.quarterSize && x < this.quarterSize) {
			return UPPER_LEFT;
		}
		else if(y < this.quarterSize && x >= this.quarterSize) {
			return UPPER_RIGHT;
		}
		else if(y >= this.quarterSize && x < this.quarterSize) {
			return LOWER_LEFT;
		}
		else {
			return LOWER_RIGHT;
		}
	}
	
	public int findQuarterOdd(int x, int y) {		
		if(y < this.quarterSize && x <= this.quarterSize) {
			return UPPER_LEFT;
		}
		else if(y <= this.quarterSize && x > this.quarterSize) {
			return UPPER_RIGHT;
		}
		else if(y >= this.quarterSize && x < this.quarterSize) {
			return LOWER_LEFT;
		}
		else if(y > this.quarterSize && x >= this.quarterSize) {
			return LOWER_RIGHT;
		}
		else {
			return MIDDLE;
		}
	}
	
	public long result() {
		long huntersAdded = this.balanceCarpet();
		if(huntersAdded == NO_SOLUTION) {
			return NO_SOLUTION;
		}
		return huntersAdded + this.maximizeHunters();
	}
	
	public long balanceCarpet() {
		long huntersDiffInQuarter1 = balanceOppositeQuarters(UPPER_LEFT, LOWER_RIGHT);
		long huntersDiffInQuarter2 = balanceOppositeQuarters(UPPER_RIGHT, LOWER_LEFT);
		if(huntersDiffInQuarter1 == NO_SOLUTION || huntersDiffInQuarter2 == NO_SOLUTION) {
			return NO_SOLUTION;
		}
		return huntersDiffInQuarter1 + huntersDiffInQuarter2;
	}
	
	public long balanceOppositeQuarters(int quarter1, int quarter2) {
		int addToQuarter = this.findLighterOppositeQuarter(quarter1, quarter2);
		long huntersToAdd = this.findWeightDiff(quarter1, quarter2);
		if(huntersToAdd == 0) {
			return 0;
		}
		if(huntersToAdd > this.vacantInQuarter[addToQuarter]) {
			return NO_SOLUTION;
		}
		else {
			this.addHuntersToBalance(addToQuarter, huntersToAdd);
			return huntersToAdd;
		}
	}
	
	public int findLighterOppositeQuarter(int quarter1, int quarter2) {
		long huntersDiff1 = this.huntersInQuarter[quarter1] - this.huntersInQuarter[quarter2];
		long huntersDiff2 = this.huntersInQuarter[quarter2] - this.huntersInQuarter[quarter1];
		if(huntersDiff1 > huntersDiff2) {
			return quarter2;
		}
		else {
			return quarter1;
		}
	}
	
	public long findWeightDiff(int quarter1, int quarter2) {
		return Math.abs(this.huntersInQuarter[quarter1] - this.huntersInQuarter[quarter2]);
	}
	
	public void addHuntersToBalance(int addToQuarter, long huntersToAdd) {
		this.huntersInQuarter[addToQuarter] += huntersToAdd;
		this.vacantInQuarter[addToQuarter] -= huntersToAdd;
	}
	
	public long maximizeHunters() {
		long max1 = this.maxHuntersInOppositeQuarters(UPPER_LEFT, LOWER_RIGHT);
		long max2 = this.maxHuntersInOppositeQuarters(UPPER_RIGHT, LOWER_LEFT);
		return 2*(max1 + max2);
	}
	
	public long maxHuntersInOppositeQuarters(int quarter1, int quarter2) {
		return (long)Math.min(this.vacantInQuarter[quarter1], this.vacantInQuarter[quarter2]);
	}
	
	public int Test(int i, long result) {
		long up = huntersInQuarter[UPPER_LEFT] + huntersInQuarter[UPPER_RIGHT];
		long down = huntersInQuarter[LOWER_LEFT] + huntersInQuarter[LOWER_RIGHT];
		long left = huntersInQuarter[UPPER_LEFT] + huntersInQuarter[LOWER_LEFT];
		long right = huntersInQuarter[LOWER_RIGHT] + huntersInQuarter[UPPER_RIGHT];
		boolean test = up == down && left == right;
		if(result == NO_SOLUTION && !test) {
			System.out.println("Case #" + i + ": " + true); 
		}
		else {
			System.out.println("Case #" + i + ": " + test); 
		}
		return up == down && left == right ? 0 : 1;
	}
}
