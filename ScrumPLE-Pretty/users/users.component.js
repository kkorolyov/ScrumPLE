"use strict"

angular
	.module('users', [])
	.component('users', {
		templateUrl: "users/users.template.html",

		bindings: {
			users: '<'
		},
		controller: ['$state', '$uibModal', 'resources', 'rest', function ($state, $uibModal, resources, rest) {
			this.messageLog = () => "This is a dummy message log"
			this.send = function () {
				console.log(this.message)
				delete this.message
			}

			this.add = function () {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Invite New Teammate"
						},
						fields: {
							email: ['email', "Email Address"],
							message: ['textarea', "Invitation Message", "Hey dude/dudette!\nCome work with me on my awesome project '" + resources.project().name + "'!"]
						}
					}
				}).result.then(result => {
					result.data.project = resources.project().name
					result.data.url = $state.href('project.register', {}, { absolute: true })

					rest.ajax("POST", resources.projectUrl() + "/users/invite", result.data)
						.then(() => console.log("Invitation sent"))
				})
			}
		}]
	})