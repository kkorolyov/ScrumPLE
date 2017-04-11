"use strict"

angular
	.module('projects', [])
	.component('projects', {
		templateUrl: "projects/projects.template.html",

		controller: ['$uibModal', 'resources', function ($uibModal, resources) {
			this.searchProjects = function () {
				console.log(this.name)
				resources.get('projects', { name: this.name })
					.then(projects => {
						this.projects = projects.sort(
							(a, b) => a.name.localeCompare(b.name))
					})
			}
			
			const proj = resources.get('projects')
			const fields = {
				name: ["text", "Project Name"],
				escription: ["text", "Project Description"],
				visible: ["checkbox", "visible"],
				handle: ["text", "Username"],
				Password: ["text", "Password"]
			}

			this.create = function () {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta:{
							title: "Create New Project"
						},
						fields: fields
					}
				}).result.then(result => {
					resources.set(proj)
						.then(() => refresh(this))
				})
			}
		}]
	})