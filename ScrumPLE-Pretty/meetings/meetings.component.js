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

			function dateify(meeting) {	// long to Date
				meeting.start = new Date(meeting.start)
				meeting.end = new Date(meeting.end)

				return meeting
			}
			function longify(meeting) {	// Date to long
				meeting.start = meeting.start.getTime()
				meeting.end = meeting.end.getTime()

				return meeting
			}

			function presentify(meetings) {
				for (let i = 0; i < meetings.length; i++) dateify(meetings[i])
				return meetings
			}

			function refresh(scope) {
				resources.get(url)
					.then(meetings => { scope.meetings = presentify(meetings) })
			}

			this.$onInit = function () { presentify(this.meetings) }

			this.add = function () {
				$uibModal.open({
					component: 'edit'
				}).result.then(result => {
					resources.set(url, longify(resultmeeting))
						.then(() => refresh(this))
				})
			}
			this.edit = function (meeting) {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meeting: meeting
					}
				}).result.then(result => {	// Save edits
					if (result.del) {
						resources.delete(url, result.meeting)
							.then(() => refresh(this))
					}	else {
						resources.set(url, longify(meeting))
							.then(() => refresh(this))
					}
				})
			}
		}]
	})
