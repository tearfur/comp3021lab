import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class QuarantineSystem {
	public static class DashBoard {
		HashMap<String, Person> People;
		int[] patientNums;
		double[] infectAvgNums;
		int[] vacNums;
		int[] vacInfectNums;

		public DashBoard(HashMap<String, Person> p_People) {
			this.People = p_People;
			this.patientNums = new int[8];
			this.infectAvgNums = new double[8];
			this.vacNums = new int[8];
			this.vacInfectNums = new int[8];
		}

		public void runDashBoard() {
			int[] infectNums = new int[8];

			for (String key : People.keySet()) {
				Person p = People.get(key);
				int ix = Math.min(p.getAge() / 10, 7);

				infectNums[ix] += p.getInfectCnt();
				if (p.getInfectCnt() > 0)
					++patientNums[ix];
				if (p.getIsVac())
					++vacNums[ix];
				if (p.getInfectCnt() > 0 && p.getIsVac())
					++vacInfectNums[ix];
			}

			for (int i = 0; i < 8; ++i)
				infectAvgNums[i] = patientNums[i] != 0 ? 1. * infectNums[i] / patientNums[i] : 0;
		}
	}


	private HashMap<String, Person> People;
	private List<Record> Records;
	private HashMap<String, Hospital> Hospitals;

	// Intermediate storage only
	private final HashMap<String, Patient> Patients;

	private DashBoard dashBoard;

	public QuarantineSystem() throws IOException {
		importPeople();
		importHospital();
		importRecords();
		dashBoard = null;
		Patients = new HashMap<>();
	}

	public void startQuarantine() throws IOException {
		/*
		 * Task 1: Saving Patients
		 */
		System.out.println("Task 1: Saving Patients");
		for (Record r : Records) {
			saveSinglePatient(r);
			releaseSinglePatient(r);
		}
		exportRecordTreatment();

		/*
		 * Task 2: Displaying Statistics
		 */
		System.out.println("Task 2: Displaying Statistics");
		dashBoard = new DashBoard(this.People);
		dashBoard.runDashBoard();
		exportDashBoard();
	}

	/*
	 * Save a single patient when the status of the record is Confirmed
	 */
	public void saveSinglePatient(Record record) {
		if (record.getStatus() != Status.Confirmed)
			return;

		Person person = People.get(record.getIDCardNo());
		Patient p = new Patient(person, record.getSymptomLevel());
		Location l = person.getLoc();
		Hospital h = null;

		for (String key : Hospitals.keySet()) {
			Hospital tmp = Hospitals.get(key);
			if (tmp.accept(p) && (h == null || tmp.getLoc().getDisSquare(l) < h.getLoc().getDisSquare(l)))
				h = tmp;
		}

		if (h == null) {
			h = new Hospital(l);
			Hospitals.put(h.HospitalID, h);
		}

		h.addPatient(p);
		Patients.put(p.IDCardNo, p);
		record.setHospitalID(h.HospitalID);
		person.infected();
	}

	/*
	 * Release a single patient when the status of the record is Recovered
	 */
	public void releaseSinglePatient(Record record) {
		if (record.getStatus() != Status.Recovered)
			return;

		String patientID = record.getIDCardNo();
		String hospitalID = Patients.remove(patientID).getHospitalID();
		Hospitals.get(hospitalID).releasePatient(patientID);
		record.setHospitalID(hospitalID);
	}

	/*
	 * Import the information of the people in the area from Person.txt
	 * The data is finally stored in the attribute People
	 * You do not need to change the method.
	 */
	public void importPeople() throws IOException {
		this.People = new HashMap<>();
		File filename = new File("data/Person.txt");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		int lineNum = 0;

		while (line != null) {
			lineNum++;
			if (lineNum > 1) {
				String[] records = line.split("        ");
				assert (records.length == 6);
				String pIDCardNo = records[0];
				System.out.println(pIDCardNo);
				int XLoc = Integer.parseInt(records[1]);
				int YLoc = Integer.parseInt(records[2]);
				Location pLoc = new Location(XLoc, YLoc);
				assert (records[3].equals("Male") || records[3].equals("Female"));
				String pGender = records[3];
				int pAge = Integer.parseInt(records[4]);
				assert (records[5].equals("Yes") || records[5].equals("No"));
				boolean pIsVac = (records[5].equals("Yes"));
				Person p = new Person(pIDCardNo, pLoc, pGender, pAge, pIsVac);
				this.People.put(p.IDCardNo, p);
			}
			line = br.readLine();
		}
	}

	/*
	 * Import the information of the records
	 * The data is finally stored in the attribute Records
	 * You do not need to change the method.
	 */
	public void importRecords() throws IOException {
		this.Records = new ArrayList<>();

		File filename = new File("data/Record.txt");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		int lineNum = 0;

		while (line != null) {
			lineNum++;
			if (lineNum > 1) {
				String[] records = line.split("        ");
				assert(records.length == 3);
				String pIDCardNo = records[0];
				System.out.println(pIDCardNo);
				assert(records[1].equals("Critical") || records[1].equals("Moderate") || records[1].equals("Mild"));
				assert(records[2].equals("Confirmed") || records[2].equals("Recovered"));
				Record r = new Record(pIDCardNo, records[1], records[2]);
				Records.add(r);
			}
			line = br.readLine();
		}
	}

	/*
	 * Import the information of the hospitals
	 * The data is finally stored in the attribute Hospitals
	 * You do not need to change the method.
	 */
	public void importHospital() throws IOException {
		this.Hospitals = new HashMap<>();

		File filename = new File("data/Hospital.txt");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
		BufferedReader br = new BufferedReader(reader);
		String line = br.readLine();
		int lineNum = 0;

		while (line != null) {
			lineNum++;
			if (lineNum > 1) {
				String[] records = line.split("        ");
				assert(records.length == 6);
				String pHospitalID = records[0];
				System.out.println(pHospitalID);
				int XLoc = Integer.parseInt(records[1]);
				int YLoc = Integer.parseInt(records[2]);
				Location pLoc = new Location(XLoc, YLoc);
				int pCritialCapacity = Integer.parseInt(records[3]);
				int pModerateCapacity = Integer.parseInt(records[4]);
				int pMildCapacity = Integer.parseInt(records[5]);
				Capacity cap = new Capacity(pCritialCapacity, pModerateCapacity, pMildCapacity);
				Hospital hospital = new Hospital(pHospitalID, pLoc, cap);
				this.Hospitals.put(hospital.HospitalID, hospital);
			}
			line = br.readLine();
		}
	}

	/*
	 * Export the information of the records
	 * The data is finally dumped into RecordTreatment.txt
	 * DO NOT change the functionality of the method
	 * Otherwise, you may generate wrong results in Task 1
	 */
	public void exportRecordTreatment() throws IOException {
		File filename = new File("output/RecordTreatment.txt");
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename));
		BufferedWriter bw = new BufferedWriter(writer);
		bw.write("IDCardNo        SymptomLevel        Status        HospitalID\n");
		for (Record entry : Records) {
			//Invoke the toString method of Record.
			bw.write(entry.toString() + "\n");
		}
		bw.close();
	}

	/*
	 * Export the information of the dashboard
	 * The data is finally dumped into Statistics.txt
	 * DO NOT change the functionality of the method
	 * Otherwise, you may generate wrong results in Task 2
	 */
	public void exportDashBoard() throws IOException {
		File filename = new File("output/Statistics.txt");
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename));
		BufferedWriter bw = new BufferedWriter(writer);

		bw.write("AgeRange        patientNums        infectAvgNums        vacNums        vacInfectNums\n");

		for (int i = 0; i < 8; i++) {
			String ageRageStr = "";
			switch (i) {
				case 0:
					ageRageStr = "(0, 10)";
					break;
				case 7:
					ageRageStr = "[70, infinite)";
					break;
				default:
					ageRageStr = "[" + String.valueOf(i) + "0, " + String.valueOf(i + 1) + "0)";
					break;
			}
			String patientNumStr = String.valueOf(dashBoard.patientNums[i]);
			String infectAvgNumsStr = String.valueOf(dashBoard.infectAvgNums[i]);
			String vacNumsStr = String.valueOf(dashBoard.vacNums[i]);
			String vacInfectNumsStr = String.valueOf(dashBoard.vacInfectNums[i]);

			bw.write(ageRageStr + "        " + patientNumStr + "        " + infectAvgNumsStr + "        " + vacNumsStr + "        " + vacInfectNumsStr + "\n");
		}

		bw.close();
	}

	/* The entry of the project */
	public static void main(String[] args) throws IOException {
		QuarantineSystem system = new QuarantineSystem();
		System.out.println("Start Quarantine System");
		system.startQuarantine();
		System.out.println("Quarantine Finished");
	}
}
