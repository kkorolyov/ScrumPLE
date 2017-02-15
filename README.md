# ScrumPLE
Collaborative development environment tailored to the Scrum framework

[![Build Status](https://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8443/jenkins/buildStatus/icon?job=ScrumPLE)](https://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:8443/jenkins/job/ScrumPLE/)
## Dev Setup
### Required Tools
* [Maven](https://maven.apache.org/)
* [Tomcat](https://tomcat.apache.org/)
### Maven Usage

### Tomcat Configuration
* Copy the (context XML)[src/main/resources/scrumple.xml] to `<tomcat-install>/conf/Catalina/localhost/`
* Configure the environment properties in the context XML to match your environment

## Installation
Included are the core ScrumPLE application and browser-based client, which both run in a servlet deployed on the Tomcat application server.
### Deployment on Apache Tomcat
* Configure an [Apache Tomcat](http://tomcat.apache.org/) instance
* Deploy the included `ScrumPLE.war` file on Tomcat
* In a web browser, navigate to the `[Tomcat Server IP Address]:[Tomcat Port]/[ScrumPLE]` URL

## Usage
The included ScrumPLE client is a browser-based application.
### Creating a project
* Enter the project name
* Click the create new button
* Enter the description of the project
* Click the create button
### Adding a new task
* Enter the task type
* Enter the task Description
* Click on submit
* Check DB to ensure task was created
## REST API
ScrumPLE's business logic layer is exposed to client applications as a RESTful web service.
### Basic Flow
* `POST` to `rest/projects` creates a new project
	* XML `project` object with 2 attributes:
		* `name` = project name
		* `description` = project description
	* Returns the UUID of the new project
* `DELETE` to `rest/projects/{uuid}` deletes the project matching `uuid`
	* Returns the deleted project in XML
* `GET` to `rest/projects` returns all public projects or the project exactly matching optional parameters
	* 1 optional parameter:
		* `name` = project name
* `GET` to `rest/projects/{uuid}` returns the project matching `uuid`
* `GET` to `rest/projects/{uuid}/users` returns basic information on all users in the project matching `uuid`
* `GET` to `rest/projects/{uuid}/tasks` returns all tasks in the project matching `uuid`
*`GET` to `rest/project/{uuid}/tasks/similar` will fetch tasks of a similar category
  * 1 parameter
    * `type=` the integer representation of task type
* `POST` to `rest/projects/{uuid}/tasks` creates a new task in the project matching `uuid`
	* 3 attributes:
	 * `userStory` = string representation of a userstory
  	 * `taskType=` the integer representation of task type
  	 * `taskDescription=` description of the task
* `PUT` to `rest/projects/{uuid}/tasks/{taskUUID}` will update the corresponding task
	* 3 attributes:
	 * `userStory` = string representation of a userstory
  	 * `taskType` = the integer representation of task type
  	 * `taskDescription` = description of the task
  	*Returns the updated task in XML
*`DELETE` to `rest/projects/{uuid}/tasks/{taskUUID}` will delete the corresponding task
	*Returns the deleted task in XML
* `GET` to `rest/projects/{uuid}/userstories` returns all userstories in the project matching `uuid`
* `POST` to `rest/projects/{uuid}/userstories` creates a new userstory in the project matching `uuid`
    * Form Data with 1 attribute:
		* `userStory` = The story

### Debug
* `GET` to `rest/debug/reset` resets the backing database with stub entities
  * 2 optional parameters
    * `projects=` number of projects to create
    * `users=` number of users to create per project
* `GET` to `rest/debug/projects` lists all projects
* `GET` to `rest/debug/projects/byName` retrieves the UUID for a specific project
  * 1 parameter
    * `name=` project name
* `GET` to `rest/debug/projects/{projectUUID}` retrieves the project for the specified UUID
* `GET` to `rest/debug/projects/{projectUUID}/users` retrieves all users in the specified project
  
