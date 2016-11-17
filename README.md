# ScrumPLE
Virtual collaboration and development environment following the Scrum SDLC

## REST API
### Basic Flow
* `GET` or `POST` to `rest/auth` authenticates a project-user pair (users are local to projects)
  * 3 attributes
    * `project=` name of project to enter
    * `handle=` handle of user to authenticate as
    * `signature=` Base64-encoded password of user to authenticate as
* If authentication passes, the server returns a unique `authkey` containing both project and user information
* `GET` to `rest/{authkey}/profile` returns information for the authenticated user
* `GET` to `rest/{authkey}/users` returns basic information on all users in the project
* `GET` to `rest/{authkey}/tasks` lists all tasks for the selected project
* `GET` to `rest/{authkey}/tasks/create` will create and add a task to the project database
  * 2 attributes
  	 * `tasktype=` the integer representation of task type
  	 * `des=` description of the task
*`GET` to `rest/{authkey}/tasks/similar` will fetch tasks of a similar category
  * 1 attribute
    * `type=` the integer representation of task type
*`POST` to 
### Debug
* `GET` or `POST` to `rest/debug/reset` resets the backing database with stub entities
  * 2 optional attributes
    * `projects=` number of projects to create
    * `users=` number of users to create per project
* `GET` to `rest/debug/projects` lists all projects
* `GET` or `POST` to `rest/debug/projects/byName` retrieves the UUID for a specific project
  * 1 attribute
    * `name=` project name
* `GET` to `rest/debug/projects/{projectUUID}` retrieves the project for the specified UUID
* `GET` to `rest/debug/projects/{projectUUID}/users` retrieves all users in the specified project
