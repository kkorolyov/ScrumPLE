"use strict"

angular
	.module('meetings', [])
	.component('meetings', {
		templateUrl: "meetings/meetings.template.html",

		bindings: {
			meetings: '<'
		},
		controller: ['resources', function (resources) {
			this.add = function (meeting) {

			}
			this.delete = function (meeting) {

			}
			
			this.update = function (meeting) {

			}
		}]
	})
