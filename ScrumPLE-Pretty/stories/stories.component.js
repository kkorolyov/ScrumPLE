"use strict"

angular
	.module('stories', ['resources', 'edit'])
	.component('stories', {
		templateUrl: "stories/stories.template.html",

		bindings: {
			stories: '<'
		},
		controller: ['$scope', '$uibModal', 'resources' , function ($scope, $uibModal, resources) {
			this.taskList = []
			$scope.isCollapsed = true
			const url = resources.projectUrl() + "/stories"
			const fields = {
				story: ['text', 'Story'],
				storyPoint: ['number', 'Story Point']
			}

			function refresh(scope) {
				resources.get(url)
					.then(stories => scope.stories = stories)
			}

			this.tasks = function(story) {
				const taskUrl = url + "/" + story.id + "/tasks"
				resources.get(taskUrl)
					.then(tasks => this.taskList = tasks)
			}

			this.createTask = function(story) {
				const taskUrl = url + "/" + story.id + "/tasks"
				const task = {
					story: story,
					description: this.taskDescription
				}
				resources.set(taskUrl, task)
					.then(() => resources.get(taskUrl)
									.then(tasks => this.taskList = tasks))
				// const taskUrl = url + "/" + story.id + "/tasks"
				// const fields = {
				// 	description: ['text', 'Description']
				// }
				// $uibModal.open({
				// 	component: 'edit',
				// 	resolve: {
				// 		meta: {
				// 			title: "Create Task"
				// 		},
				// 		fields: fields
				// 	}.result.then(result => {
				// 		resources.set(taskUrl, result.data)
				// 			.then(tasks => this.taskList = tasks)
				// 	})
				// })
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