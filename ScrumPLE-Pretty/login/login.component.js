"use strict"

angular
	.module('login', ['resources'])
	.component('login', {
		templateUrl: "login/login.template.html",

		controller: ['$state', 'resources', 'socket',function ($state, resources,socket) {
			this.login = function () {
				resources.login(this.handle, this.password)
					.then(() => {
                    socket.emit("addUser",{displayName:this.handle});

						delete this.failed
						delete this.handle
						delete this.password
					}, () => {
						this.failed = true
					})
			}
			this.logout = function () {
                socket.emit('disconnect',{	});
				resources.logout()
				$state.go('projects')
			}

			this.resources = resources
		}]
	})
