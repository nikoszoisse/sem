package application;

import java.nio.file.Path;

import report.HistoryReportEngine;
import Parser.ParserController;
import view.ViewEngine;

/*
 * Initialize the fields and the methods like report
 * for any differce between report and this file, report it with comments.
 *  
 */
public class AppManager {
	private ViewEngine view_engine;
	private ParserController parser_controller;
	private HistoryReportEngine history_report;
	
	public void initialize(){
		view_engine = new ViewEngine(this);
		parser_controller = new ParserController(this);
		history_report = new HistoryReportEngine();
		view_engine.initialize();
	}
	
	//RENAME of parseProcedure
	/**
	 * Procedure to parse a file that ncomes from ViewEngine
	 * @param file_path
	 * @throws InterruptedException
	 */
	public void parseFileProcedure(final Path file_path) throws InterruptedException {
		Thread th = new Thread(){
			public void run(){
				parser_controller.generateWorkspaceFromFile(file_path);
			}
		};
		
		th.start();
	}
	
	/**
	 * Creates report File in save_file_path if all laws evaluated
	 * @param save_file_path,workspace 
	 */
	public void reportProcedure(Path save_file_path,Workspace workspace){
		String error = workspace.checkIfLawsEvaluated();
		if (error == null){
		    history_report.createReport(save_file_path, workspace);
		}
		else{
		    this.view_engine.showErrorDialog(error);
		}
	}
	
	/**
	 * All Error need to be loged here! 
	 * @param message
	 */
	public void setError(String message){
		//We Handle this error just shoing it to User via ViewEngine
		this.view_engine.showErrorDialog(message);
	}
	
	/**
	 * Add the workspace to Workspace list of app
	 * Inform ViewEngine Too
	 * @param ret_workspace
	 */
	public void addWorkspace(Workspace ret_workspace) {
		//TODO ADD IT TO LLIST
		this.view_engine.addTab(ret_workspace);
	}
}
