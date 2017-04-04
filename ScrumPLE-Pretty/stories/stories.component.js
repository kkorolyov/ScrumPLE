"use strict"

angular
	.module('stories', ['resources'])
	.component('stories', {
		templateUrl: "stories/stories.template.html",

		bindings: {

		},
		controller: ['resources' , function (resources) {
			this.stories = []
			resources.stories()
				.then(storiesMap => {
					for(let key in storiesMap){
						this.stories.push(storiesMap[key])
					}
				})
			this.create = function() {
				console.log("Create Button")
				resources.createStory(this.story, this.storyPoint)
			} //TODO

		}]
	})