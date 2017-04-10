"use strict"

angular
	.module('stories', ['resources', 'edit'])
	.component('stories', {
		templateUrl: "stories/stories.template.html",

		bindings: {
			stories: '<'
		},
		controller: ['$uibModal', 'resources' , function ($uibModal, resources) {

			const url = resources.projectUrl() + "/stories"
			const fields = {
				story: ['text', 'Story'],
				storyPoint: ['number', 'Story Point']
			}

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
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: 'Edit Story'
						},
						fields: fields,
						data: story
					}
				}).result.then(result => {
					(result.del ? resources.delete(url, result.data) : resources.set(url, result.data))
						.then(() => refresh(this))
				})
			}
		}]
	})