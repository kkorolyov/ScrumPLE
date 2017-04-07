"use strict"

angular
	.module('meetings')
	.component('edit', {
		templateUrl: "meetings/edit.template.html",

		bindings: {
			resolve: '<'
		},
		controller: [function () {
			this.$onInit = function () {
				this.meeting = this.resolve.meeting

				console.log("HI")
				console.log(this.meeting)
			}
		}]
	})
