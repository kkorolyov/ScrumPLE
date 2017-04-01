"use strict"

angular
	.module('login', ['rest'])
	.component('login', {
		templateUrl: "login/login.template.html",

		bindings: {
			projectId: '<',
			auth: '=',
		},
		controller: ['rest', function (rest) {
			this.login = function () {
				rest.login(this.handle, this.password, this.projectId, () => {
					this.auth.loggedIn = true
					rest.ajax('GET', "projects/" + this.projectId, null, project => {
						this.auth.project = project
					})
					delete this.auth.failed

					delete this.handle
					delete this.password
				}, () => {
					this.auth.failed = true

					delete this.auth.loggedIn
				})
			}
			this.logout = function () {
				rest.token = null

				delete this.auth.loggedIn
				delete this.auth.project
			}
		}]
	})
