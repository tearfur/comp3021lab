import java.util.ArrayList;

interface Miner {
	Integer mine(String accName);
}


public interface Bank {
	boolean addAccount(String accountID, Integer initBalance);
	
	boolean deposit(String accountID, Integer amount);
	
	boolean withdraw(String accountID, Integer amount, long timeoutMillis);
	
	boolean transfer(String srcAccount,
			                String dstAccount, 
			                Integer amount, 
			                long timeoutMillis);
	
	Integer getBalance(String accountID);
	
	void doLottery(ArrayList<String> accounts, Miner miner);
}
