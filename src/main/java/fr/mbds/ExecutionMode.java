package fr.mbds;

/**
* The ExecutionMode enum defines the mode of execution
* of the application.
*/
public enum ExecutionMode {
	/** Only index a local directory */
	INDEX,
	/** Only search for a term */
	SEARCH,
	/** Index a local directory and then search for a term*/
	BOTH
}