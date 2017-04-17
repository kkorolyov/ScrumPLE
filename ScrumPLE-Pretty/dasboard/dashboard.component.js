"use strict"

angular
	.module('dashboard', [])
	.component('dashboard', {
		templateUrl: "dashboard/dashboard.template.html",

        bindings: {
			dashboard: '<'
		},

        controller:['$uibModal', 'resources', 'myCtrl', function ($uibModal, $scope, resources) {
            $scope.names = ["InProgress", "Not Started","Completed"];
        }]
    })