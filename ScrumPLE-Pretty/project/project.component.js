"use strict"

angular
	.module('project', ['resources', 'login', 'users', 'tasks', 'stories', 'sprints'])
	.component('project', {
		templateUrl: "project/project.template.html",

		controller: ['$location', 'resources', function ($location, resources) {
			this.project = () => resources.project()
			this.auth = () => resources.auth()
		}]
	})
