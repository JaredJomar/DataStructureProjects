package police;

import java.io.File;
import java.io.IOException;

/**
 * This class implements the main method that will follow the steps needed for
 * arresting members of criminal organizations. The step should be followed as
 * established in the project's document.
 */
public class ArrestOperation {

	/**
	 * The main method that will execute the necessary steps to arrest members of a
	 * criminal organization.
	 * 
	 * @param args An array of command-line arguments for the main method (not
	 *             used).
	 * @throws IOException If there is an error reading or writing files related to
	 *                     this program.
	 */
	public static void main(String[] args) throws IOException {

		// Create a new PoliceDepartment object with a Captain named "Morgan"
		PoliceDepartment policeDepartment = new PoliceDepartment("Captain Morgan");

		// Set up the police organizations based on the input files in the "case1"
		// directory
		policeDepartment.setUpOrganizations("inputFiles/case1");

		// Create a new File object representing the "Flyers" directory in the "case1"
		// directory
		File directory = new File("inputFiles/case1/Flyers");

		// Iterate through each file in the "Flyers" directory
		for (File file : directory.listFiles()) {

			// If the file is not null and is a regular file (not a directory)
			if (!file.equals(null) && file.isFile()) {

				// Use the PoliceDepartment object to decipher the message in the file and
				// determine the leader's name
				String leader = policeDepartment.decipherMessage(file.getPath());

				// Use the PoliceDepartment object to arrest the leader
				policeDepartment.arrest(leader);
			}
		}

		// Generate a police report based on the arrests made and write it to the
		// "case1report.txt" file in the "results" directory
		policeDepartment.policeReport("results/case1report.txt");
	}

}
