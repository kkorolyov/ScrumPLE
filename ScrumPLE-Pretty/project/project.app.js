"use strict"

angular
	.module('project', [])
	.controller('project', ['$scope', '$location', function ($scope, $location) {
		$scope.projectId = $location.search().projectId
		
		$scope.loggedIn = false
		$scope.logInSuccess = () => $scope.loggedIn = true; $scope.$apply
		$scope.logOutSuccess = () => $scope.loggedIn = false
	}])
