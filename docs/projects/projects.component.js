"use strict"

angular
	.module('projects', [])
	.component('projects', {
		templateUrl: "projects/projects.template.html",

		controller: ['$uibModal', 'resources', function ($uibModal, resources) {
			this.searchProjects = function () {
				resources.get('projects', { name: this.name })
					.then(projects => {
						this.projects = projects.sort(
							(a, b) => a.name.localeCompare(b.name))
					})
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
					})
				})
			}
		}]
	})