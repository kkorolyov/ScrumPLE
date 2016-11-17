# ScrumPLE
Virtual collaboration and development environment following the Scrum SDLC

## REST API
### Basic Flow
* `POST` to `rest/projects` creates a new project
	* XML or ~~JSON~~ `project` object with 2 attributes:
		* `name` = project name
		* `description` = project description
	* Returns the UUID of the new project
* `DELETE` to `rest/projects/{uuid}` deletes the project matching `uuid`
	* Returns the deleted project in XML or ~~JSON~~
* `GET` or `POST` to `rest/projects/find` locates a project by name
	* 1 parameter:
		* `name` = project name
	* Returns the UUID of a matching project
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
  
