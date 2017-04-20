"use strict"

angular
	.module('users', [])
	.component('users', {
		templateUrl: "users/users.template.html",

		bindings: {
			users: '<'
		},
		controller: ['$state', '$uibModal', 'resources', 'rest', 'socket', function ($state, $uibModal, resources, rest, socket) {
			/*this.messageLog = () => "This is a dummy message log"
			this.send = function () {
				console.log(this.message)
				delete this.message
			}*/

			this.add = function () {
				$uibModal.open({
					component: 'edit',
					resolve: {
						meta: {
							title: "Invite New Teammate"
						},
						fields: {
							email: ['email', "Email Address"],
							message: ['textarea', "Invitation Message", "Hey dude/dudette!\nCome work with me on my awesome project '" + resources.project().name + "'!"]
						}
					}
				}).result.then(result => {
					result.data.project = resources.project().name
					result.data.url = $state.href('project.register', {}, { absolute: true })

					rest.ajax("POST", resources.projectUrl() + "/users/invite", result.data)
						.then(() => console.log("Invitation sent"))
				})
			}
            this.displayName=resources.user().displayName;
            this.showFriends=false;
            this.receiver="";
            this.privateMessages={};
            this.messages=this.privateMessages;
            this.users=[];
            this.openChat=function(){
                this.showFriends= !this.showFriends;
                var _this=this;
                if(this.showFriends){
                    socket.emit('getAllUsers',{displayName:this.displayName},function(data){
                        console.log(data);
                        _this.users=data;
                    });
                }else{
                    this.chat=false;

                }
            }
            this.postMessage=function(){
                this.chat=true;
                this.receiver=receiver;
                if(!this.privateMessages[receiver]){
                    this.privateMessages[receiver]=[];
                }
                this.messages=this.privateMessages[receiver];
            }
            var _this=this;
            socket.on('userAdded', function(data) {
                _this.users.push(data);
            });
            socket.on('allUser', function(data) {
                _this.users=data;
            });
            socket.on('userRemoved', function(data) {
                for(var i=0;i<_this.users.length;i++) {
                    if (_this.users[i].displayName == data.displayName) {
                        _this.users.splice(i, 1);
                        return;
                    }
                }
            })
            socket.on('messageAdded', function(data) {
                if(data.to){
                    if(!_this.privateMessages[data.from]){
                        _this.privateMessages[data.from]=[];
                    }
                    _this.privateMessages[data.from].push(data);
                }
            })


        }]
	})