"use strict"

angular
	.module('project', ['login', 'users', 'tasks', 'stories', 'sprints'])
	.controller('project', ['$scope', '$location', function ($scope, $location) {
		$scope.projectId = $location.search().projectId
		$scope.auth = {}
		$scope.title = function () {
			return ($scope.auth.project) ? "ScrumPLE - " + $scope.auth.project.name : "Log In To Project"
		}
	}])
