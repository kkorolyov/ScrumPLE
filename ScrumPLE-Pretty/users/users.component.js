"use strict"

angular
	.module('users', [])
	.component('users', {
		templateUrl: "users/users.template.html",

		bindings: {
			users: '<'
		},
		controller: ['$uibModal', 'resources', function ($uibModal, resources) {
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
							email: ['email', 'Email Address'],
							text: ['textarea', "Invitation Message", "Hey dude/dudette!\nCome work with me on my awesome project '" + resources.project().name + "'!"]
						}
					}
				}).result.then(message => {
					console.log(message)	// TODO Make/Call email API
				})
			}
		}]
	})