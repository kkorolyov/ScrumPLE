"use strict"

const app = angular.module('scrumple', ['ui.router', 'title', 'resources', 'edit', 'projects', 'project', 'login', 'users', 'meetings', 'stories', 'sprints'])

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

						title.projectSub()
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
			onEnter: ['title', function (title) {
				title.projectSub("Team")
			}],
			resolve: {
				users: ['resources', function (resources) {
					return resources.get(resources.projectUrl() + '/users')
						.then(users => users.filter(user => user.id !== resources.user().id))	// Only return other users
				}]
			}
		})
		.state({
			name: 'project.meetings',
			url: '/meetings',
			component: 'meetings',
			onEnter: ['title', function (title) {
				title.projectSub("Meetings")
			}],
			resolve: {
				meetings: ['title', 'resources', function (title, resources) {
					return resources.get(resources.projectUrl() + '/meetings')
				}]
			}
		})
		.state({
			name: 'project.stories',
			url: '/stories',
			component: 'stories',
			onEnter: ['title', function (title) {
				title.projectSub("Stories")
			}],
			resolve: {
				stories: ['title', 'resources', function (title, resources) {
					return resources.get(resources.projectUrl() + '/stories')
				}]
			}
		})
		.state({
			name: 'project.sprints',
			url: '/sprints',
			component: 'sprints',
			onEnter: ['title', function (title) {
				title.projectSub("Sprints")
			}],
			resolve: {
				sprints: ['title', 'resources', function (title, resources) {
					return resources.get(resources.projectUrl() + '/sprints')
				}]
			}
		})
}])

app.controller('scrumple', ['$scope', 'title', function ($scope, title) {
	$scope.title = () => title.title()

	$scope.onSplash = () => $scope.title() === "ScrumPLE"	// Lame hack, don't want to make another service just for BG class, though
}])
