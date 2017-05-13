"use strict"

angular
	.module('meetings', [])
	.component('meetings', {
		templateUrl: "meetings/meetings.template.html",

		bindings: {
			meetings: '<'
		},
		controller: ['$uibModal', 'resources', function ($uibModal, resources) {
			const url = resources.projectUrl() + "/meetings"
			const fields = {
				type: ["text", "Type"],
				start: ["date", "Start Date"],
				end: ["date", "End Date"]
			}

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
				return meetings.map(meeting => dateify(meeting))
					.sort((a, b) => a.start > b.start ? -1 : a.start < b.start ? 1 : 0)	// Sort by start time descending
			}

			function refresh(scope) {
				resources.get(url)
					.then(meetings => { scope.meetings = presentify(meetings) })
			}

			this.$onInit = function () { this.meetings = presentify(this.meetings) }

			this.add = function () {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Create Meeting"
						},
						fields: fields
					}
				}).result.then(result => {
					resources.set(url, longify(result.data))
						.then(() => refresh(this))
				})
			}
			this.edit = function (meeting) {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Edit Meeting"
						},
						fields: fields,
						data: meeting
					}
				}).result.then(result => {	// Save edits
					(result.del ? resources.delete(url, result.data) : resources.set(url, longify(result.data)))	// Return a delete or edit promise
						.then(() => refresh(this))
				})
			}
		}]
	})
