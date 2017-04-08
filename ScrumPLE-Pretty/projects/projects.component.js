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

			this.addProject = function (name, description, visible, handle, password) {
				resources.set("projects", {
					name: this.projectName,
					description: this.description,
					visible: this.visible,
					owner: {
						credentials: {
							handle: this.handle,
							password: this.password
						},
						displayName: this.handle
						
					}
				})
				// .then(project => {
				// 	console.log(project)
				// 	const url = "projects/" + project+ "/users"
				// 	 resources.set(url, {
				// 	 	handle: this.handle,
				// 	 	password: this.password
				// 	 })
				// })
				
			}
		}]
	})