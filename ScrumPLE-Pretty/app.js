"use strict"

const app = angular.module('scrumple', ['projects', 'project', 'login', 'users', 'meetings', 'tasks', 'stories', 'sprints', 'ui.router', 'title', 'resources'])

app.config(['$urlRouterProvider', '$stateProvider', function ($urlRouterProvider, $stateProvider) {
	$urlRouterProvider.otherwise("/")

	$stateProvider
		.state({
			name: 'projects',
			url: '/',
			component: 'projects'
		})

		.state({
			name: 'project',
			url: '/project/{projectName}',
			component: 'project',
			onEnter: ['$stateParams', 'resources', function ($stateParams, resources) {	// Enter project in resources service
				resources.get('projects', { name: $stateParams.projectName })
					.then(projects => {
						resources.enter(projects[0])
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
				users: ['resources', function (resources) {
					return resources.get(resources.projectUrl() + '/users')
						.then(users => {
							const sortedUsers = [resources.user()]

							for (let i = 0; i < users.length; i++) {
								if (users[i].id !== resources.user().id) sortedUsers.push(users[i])
							}
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
				meetings: ['resources', function (resources) {
					return resources.get(resources.projectUrl() + '/meetings')
				}]
			}
		})
		.state({
			name: 'project.stories',
			url: '/stories',
			component: 'stories',
			resolve: {
				stories: ['resources', function (resources) {
					return resources.get(resources.projectUrl() + '/stories')


				}]
			}
		})
		.state({
			name: 'project.stories.tasks',
			url: '/{storyId}/tasks',
			component: 'tasks',
			resolve: {
				tasks: ['resources', function(resources) {
					//TODO
				}]
			}
		})
}])

app.controller('scrumple', ['$scope', 'title', function ($scope, title) {
	$scope.title = title.title
}])
