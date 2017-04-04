"use strict"

angular
	.module('sprints', ['resources'])
	.component('sprints', {
		templateUrl: "sprints/sprints.template.html",

		bindings: {

		},
		controller: ['resources', function (resources) {
			this.sprints = []
			resources.sprints().then(sprintMap => {
				for(let key in sprintMap){
					this.sprints.push(sprintMap[key])
				}
			})

			this.create //TODO
		}]
	})