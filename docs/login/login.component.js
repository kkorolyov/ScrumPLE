"use strict"

angular
	.module('login', [])
	.component('login', {
		templateUrl: "login/login.template.html",

		controller: ['$state', '$uibModal', 'resources', 'socket', function ($state, $uibModal, resources, socket) {
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

			this.edit = function () {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Edit Profile"
						},
						fields: {
							displayName: ['text', 'Display name', resources.user().displayName],
							role: ['text', 'Project role', resources.user().role],
							handle: ['email', 'New email'],
							password: ['password', 'New password']
						}
					}
				}).result.then(result => {
					result.data.id = resources.user().id
					result.data.credentials = {
						handle: result.data.handle,
						password: result.data.password
					}

					resources.set(resources.projectUrl() + "/users", result.data)
						.then(() => resources.login(result.data.handle, result.data.password))
				})
			}
		}]
	})
