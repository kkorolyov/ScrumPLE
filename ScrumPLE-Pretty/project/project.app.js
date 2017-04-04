"use strict"

const app = angular.module('project', ['login', 'users', 'tasks', 'stories', 'sprints', 'resources', 'ngRoute'])

app.config(['$routeProvider', function ($routeProvider) {
	$routeProvider
		.when('/users',	{
			template: "<users></users>"
		})
		.when('/stories', {
			template: "<stories></stories>"
		})
}])

app.controller('project', ['$scope', '$location', 'resources', function ($scope, $location, resources) {
	resources.project().id = $location.search().projectId

	$scope.title = () => { return (resources.auth()) ? "ScrumPLE - " + resources.project().name : "Log In To Project" }
	$scope.resources = resources
}])
