import java.util.HashMap;

public class Hospital {
	public final String HospitalID;
	private final Location loc;
	private final Capacity cap;
	private final HashMap<String, Patient> Patients;
	static int numNewHospitals = 0;

	/* Create the hospital according to the input file */
	public Hospital(String p_HospitalID, Location p_loc, Capacity p_cap) {
		this.HospitalID = p_HospitalID;
		this.loc = p_loc;
		this.cap = p_cap;
		this.Patients = new HashMap<>();
	}

	/* Create new hospital */
	public Hospital(Location p_loc) {
		this("H-New-" + ++numNewHospitals, p_loc, new Capacity(5, 10, 20));
	}

	/* Get the location of the hospital */
	public Location getLoc() {
		return this.loc;
	}

	/* Whether this hospital can treat this patient */
	public boolean accept(Patient patient) {
		return cap.getSingleCapacity(patient.getSymptomLevel()) > 0;
	}

	/* Add a patient to the corresponding patient list */
	public boolean addPatient(Patient patient) {
		if (!cap.decreaseCapacity(patient.getSymptomLevel()))
			return false;

		patient.setHospitalID(HospitalID);
		Patients.put(patient.IDCardNo, patient);

		return true;
	}

	/* Remove a patient from the corresponding patient list */
	public boolean releasePatient(String id) {
		if (!Patients.containsKey(id))
			return false;

		cap.increaseCapacity(Patients.get(id).getSymptomLevel());

		return Patients.remove(id) != null;
	}

	/* Dump the hospital info as a string */
	public String toString() {
		String str = "";
		str += HospitalID + "        ";
		str += loc.xloc + "        ";
		str += loc.yloc + "        ";
		str += cap.CriticalCapacity + "        ";
		str += cap.ModerateCapacity + "        ";
		str += cap.MildCapacity;
		return str;
	}
}
