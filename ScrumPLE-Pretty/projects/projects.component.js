"use strict"

angular
	.module('projects', [])
	.component('projects', {
		templateUrl: "projects/projects.template.html",

		controller: ['resources', function (resources) {
			this.searchProjects = function () {
				console.log(this.name)
				resources.get('projects', { name: this.name })
					.then(projects => {
						this.projects = projects.sort(
							(a, b) => a.name.localeCompare(b.name))
					})
			}

			this.addProject = function (name, description, handle, password) {
				resources.set("projects", {
					name: name,
					description: description,
					owner: {
						handle: handle,
						password: password
					}
				})
			}
		}]
	})