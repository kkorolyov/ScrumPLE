"use strict"
angular.
	module('projectStats').
	component('projectStats', {
		templateUrl: "project-stats.template.html",

		controller: function($scope) {
			this.projects = [{}],
			this.$onInit = function () {
				rest.ajax('GET', 'projects', null, response => {
					for (let key in response) {
						this.projects.push(response[key])
					}
					$scope.$apply()
				})
			}
		}
	})
