"use strict"

angular
	.module('stories', ['resources', 'edit'])
	.component('stories', {
		templateUrl: "stories/stories.template.html",

		bindings: {
			stories: '<'
		},
		controller: ['$uibModal', 'resources' , function ($uibModal, resources) {
			// this.stories = []
			const url = resources.projectUrl() + "/stories"
			const fields = {
				story: ['text', 'Story'],
				storyPoint: ['number', 'Story Point']
			}
			// resources.get(url)
			// 	.then(stories => {
			// 		this.stories = stories
			// 		console.log(stories)
			// 	})
			function refresh(scope) {
				resources.get(url)
					.then(stories => scope.stories = stories)
			}
			this.create = function() {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Create Story"
						},
						fields: fields
					}
				}).result.then(result => {
					resources.set(url, result.data)
						.then(() => refresh(this))
				})
			}

			this.update = function(story) {
				// const url = resources.projectUrl() + "/stories"
				resources.set(url, story)
			}

			this.delete = function(story) {
				resources.delete(url, story)
					.then(() => {
						refresh(this)
					})
			}

		}]
	})