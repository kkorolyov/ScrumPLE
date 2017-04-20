"use strict"

angular
	.module('stories', ['resources', 'edit'])
	.component('stories', {
		templateUrl: "stories/stories.template.html",

		bindings: {
			stories: '<'
		},
		controller: ['$uibModal', 'resources' , function ($uibModal, resources) {
			this.taskList = []
			const url = resources.projectUrl() + "/stories"
			const fields = {
				story: ['text', 'Story'],
				storyPoint: ['number', 'Story Point']
			}

			function refresh(scope) {
				resources.get(url)
					.then(stories => scope.stories = stories)
			}

			this.debug = function () {
				console.log("Debugging")
			}

			function refreshTasks(url, story) {
				const taskList = []
				resources.get(url)
					.then(tasks => {
						for(let i = 0; i < tasks.length; i++) {
							if(tasks[i].story.story === story.story) {
								taskList.push(tasks[i])
							}
						}
					})
					return taskList
			}
			this.tasks = function(story) {
				const taskUrl = url + "/" + story.id + "/tasks"
				this.taskList = refreshTasks(taskUrl, story)
			}

			this.createTask = function(story) {
				const taskUrl = url + "/" + story.id + "/tasks"
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Create Task for " + story.story
						},
						fields: {
							description: ['text', 'Task Description']
						}
					}
				}).result.then(result => {
					const task = {
						story: story,
						description: result.data.description
					}
					resources.set(taskUrl, task).then(() => this.taskList = refreshTasks(taskUrl, story))
				})
			}

			this.editTask = function(story, task) {
				const taskUrl = url + "/" + story.id + "/tasks"
				$uibModal.open({
					component: "edit",
					resolve: {
						meta: {
							title: "Edit Task"
						},
						fields: {
							description: ['text', 'Task Description', task.description],
							done: ['checkbox', 'Done', task.done]
						},
						data: task
					}
				}).result.then(result => {
					if(!result.del){
						console.log(result.data.done)
						task.done = result.data.done
						console.log(task.done)
						task.description = result.data.description
					}
					(result.del ? resources.delete(taskUrl, task) : resources.set(taskUrl, task))
						.then(() => this.taskList = refreshTasks(taskUrl, story))
				})
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