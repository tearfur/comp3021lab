package lab9;

import java.util.Arrays;

/**
 * COMP 3021
 *
 * This is a class that prints the maximum value of a given array of 90 elements
 *
 * This is a single threaded version.
 *
 * Create a multi-thread version with 3 threads:
 *
 * one thread finds the max among the cells [0,29]
 * another thread the max among the cells [30,59]
 * another thread the max among the cells [60,89]
 *
 * Compare the results of the three threads and print at console the max value.
 *
 * @author valerio
 */
public class FindMax {
	// this is an array of 90 elements
	// the max value of this array is 9999
	static final int[] array = {
			1, 34, 5, 6, 343, 5, 63, 5, 34, 2, 78, 2, 3, 4, 5, 234, 678, 543, 45, 67, 43, 2, 3, 4543, 234, 3, 454, 1, 2,
			3, 1, 9999, 34, 5, 6, 343, 5, 63, 5, 34, 2, 78, 2, 3, 4, 5, 234, 678, 543, 45, 67, 43, 2, 3, 4543, 234, 3,
			454, 1, 2, 3, 1, 34, 5, 6, 5, 63, 5, 34, 2, 78, 2, 3, 4, 5, 234, 678, 543, 45, 67, 43, 2, 3, 4543, 234, 3,
			454, 1, 2, 3
	};

	public static void main(String[] args) {
		printMaxParallel();
	}

	public static void printMax() {
		// this is a single threaded version
		int max = findMax(0, array.length - 1);
		System.out.println("the max value is " + max);
	}

	public static void printMaxParallel() {
		SubTask[] tasks = {new SubTask(0, 29), new SubTask(30, 59), new SubTask(60, 89)};
		Thread[] threads = Arrays.stream(tasks).map(Thread::new).toArray(Thread[]::new);

		for (Thread t : threads)
			t.start();
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		System.out.println(
				"the max value is " + Arrays.stream(tasks).map(SubTask::getValue).max(Integer::compare).orElse(0));
	}

	/**
	 * returns the max value in the array within a give range [begin,range]
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	private static int findMax(int begin, int end) {
		// you should NOT change this function
		int max = array[begin];
		for (int i = begin + 1; i <= end; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}
		return max;
	}

	private static class SubTask implements Runnable {
		private final int begin, end;
		private volatile int max;

		public SubTask(int begin, int end) {
			this.begin = begin;
			this.end = end;
		}

		@Override
		public void run() {
			max = findMax(begin, end);
		}

		public int getValue() {
			return max;
		}
	}
}
