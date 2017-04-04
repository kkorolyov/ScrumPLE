"use strict"

const app = angular.module('scrumple', ['project', 'login', 'users', 'tasks', 'stories', 'sprints', 'ui.router', 'title'])

app.config(['$stateProvider', function ($stateProvider) {
	$stateProvider
		.state({
			name: 'project',
			url: '/project/{projectName}',
			component: 'project',
			resolve: {
				p: () => {
					const test = "BLAH"
					console.log(test)
					return test
				}
			}
		})

		.state({
			name: 'project.users',
			url: '/users',
			component: 'users'
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
