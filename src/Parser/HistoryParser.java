package Parser;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import application.Version;
import application.Workspace;

/**
 * @author mtsio
 */

public class HistoryParser implements Parser{
    // positions of fields so we don't have to remember the format of the file
    final int ID = 0;
    final int DATE = 1;
    final int OP_ADD = 2;
    final int OP_DEL = 3;
    final int OP_UPD = 4;
    final int DATA_STRUCTURES_ADD = 5;
    final int DATA_STRUCTURES_DEL = 6;
    final int DATA_STRUCTURES_UPD = 7;
    
    private Path historyFile;
    
    private String softwareName;
    private int initOperationsNum;
    private int initDataStructuresNum;

    private int id;
    private String date;
    private int opAdd;
    private int opDel;
    private int opUpd;
    private int dataStructuresAdd;
    private int dataStructuresDel;
    private int dataStructuresUpd;
	private String errors;

    public HistoryParser () {
    }

	public ArrayList <Version> readFile () {
	ArrayList <Version> versions =  new ArrayList <Version> ();
	
	try (BufferedReader reader =
	     Files.newBufferedReader (historyFile,
				      StandardCharsets.US_ASCII)) {
	    
	    String line = reader.readLine ();
	    String[] fields = line.split (";");
	    softwareName = fields[1];
	    // skip next line
	    line = reader.readLine (); line = reader.readLine ();
	    fields = line.split (";");
	    initOperationsNum = Integer.parseInt (fields[1] );
	    
	    line = reader.readLine ();
	    fields = line.split (";");
	    initDataStructuresNum = Integer.parseInt (fields[1] );

	    // skip next two lines
	    line = reader.readLine (); line = reader.readLine ();
	    while ((line = reader.readLine()) != null) {
	    	fields = line.split (";");
		id = Integer.parseInt (fields[ID] );
	    	date = fields[DATE];
	    	opAdd = Integer.parseInt (fields[OP_ADD] );
	    	opDel = Integer.parseInt (fields[OP_DEL] );
	    	opUpd = Integer.parseInt (fields[OP_UPD] );
	    	dataStructuresAdd =
	    	    Integer.parseInt (fields[DATA_STRUCTURES_ADD] );
	    	dataStructuresDel =
	    	    Integer.parseInt (fields[DATA_STRUCTURES_DEL] );
	    	dataStructuresUpd =
	    	    Integer.parseInt (fields[DATA_STRUCTURES_UPD] );

		// we expect an appropriate ctor here @Master
		Version v = new Version (softwareName, initOperationsNum,
					 initDataStructuresNum, id, date,
					 opAdd, opDel, opUpd,
					 dataStructuresAdd, dataStructuresDel,
					 dataStructuresUpd);
		versions.add (v);
	    }
	    System.out.println (softwareName);
	} catch (IOException e) {
		this.setError(e.getMessage());
	    System.err.format ("IOException: %s%n", e);
	}
	return versions;
    }

	@Override
	public Workspace requestWorkspace() {
		if(this.historyFile == null){
			this.setError("File not opened!");
			return null;
		}
		
		ArrayList<Version> versions = this.readFile();
		if(this.errors == null)
			return new Workspace(softwareName, versions);

		return null;
	}

	@Override
	public void openFile(Path file_path) {
		try{
			this.historyFile = file_path;
		}catch(InvalidPathException e){
			e.printStackTrace();
			this.setError(e.getReason());
		}
	}

	@Override
	public String getErrors() {
		return errors;
	}
	
	/**
	 * When you have an Error that weneed to show to user set it up!!
	 * @param error_message
	 */
	@Override
	public void setError(String error_message) {
		if(errors == null)
			errors=error_message;
		else
			errors+=", "+error_message;
	}
}
