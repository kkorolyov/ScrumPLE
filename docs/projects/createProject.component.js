"use strict"

angular
    .module('projects')
    .component('createProject', {
        templateUrl: "projects/createProject.template.html",

        controller: ['resources', function (resources) {

            this.addProject = function (name, description, visible, handle, password) {
				resources.set("projects", {
					name: this.projectName,
					description: this.description,
					visible: this.visible,
					owner: {
						credentials: {
							handle: this.handle,
							password: this.password
						},
						displayName: this.handle
						
					}
                })
            }
        }]
    })