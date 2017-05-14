"use strict"

angular
	.module('projects', [])
	.component('projects', {
		templateUrl: "projects/projects.template.html",

		controller: ['$state', '$uibModal', 'resources', function ($state, $uibModal, resources) {
			this.search = function (name) {
				return resources.get('projects', { name: name })
					.then(projects => {
						return projects.map(project => project.name)
					})
			}

			this.go = function () {
				$state.go('project', { projectName: this.name })
			}

			this.add = function () {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Create New Project"
						},
						fields: {
							name: ["text", "Project Name"],
							description: ["text", "Project Description"],
							handle: ["email", "Project Owner Email Address"],
							password: ["password", "Project Owner Password"]
						}
					}
				}).result.then(result => {
					resources.set('projects', {
						name: result.data.name,
						description: result.data.description,
						owner: {
							credentials: {
								handle: result.data.handle,
								password: result.data.password
							},
							displayName: result.data.handle,
							role: "owner"
						}
					}).then(() => {
						this.name = result.data.name.replace(/\s+/g, "_")
						resources.get('projects', { name: this.name })
							.then(projects => {
								const project = projects[0]

								resources.enter(project)
								resources.login(result.data.handle, result.data.password)
									.then(() => {
										this.go()
									})
							})
					})
				})
			}
		}]
	})