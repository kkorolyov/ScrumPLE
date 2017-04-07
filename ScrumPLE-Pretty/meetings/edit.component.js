"use strict"

angular
	.module('meetings')
	.component('edit', {
		templateUrl: "meetings/edit.template.html",

		bindings: {
			resolve: '<',
			close: '&',
			dismiss: '&'
		},
		controller: [function () {
			this.$onInit = function () {
				this.meeting = this.resolve.meeting
			}
			
			this.ok = function () {	// Return augmented meeting
				this.close({$value: this.meeting})
			}
		}]
	})
