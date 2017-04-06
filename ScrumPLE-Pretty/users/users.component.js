"use strict"

angular
	.module('users', ['resources'])
	.component('users', {
		templateUrl: "users/users.template.html",

		bindings: {
			users: '<'
		},
		controller: ['resources', function (resources) {
			this.addUser = function () {
				
			}
		}]
	})