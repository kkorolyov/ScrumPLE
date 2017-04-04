"use strict"

angular
	.module('project', ['login', 'users', 'tasks', 'stories', 'sprints', 'resources'])
	.component('project', {

		bindings: {
			p: '<'
		},
		templateUrl: "project/project.template.html",
		controller: ['$location', 'resources', function ($location, resources) {
			this.$onInit = function () { console.log(this.p) }
			resources.project().id = this.projectName

			this.title = () => { return (resources.auth()) ? "ScrumPLE - " + resources.project().name : "Log In To Project" }
			this.resources = resources
		}]
	})
