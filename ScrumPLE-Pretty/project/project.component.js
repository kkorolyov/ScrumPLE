"use strict"

angular
	.module('project', ['resources', 'login', 'dashboard', 'users', 'stories', 'sprints'])
	.component('project', {
		templateUrl: "project/project.template.html",

		controller: ['$location', 'resources', function ($location, resources) {
			this.project = () => resources.project()
			this.auth = () => resources.auth()
		}]
	})
