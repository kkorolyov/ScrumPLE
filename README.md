# ScrumPLE
Virtual collaboration and development environment following the Scrum SDLC

## Installation
Included are the core ScrumPLE application and browser-based client, which both run in a servlet deployed on the Tomcat application server.
### Deployment on Apache Tomcat
* Configure an [Apache Tomcat](http://tomcat.apache.org/) instance
* Deploy the included `ScrumPLE.war` file on Tomcat
* In a web browser, navigate to the `[Tomcat Server IP Address]:[Tomcat Port]//[ScrumPLE]` URL

## Usage
The included ScrumPLE client is a browser-based application.
### Some possible action 1
### Some possible action 2

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
* `POST` to `rest/projects/{uuid}/tasks` creates a new task in the project matching `uuid`
	* XML object with 2 attributes:
  	 * `tasktype=` the integer representation of task type
  	 * `des=` description of the task
*`GET` to `rest/project/{uuid}/tasks/similar` will fetch tasks of a similar category
  * 1 parameter
    * `type=` the integer representation of task type
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
  
