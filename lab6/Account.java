package lab6;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class Account {
	public static Consumer<Account> add100 = a -> a.balance += 100;

	public static Predicate<Account> lowerBound = a -> a.balance >= 0;
	public static Predicate<Account> upperBound = a -> a.balance <= 10000;
	public static Predicate<Account> checkBound = a -> lowerBound.test(a) && upperBound.test(a);

	public static AddMaker maker = N -> (a -> a.balance += N);

	public int id;
	public int balance;

	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
	}

	// You can assume that all the Account in acconts have positive balances.
	public static int getMaxAccountID(List<Account> accounts) {
		Account maxOne = accounts.stream().reduce(new Account(0, -100), (a, b) -> a.balance > b.balance ? a : b);

		return maxOne.id;
	}


	interface AddMaker {
		Consumer<Account> make(int N);
	}
}
