"use strict"

angular
	.module('meetings', ['ui.bootstrap'])
	.component('meetings', {
		templateUrl: "meetings/meetings.template.html",

		bindings: {
			meetings: '<'
		},
		controller: ['$uibModal', 'resources', function ($uibModal, resources) {
			const url = resources.projectUrl() + "/meetings"

			this.edit = function (meeting) {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meeting: function () {
							console.log(meeting)
							return meeting
						}
					}
				}).result.then(meeting => { resources.set(url, meeting) })	// Save edits
			}

			this.add = function (meeting) {

			}
			this.delete = function (meeting) {

			}

			this.update = function (meeting) {

			}
		}]
	})
