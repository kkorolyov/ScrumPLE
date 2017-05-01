"use strict"

const app = angular.module('scrumple', ['ui.router', 'title', 'resources', 'edit', 'projects', 'project', 'login', 'users', 'dashboard', 'meetings', 'stories', 'sprints'])

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
			name: 'project.dashboard',
			url: '/dashboard',
			component: 'dashboard',
			onEnter: ['title', function (title) {
				title.projectSub("Dashboard")
			}],
			resolve: {
				meetings: ['resources', function (resources) {
					const currentDate = Date.now()
					var endDate = new Date()
					endDate.setHours(23,59,59,999)
					var endMillis = endDate.getTime()
					return resources.get(resources.projectUrl() + '/meetings', {start: currentDate, end: endMillis})
				}],
				users: ['resources', function (resources) {
					return resources.get(resources.projectUrl() + '/users')
				}],
				sprints: ['resources', function (resources) {
					return resources.get(resources.projectUrl() + '/sprints')
				}],
				stories: ['resources', function (resources) {
					return resources.get(resources.projectUrl() + '/stories')
				}]
			}
		})

		.state({
			name: 'project',
			url: '/{projectName}',
			component: 'project',
			resolve: {	// Stupid hack to defer loading page until project resolves
				project: ['$stateParams', 'title', 'resources', function ($stateParams, title, resources) {
					return resources.get('projects', { name: $stateParams.projectName })
						.then(projects => {
							resources.enter(projects[0])

							title.projectSub()
						})
				}]
			},
			onExit: ['resources', function (resources) {
				resources.logout()
			}]
		})

		.state({
			name: 'project.register',
			url: '/register',
			onEnter: ['$state', '$uibModal', 'resources', function ($state, $uibModal, resources) {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: 'Contribute to ' + resources.project().name
						},
						fields: {
							handle: ['email', "Login email"],
							password: ['password', "Login password"],
							displayName: ['text', "Display name"],
							role: ['text', "Project role"]
						}
					}
				}).result.then(result => {
					const user = {
						credentials: {
							handle: result.data.handle,
							password: result.data.password
						},
						displayName: result.data.displayName,
						role: result.data.role
					}
					resources.set(resources.projectUrl() + "/users", user)
						.then(() => {
							resources.login(user.credentials.handle, user.credentials.password)
						})
				}, () => {
					$state.go('projects')
				})
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

app.factory('socket', function ($rootScope) {
	var socket = io('https://ec2-52-10-231-227.us-west-2.compute.amazonaws.com:3000');
	//console.log(socket);
	return {
		on: function (eventName, callback) {
			socket.on(eventName, function () {
				var args = arguments;
				//console.log(args);
				$rootScope.$apply(function () {
					callback.apply(socket, args);
				});
			});
		},
		emit: function (eventName, data, callback) {
			socket.emit(eventName, data, function () {
				var args = arguments;
				//console.log(args);
				$rootScope.$apply(function () {
					if (callback) {
						callback.apply(socket, args);
					}
				});
			});
		}
	};
})

app.controller('scrumple', ['$scope', 'title', function ($scope, title) {
	$scope.title = () => title.title()

	$scope.onSplash = () => $scope.title() === "ScrumPLE"	// Lame hack, don't want to make another service just for BG class, though
}])
