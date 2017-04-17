"use strict"

angular
	.module('dashboard', ['resources','users','stories', 'sprints', 'meetings'])
	.component('dashboard', {
		templateUrl: "dashboard/dashboard.template.html",

        bindings: {
			dashboard: '<'
		},

        controller:['$uibModal', 'resources', function ($uibModal, resources) {
            this.names = [ "InProgress", "Not Started","Completed"];
        }]
    })