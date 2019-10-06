import java.io.*;
import java.util.Arrays;

public class Test {
	public static void main(String[] args) throws IOException {
		File fromFile = new File("test.txt");
		File toFile = new File("test_out.txt");
		BufferedReader br = new BufferedReader(new FileReader(fromFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(toFile));
		String [] in;
		String s;
		for(int i = 1; i <= 5; ++i) {
			in = br.readLine().split(" ");
			System.out.println(Arrays.toString(in));
			for(int j = 1; j <= 5; ++j) {
				s = in[j-1];
				if(s.equals("B")) {
					bw.write("B "+ j +" " + i + "\n");
				}
				else {
					bw.write("H "+ j +" " + i + "\n");
				}
			}
		}
		br.close();
		bw.close();
	}	
}
