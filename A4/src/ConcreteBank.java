import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcreteBank implements Bank {
	final private HashMap<String, AtomicInteger> accounts;

	public ConcreteBank() {
		accounts = new HashMap<>();
	}

	@Override
	public boolean addAccount(String accountID, Integer initBalance) {
		synchronized (accounts) {
			return accounts.putIfAbsent(accountID, new AtomicInteger(initBalance)) == null;
		}
	}

	@Override
	public boolean deposit(String accountID, Integer amount) {
		AtomicInteger val = accounts.get(accountID);
		if (val == null)
			return false;
		synchronized (val) {
			val.addAndGet(amount);
			val.notifyAll();
		}
		return true;
	}

	@Override
	public boolean withdraw(String accountID, Integer amount, long timeoutMillis) {
		AtomicInteger val = accounts.get(accountID);
		if (val == null)
			return false;

		synchronized (val) {
			if (val.get() < amount) {
				try {
					val.wait(timeoutMillis);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}

			if (val.get() < amount)
				return false;

			val.addAndGet(-amount);
			return true;
		}
	}

	@Override
	public boolean transfer(String srcAccount, String dstAccount, Integer amount, long timeoutMillis) {
		if (!accounts.containsKey(srcAccount) || !accounts.containsKey(dstAccount))
			return false;
		if (!withdraw(srcAccount, amount, timeoutMillis))
			return false;
		return deposit(dstAccount, amount);
	}

	@Override
	public Integer getBalance(String accountID) {
		try {
			return accounts.get(accountID).get();
		} catch (NullPointerException ex) {
			return 0;
		}
	}

	@Override
	public void doLottery(ArrayList<String> accounts, Miner miner) {
		accounts.parallelStream().forEach(accountID -> deposit(accountID, miner.mine(accountID)));
	}
}
