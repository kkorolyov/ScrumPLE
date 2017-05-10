"use strict"

angular
	.module('users', [])
	.component('users', {
		templateUrl: "users/users.template.html",

		bindings: {
			users: '<'
		},
		controller: ['$state', '$uibModal', 'resources', 'rest', 'socket', function ($state, $uibModal, resources, rest, socket) {

			this.add = function () {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Invite New Teammate"
						},
						fields: {
							email: ['text', "Email Addresses separated by ','"],
							message: ['textarea', "Invitation Message", "Hey dude/dudette!\nCome work with me on my awesome project '" + resources.project().name + "'!"]
						}
					}
				}).result.then(result => {
					result.data.project = resources.project().name

					const emails = result.data.email.split(/\s*,\s*/)
					for (let i = 0; i < emails.length; i++) {
						result.data.url = $state.href('project.register', {}, { absolute: true }) + "?email=" + emails[i]

						rest.ajax("POST", resources.projectUrl() + "/users/invite", result.data)
							.then(() => console.log("Invitation sent to " + emails[i]))
					}
				})
			}

			this.displayName = resources.user().displayName;
			this.projectName = resources.project().name;
			socket.emit("addUser", { displayName: this.displayName, projectName: this.projectName });
			this.showFriends = false;
			this.receiver = "";
			this.publicMessages = [];
			this.privateMessages = {};
			this.messages = this.publicMessages;
			this.usersOnline = [];
			this.openChat = function () {
				this.showFriends = !this.showFriends;
				var _this = this;
				if (this.showFriends) {
					socket.emit('getAllUsers', { displayName: this.displayName, projectName: this.projectName }, function (data) {
						console.log(data);
						_this.usersOnline = data;
					});
				} else {
					this.chat = false;
				}
			}
			this.postMessage = function () {
				var msg = { text: this.words, from: this.displayName, to: this.receiver, time: new Date(), projectName: this.projectName };
				var rec = this.receiver;
				if (rec) {
					if (!this.privateMessages[rec]) {
						this.privateMessages[rec] = [];
					}
					this.privateMessages[rec].push(msg);
				} else {
					_this.publicMessages.push(msg);
				}
				this.words = "";
				if (rec !== this.displayName) {
					socket.emit("addMessage", msg);
				}
			};
			this.closeMsg = function () {
				this.chat = false;
			};
			this.setReceiver = function (receiver) {
				this.chat = true;
				this.receiver = receiver;
				if (receiver) {
					if (!this.privateMessages[receiver]) {
						this.privateMessages[receiver] = [];
					}
					this.messages = this.privateMessages[receiver];
				} else {
					_this.messages = _this.publicMessages;
				}
			};

			var _this = this;
			socket.on('userAdded', function (data) {
				if (data.projectName) {
					return;
				} else {
					_this.usersOnline.push(data);
				}

			});
			socket.on('allUser', function (data) {
				console.log(data);
				_this.usersOnline = data;
			});
			socket.on('userRemoved', function (data) {
				if (data.projectName) {
					return;
				} else {
					for (var i = 0; i < _this.usersOnline.length; i++) {
						if (_this.usersOnline[i].displayName == data.displayName) {
							_this.usersOnline.splice(i, 1);
							return;
						}
					}
				}

			})
			socket.on('messageAdded', function (data) {
				if (data.to) {
					if (!_this.privateMessages[data.from]) {
						_this.privateMessages[data.from] = [];
					}
					_this.privateMessages[data.from].push(data);
				} else {
					_this.publicMessages.push(data);
				}
			})


		}]
	})