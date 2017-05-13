"use strict"

angular
	.module('sprints', ['edit'])
	.component('sprints', {
		templateUrl: "sprints/sprints.template.html",

		bindings: {
			sprints: '<'

		},
		controller: ['$uibModal', 'resources', function ($uibModal, resources) {
			const url = resources.projectUrl() + "/sprints"
			const fields = {
				sprintNumber: ["number", "Sprint Number"],
				start: ["date", "Start Date"],
				end: ["date", "End Date"]
			}

			function toDate(sprint){
				sprint.start = new Date(sprint.start)
				sprint.end = new Date(sprint.end)

				return sprint
			}

			function toLong(sprint) {
				sprint.start = sprint.start.getTime()
				sprint.end = sprint.end.getTime()

				return sprint
			}

			function presentify(sprints) {
				for(let i = 0; i < sprints.length; i++) {
					toDate(sprints[i])
				}
				return sprints
			}

			function refresh(scope) {
				resources.get(url)
					.then(sprints => { scope.sprints = presentify(sprints.sort((a, b) => a.start < b.start ? -1 : a.start > b.start ? 1 : 0))})
			}
			this.$onInit = function() {
				presentify(this.sprints)
			}
			this.add = function() {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Create Sprint"
						},
						fields: fields 
					}
				}).result.then(result => {
					resources.set(url, toLong(result.data))
						.then(() => refresh(this))
				})
			}

			this.edit = function(sprint) {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Edit Sprint"
						},
						fields: fields,
						data: sprint
					}
				}).result.then(result => {
					(result.del ? resources.delete(url, result.data) : resources.set(url, toLong(result.data)))
						.then(() => refresh(this))
				})
			}

			this.create //TODO
		}]
	})