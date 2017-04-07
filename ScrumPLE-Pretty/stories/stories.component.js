"use strict"

angular
	.module('stories', ['resources'])
	.component('stories', {
		templateUrl: "stories/stories.template.html",

		bindings: {
			stories: '<'
		},
		controller: ['resources' , function (resources) {
			const url = resources.projectUrl() + "/stories"
			this.create = function() {
				console.log("Create Button")
				const newStory = {story: this.story, storyPoint: this.storyPoint}
				// const url = resources.projectUrl() +"/stories"
				resources.set(url, newStory)
			}

			this.update = function(story) {
				// const url = resources.projectUrl() + "/stories"
				resources.set(url, story)
			}

			this.delete = function(story) {
				resources.delete(url, story)
			}

		}]
	})