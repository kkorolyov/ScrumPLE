"use strict"

angular
	.module('login', ['resources'])
	.component('login', {
		templateUrl: "login/login.template.html",

		controller: ['$state', 'resources', 'socket', function ($state, resources, socket) {
			this.resources = resources

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
				socket.emit('disconnect', {});
				$state.go('projects')
			}
		}]
	})
