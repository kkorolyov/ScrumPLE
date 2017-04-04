"use strict"

angular
	.module('login', ['resources'])
	.component('login', {
		templateUrl: "login/login.template.html",

		controller: ['resources', function (resources) {
			this.login = function () {
				resources.login(this.handle, this.password)
					.then(() => {
						delete this.failed
						delete this.handle
						delete this.password
					}, () => {
						this.failed = true
					})
			}
			this.logout = function () {
				resources.logout()
			}

			this.resources = resources
		}]
	})
