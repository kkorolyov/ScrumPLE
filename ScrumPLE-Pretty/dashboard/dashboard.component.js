"use strict"

angular
	.module('dashboard', [])
	.component('dashboard', {
		templateUrl: "dashboard/dashboard.template.html",

        bindings: {
			dashboard: '<'
		},

        controller:['$uibModal', 'resources', function ($uibModal, resources) {
            this.names = [
                {model : "InProgress"},
                {model : "Not Started"},
                {model : "Completed"}
                ];
        }]
    })