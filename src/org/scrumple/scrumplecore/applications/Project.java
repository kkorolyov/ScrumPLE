package org.scrumple.scrumplecore.applications;

public class Project {
	private String projectName;
	private String projDescription;
	
	public Project(String name, String description) {
		this.projectName = name;
		this.projDescription = description;

	}
	
	public String getName() {
		return this.projectName;
	}
	
	public String getDescription() {
		return this.projDescription;
	}
	/*public void createProject(){
		try {
			db.getConn().setCatalog("Project");
		} 
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		try(Statement sql = db.getConn().createStatement()){
			System.out.println(PROJECT_NAME);
			String name = s.nextLine();
			System.out.println(DESCRIPTION);
			String des = s.nextLine();
			sql.executeUpdate("INSERT INTO Project (name, description) VALUES ('"+name+"', '" +des + "')");
			db.getConn().commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());		}
	}*/


}
