**Note: Returned collections are UUID->object maps**

# Project
```
{
	"name": (string),
	"description": (string),
	"visible": (boolean),
	"owner": {	// Send-only
		"credentials": {
			"handle": (string),
			"password": (string)
		},
		"displayName": (string),
		"role": (string)
	}
}
```
### GET
`/rest/projects` retrieves all **public** projects  
`?name` filters projects with names containing a substring  
`?handle` filters projects containing a user with a certain handle  

`/rest/projects/{projectID}` retrieves the project referenced by `projectID`

### POST
`/rest/projects` with a project object creates a new project **if a project of the same name does not yet exist** and returns its UUID  

### PUT
`/rest/projects/{projectID}` with a project object updates the project referenced by `projectID`

### DELETE - Project Owner
`/rest/projects/{projectID}` deletes the project referenced by `projectID`

---
# User
```
{
	"credentials": {	// Send-only
		"handle": (string),
		"password": (string)
	},
	"displayName": (string),
	"role": (string)
}
```
### GET
`/rest/projects/{projectID}/users` retrieves all users local to the project referenced by `projectID`  
`?displayName` filters users with a certain display name  
`?role` filters users with a certain role - _Pending_  

### POST
`/rest/projects/{projectID}/users` with a user object creates a new user under the project referenced by `projectID`

### PUT
`/rest/projects/{projectID}/users/{userID}` with a user object updates the user referenced by `userID`

### DELETE - Same User / Project Owner
`/rest/projects/{projectID}/users/{userID}` deletes the user referenced by `userID`

---
# Meeting
```
{
	"type": (string),
	"start": (long),
	"end": (long)
}
```
### GET - Project User
`/rest/projects/{projectID}/meetings` retrieves all meetings local to the project referenced by `projectID`
`?start` denotes the earliest start time of meetings
`?end` denotes the latest start time of meetings

### POST - Project User
`/rest/projects/{projectID}/meetings` with a meeting object creates a new meeting under the project referenced by `projectID`  
`/rest/projects/{projectID}/meetings/{meetingID}` with a number `start = (millis since epoch start)` clones the meeting referenced by `meetingID` and starts the cloned meeting at `start`  

### PUT - Project User
`/rest/projects/{projectID}/meetings/{meetingID}` with a meeting object updates the meeting referenced by `meetingID`

### DELETE - Project User
`/rest/projects/{projectID}/meetings/{meetingID}` deletes the meeting referenced by `meetingID`

---
# User Stories
```
{
	"story": (string),
	"storyPoint": (int),
	"sprint": {
		"sprintNumber": (int),
		"start": (long),
		"end": (long)
	}
}
```
### GET
`/rest/projects/{projectID}/stories` retrieves all stories local to the project referenced by `projectID`  

### POST
`/rest/projects/{projectID}/stories` with a user story object creates a new user story under the project referenced by `projectID`

### PUT
`/rest/projects/{projectID}/stories/{storyID}` with a user story object updates the user referenced by `storyID`

### DELETE - Same User / Project Owner
`/rest/projects/{projectID}/stories/{storyID}` deletes the user story referenced by `storyID`

---
# Sprints
```
{
	"sprintNumber": (int),
	"start": (long),
	"end": (long)
}
```
### GET
`/rest/projects/{projectID}/sprints` retrieves all the sprints local to the project referenced by `projectID`

### POST
`/rest/projects/{projectID}/sprints` with a sprint object will create a new sprint under the project referenced by `projectID`

### PUT
`/rest/projects/{projectID}/sprints/{sprintID}` with a sprint object will update the sprint referenced by `sprintID`

### DELETE
`/rest/projects/{projectID}/sprints/{sprintID}` deletes the sprint referenced by `sprintID`

---
# Tasks
```
{
	"story": {
		"story": (string),
		"storyPoint": (int),
		"sprint": {
			"sprintNumber": (int),
			"start": (long),
			"end": (long)
		}
	},
	"description": (string),
	"done": (boolean),
	"user": {
		"credentials": {	// Send-only
			"handle": (string),
			"password": (string)
		},
		"displayName": (string),
		"role": (string)
	}
}
```
### GET
`/rest/projects/{projectID}/stories/{storyID}/tasks` retrieves all the tasks belonging to the story referenced by `storyID`

### POST
`/rest/projects/{projectID}/stories/{storyID}/tasks` with a task object will create a new task under the story referenced by `storyID`

### PUT
`/rest/projects/{projectID}/stories/{storyID}/tasks/{taskID}` with a task object will update the task referenced by `taskID`

### DELETE
`/rest/projects/{projectID}/stories/{storyID}/tasks/{taskID}` deletes the task referenced by `taskID`

---
# Auth
Credentials
```
{
	"handle": (string),
	"password": (string)
}
```
`POST` to `/rest/projects/{projectID}/auth` with a credentials object returns a string token on successful authentication  
This token should be passed in the `Authorization` header of subsequent REST calls requiring authorization
