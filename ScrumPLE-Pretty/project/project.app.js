"use strict"

angular
	.module('project', ['login', 'users', 'tasks', 'stories', 'sprints', 'resources'])
	.controller('project', ['$scope', '$location', 'resources', function ($scope, $location, resources) {
		resources.project().id = $location.search().projectId

		$scope.title = () => { return (resources.auth()) ? "ScrumPLE - " + resources.project().name : "Log In To Project" }
		$scope.resources = resources
	}])
