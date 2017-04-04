"use strict"

angular
	.module('project', ['login', 'users', 'tasks', 'stories', 'sprints', 'resources'])
	.component('project', {
		templateUrl: "project/project.template.html",

		controller: ['$location', 'resources', function ($location, resources) {
			this.project = () => { return resources.project() }
			this.auth = () => { return resources.auth() }
		}]
	})
