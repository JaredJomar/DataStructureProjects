package police;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import criminals.Organization;
import criminals.Member;
import interfaces.List;
import lists.ArrayList;

public class PoliceDepartment {

	/**
	 * Creates an instance of PoliceDepartment. It receives the name of the Captain
	 * as a parameter.
	 *
	 * @param captain - (String) Name of the captain of the Police Department
	 */

	// Define a String variable called "captain" to store the name of the captain
	String captain;

	// Define an int variable called "organizationPosition" to keep track of the
	// organization position
	int organizationPosition = 0;

	// Define an ArrayList variable called "criminalOrganizations" to contain
	// objects of the "Organization" class
	List<Organization> criminalOrganizations = new ArrayList<>();

	// Define an int variable called "arrest" to keep track of the number of arrests
	// made by the police department
	int arrest;

	// Constructor for the "PoliceDepartment" class that takes a "captain" parameter
	public PoliceDepartment(String captain) {
		// Set the "captain" member variable to the value of the "captain" parameter
		this.captain = captain;
	}

	/**
	 * Returns the List of Criminal Organizations that the Police Department has on
	 * record.
	 */
	// Getter method for the criminal organizations list
	public List<Organization> getCriminalOrganizations() {
		// Return the value of the "criminalOrganizations" member variable
		return this.criminalOrganizations;
	}

	/**
	 * Does the setup of the criminal organizations List.
	 *
	 * This method will read each organization file in the Criminal Organization
	 * folder and and add each one to the criminal organization List.
	 *
	 * NOTE: The order the files are read is important. The files should be read in
	 * alphabetical order.
	 *
	 * @param caseFolder - (String) Path of the folder containing the criminal
	 *                   organization files.
	 * @throws IOException
	 */
	// This method sets up the list of criminal organizations for the police
	// department
	public void setUpOrganizations(String caseFolder) throws IOException {
		// Construct the path to the folder containing the criminal organization files
		String path = caseFolder + File.separator + "CriminalOrganizations";
		// Create a File object for the folder
		File folder = new File(path);
		// Get an array of File objects for all files in the folder
		File[] listOfFiles = folder.listFiles();
		// Sort the list of files in alphabetical order
		Arrays.sort(listOfFiles);
		// Loop through each file in the array
		for (File file : listOfFiles) {
			// Check if the file is actually a file (not a directory)
			if (file.isFile()) {
				// Get the name of the file
				String name = file.getName();
				// Create an Organization object using the file path
				Organization organization = new Organization(path + File.separator + name);
				// Add the Organization object to the list of criminal organizations
				this.criminalOrganizations.add(organization);
			}
		}
	}

	/**
	 * Receives the path to the message and deciphers the name of the leader of the
	 * operation. This also identifies the index of the current organization we are
	 * processing.
	 * 
	 * @param caseFolder - (String) Path to the folder containing the flyer that has
	 *                   the hidden message.
	 * @return The name of the leader of the criminal organization.
	 * @throws FileNotFoundException If the file cannot be found.
	 * @throws IOException           If there is an error reading the file.
	 */

	// Method to decipher a message from a file
	public String decipherMessage(String caseFolder) throws IOException {
		// Read the message file line by line into a list
		List<String> messageDeciphered = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(caseFolder))) {
			String line;
			while ((line = reader.readLine()) != null) {
				messageDeciphered.add(line);
			}
		}

		// Determine the organization and leader key
		this.organizationPosition = getDigiroot(messageDeciphered.get(0)) - 1;
		Organization organization = criminalOrganizations.get(organizationPosition);
		int leader = organization.getLeaderKey();

		// Extract the leader name from the message
		char[] leaderNameChars = new char[messageDeciphered.size() - 4];
		for (int i = 2; i < messageDeciphered.size() - 2; i++) {
			String[] message = messageDeciphered.get(i).split("\\W+");
			if (message.length < leader) {
				leaderNameChars[i - 2] = ' ';
			} else {
				leaderNameChars[i - 2] = message[leader - 1].charAt(0);
			}
		}

		// Convert the leader name to a string and return it
		return new String(leaderNameChars);
	}

	/**
	 * Calculates the digital root (digiroot) of the number received. The number is
	 * received as a String since it makes processing each individual number a bit
	 * easier.
	 *
	 * @param numbers - The string representation of the number whose digital root
	 *                is to be found.
	 * @return - The digital root of the number.
	 */
	public int getDigiroot(String numbers) {
		// Remove the "#" symbol from the input string
		numbers = numbers.replaceAll("#", "");

		int sum = 0;
		for (int i = 0; i < numbers.length(); i++) {
			sum += Character.getNumericValue(numbers.charAt(i));
		}

		while (sum >= 10) {
			int newSum = 0;
			while (sum > 0) {
				newSum += sum % 10;
				sum /= 10;
			}
			sum = newSum;
		}

		return sum;
	}

	/**
	 * Does the arrest operation by first finding the leader within its given
	 * organization. Then using that as a starting point arrest the most members
	 * possible.
	 *
	 * The idea is to arrest the leader and then arrest their underlings. Afterwards
	 * you identify which of the underlings has the most underlings under them and
	 * then move to arrest those. You will repeat this process until there are no
	 * more underlings to arrest.
	 *
	 * Notice that this process is pretty recursive...
	 *
	 * @param message - (String) Identity of the leader of the criminal operation.
	 */
	public void arrest(String message) {
		// Get the criminal organization at the organizationPosition
		Organization organization = criminalOrganizations.get(organizationPosition);
		// Get all the members of the organization
		List<Member> members = organization.organizationTraversal(m -> true);

		// Loop through all the members of the organization
		for (int i = 0; i < members.size(); i++) {
			Member member = members.get(i);
			// Check if the member's nickname matches the message
			if (member.getNickname().equalsIgnoreCase(message)) {
				// Assign the member as the leader
				Member leader = member;
				// Check if the leader has any underlings
				if (leader.getUnderlings().size() == 0) {
					// If the leader has no underlings, skip the rest of the loop iteration
					continue;
				} else {
					// Loop through all the underlings of the leader
					List<Member> underlings = leader.getUnderlings();
					int maxUnderlings = 0;
					Member maxMember = null;
					for (int j = 0; j < underlings.size(); j++) {
						Member underling = underlings.get(j);
						// Arrest the underling
						underling.setArrested(true);
						this.arrest++;
						// Find the underling with the most underlings
						if (underling.getUnderlings().size() > maxUnderlings) {
							maxUnderlings = underling.getUnderlings().size();
							maxMember = underling;
						}
					}

					// Arrest the underlings of the underling with the most underlings
					if (maxMember != null) {
						List<Member> maxUnderlingsList = maxMember.getUnderlings();
						for (int k = 0; k < maxUnderlingsList.size(); k++) {
							Member memberToArrest = maxUnderlingsList.get(k);
							memberToArrest.setArrested(true);
							this.arrest++;
						}
					}

					// Arrest the leader
					leader.setArrested(true);
					this.arrest++;
				}
				// Exit the loop after the leader is arrested
				break;
			}
		}
	}

	/**
	 * Generates the police report detailing how many arrests were achieved and how
	 * the organizations ended up afterwards.
	 *
	 * @param filePath - (String) Path of the file where the report will be saved.
	 * @throws IOException - If an I/O error occurs while writing to the file.
	 */

	// Method to generate a police report for a case
	public void policeReport(String filepath) throws IOException {
		// Create a new File object using the given filepath
		File file = new File(filepath);
		// Create a new BufferedWriter object to write to the file
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		// Write the initial heading of the police report
		writer.write("CASE REPORT\n\n");
		// Write the name of the police captain in charge of the operation
		writer.write("In charge of Operation: " + this.captain + "\n\n");
		// Write the total number of arrests made during the operation
		writer.write("Total arrests made: " + this.arrest + "\n\n");
		// Write the current status of all criminal organizations involved in the
		// operation
		writer.write("Current Status of Criminal Organizations:\n\n");
		// Loop through each criminal organization and write its status to the report
		for (Organization organization : getCriminalOrganizations()) {
			// If the boss of the organization has been arrested, write "DISOLVED" to the
			// report
			if (organization.getBoss().isArrested() == true) {
				writer.write("DISOLVED\n");
			}
			// Otherwise, write the details of the organization to the report
			writer.write(organization + "\n");
			writer.write("---\n");
		}
		// Write the end of the report and close the BufferedWriter
		writer.write("END OF REPORT");
		writer.close();
	}
	
}