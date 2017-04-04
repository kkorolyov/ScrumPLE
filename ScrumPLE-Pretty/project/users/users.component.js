"use strict"

angular
	.module('users', ['resources'])
	.component('users', {
		templateUrl: "users/users.template.html",

		controller: ['resources', function (resources) {
			this.users = [resources.user()]
			resources.users()
				.then(userMap => {
					for (let key in userMap) {
						if (key !== resources.user().id) this.users.push(userMap[key])
					}
				})
		}]
	})