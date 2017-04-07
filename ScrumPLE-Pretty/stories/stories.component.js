"use strict"

angular
	.module('stories', ['resources'])
	.component('stories', {
		templateUrl: "stories/stories.template.html",

		bindings: {
			//stories: '<'
		},
		controller: ['resources' , function (resources) {
			this.stories = []
			const url = resources.projectUrl() + "/stories"
			resources.get(url)
				.then(stories => {
					this.stories = stories
					console.log(stories)
				})
			this.create = function() {
				console.log("Create Button")
				const newStory = {story: this.story, storyPoint: this.storyPoint}
				// const url = resources.projectUrl() +"/stories"
				resources.set(url, newStory)
					.then(() => {
						resources.get(url)
							.then(stories => {
								this.stories = stories
								console.log("nested then")
								console.log(this.stories)
							})
					})
				/*resources.get(url)
					.then(stories => {
						this.stories = stories
						console.log("Inside then:" + stories)
					})*/
				console.log("outside then: " + this.stories)
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