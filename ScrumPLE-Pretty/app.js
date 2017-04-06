"use strict"

const app = angular.module('scrumple', ['project', 'login', 'users', 'tasks', 'stories', 'sprints', 'ui.router', 'title', 'resources'])

app.config(['$stateProvider', function ($stateProvider, resources) {
	$stateProvider
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
			name: 'project.stories',
			url: '/stories',
			component: 'stories'
		})
}])

app.controller('scrumple', ['$scope', 'title', function ($scope, title) {
	$scope.title = title.title
}])
