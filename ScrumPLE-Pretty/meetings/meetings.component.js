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

			function presentify(meetings) {
				for (let i = 0; i < meetings.length; i++) {
					const meeting = meetings[i]

					// Change to dates
					meeting.start = new Date(meeting.start)
					meeting.end = new Date(meeting.end)
				}
				return meetings
			}
			this.$onInit = function () { presentify(this.meetings) }

			this.edit = function (meeting) {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meeting: meeting
					}
				}).result.then(meeting => {	// Save edits
					// Change back to longs
					meeting.start = meeting.start.getTime()
					meeting.end = meeting.end.getTime()

					resources.set(url, meeting)
						.then(() => {
							resources.get(url)
								.then(meetings => { this.meetings = presentify(meetings) })
						})
				})
			}

			this.add = function (meeting) {

			}
			this.delete = function (meeting) {

			}
		}]
	})
