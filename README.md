# ScrumPLE
Virtual collaboration and development environment following the Scrum SDLC

## REST API
### Basic Flow
* `GET` or `POST` to the `rest/auth` URL 3 attributes
 * `project=` name of project to enter
 * `handle=` handle of user to authenticate as
 * `signature=` Base64-encoded password of user to authenticate as
* If authentication passes, the server will return a unique `authkey` contained both project and user information
* `GET` to `rest/{authkey}/profile` returns information for the authenticated user
* `GET` to `rest/{authkey}/users` returns basic information on all users in the project
