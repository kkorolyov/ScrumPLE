"use strict"

angular
	.module('sprints', ['rest'])
	.component('sprints', {
		templateUrl: "sprints/sprints.template.html",

		bindings: {

		},
		controller: ['rest', function (rest) {
			this.create 

		}]
	})