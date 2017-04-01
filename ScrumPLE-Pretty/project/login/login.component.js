"use strict"

angular.module('project').component('login', {
	templateUrl: "login/login.template.html",

	bindings: {
		projectId: '<',
		logInSuccess: '&',
	},
	controller: ['rest', function (rest) {
		this.login = function () {
			rest.login(this.handle, this.password, this.projectId, () => {
				this.logInSuccess()
			}, () => {
				this.logInFailed = true
			})
		}
	}]
})
