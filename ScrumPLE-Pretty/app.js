"use strict"

const app = angular.module('scrumple', ['ui.router', 'title', 'resources', 'projects', 'project', 'login', 'users', 'meetings', 'tasks', 'stories', 'sprints'])

app.config(['$urlRouterProvider', '$stateProvider', function ($urlRouterProvider, $stateProvider) {
	$urlRouterProvider.otherwise("/")

	$stateProvider
		.state({
			name: 'projects',
			url: '/',
			component: 'projects',
			onEnter: ['title', function (title) {
				title.title("ScrumPLE")
			}]
		})

		.state({
			name: 'project',
			url: '/project/{projectName}',
			component: 'project',
			onEnter: ['$stateParams', 'title', 'resources', function ($stateParams, title, resources) {	// Enter project in resources service
				resources.get('projects', { name: $stateParams.projectName })
					.then(projects => {
						resources.enter(projects[0])

						title.title(resources.project().name)
					})
			}],
			onExit: ['resources', function (resources) {
				resources.logout()
			}]
		})

		.state({
			name: 'project.users',
			url: '/users',
			component: 'users',
			resolve: {
				users: ['title', 'resources', function (title, resources) {
					return resources.get(resources.projectUrl() + '/users')
						.then(users => {
							const sortedUsers = [resources.user()]

							for (let i = 0; i < users.length; i++) {
								if (users[i].id !== resources.user().id) sortedUsers.push(users[i])
							}
							title.title(resources.project().name + " - Users")
							return sortedUsers
						})
				}]
			}
		})
		.state({
			name: 'project.meetings',
			url: '/meetings',
			component: 'meetings',
			resolve: {
				meetings: ['title', 'resources', function (title, resources) {
					return resources.get(resources.projectUrl() + '/meetings')
						.then(meetings => {
							title.title(resources.project().name + " - Meetings")
							return meetings
						})
				}]
			}
		})
		.state({
			name: 'project.stories',
			url: '/stories',
			component: 'stories',
			resolve: {
				stories: ['title', 'resources', function (title, resources) {
					return resources.get(resources.projectUrl() + '/stories')
						.then(stories => {
							title.title(resources.project().name + " - Stories")
							return stories
						})
				}]
			}
		})

		.state({
			name: 'project.sprints',
			url: '/sprints',
			component: 'sprints',
			resolve: {
				sprints: ['title', 'resources', function(title, resources) {
					return resources.get(resources.projectUrl() + '/sprints')
						.then(sprints => {
							title.title(resources.project().name + " - Sprints")
							return sprints
						})
				}]
			}
		})
		.state({
			name: 'project.tasks',
			url: '/{storyId}/tasks',
			component: 'tasks',
			resolve: {
				tasks: ['$stateParams', 'title', 'resources', function ($stateParams, title, resources) {
					return resources.get(resources.projectUrl() + "/" + $stateParams.storyId + "/tasks")
						.then(tasks => {
							title.title(resources.project().name + " Stories(" + $stateParams.storyId + ")")
						})
				}]
			}
		})
}])

app.controller('scrumple', ['$scope', 'title', function ($scope, title) {
	$scope.title = () => title.title()

	$scope.onSplash = () => $scope.title() === "ScrumPLE"	// Lame hack, don't want to make another service just for BG class, though
}])
