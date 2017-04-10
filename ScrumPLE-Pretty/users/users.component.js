"use strict"

angular
	.module('users', ['resources'])
	.component('users', {
		templateUrl: "users/users.template.html",

		bindings: {
			users: '<'
		},
		controller: ['$state', 'resources', function ($state, resources) {
			this.user = resources.user()

			this.chat = false

			this.message = undefined
			this.messageLog = () => "This is a dummy message log"
			this.send = function () {
				console.log(this.message)
				this.message = undefined
			}
		}]
	})