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

			this.create = function () {
				$uibModal.open({
					component: 'createProject'
				})
			}
		}]
	})