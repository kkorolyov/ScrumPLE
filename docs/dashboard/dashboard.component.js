"use strict"

angular
	.module('dashboard', ['resources','users','stories', 'sprints', 'meetings'])
	.component('dashboard', {
		templateUrl: "dashboard/dashboard.template.html",

        bindings: {
			meetings: '<',
            stories: '<',
            sprints: '<',
            users: '<'
		},

        controller:['$uibModal', 'resources', function ($uibModal, resources) {

            this.names = [ "InProgress", "Not Started","Completed"];

            const meetingurl = resources.projectUrl() + "/meetings"
            const storyurl = resources.projectUrl() + "/stories"
            const sprinturl = resources.projectUrl() + "/sprints"

            const meetings = []
            const tasks = []
            const sprint = []
            this.user = resources.user()

            this.showmeeting = function (meetingurl, meetings) {
                const currentDate = new Date ();
                resources.get(meetingurl)
                    .then(meeting => {
                        for(let i =0; i < meeting.length; i++){
                            if(meeting[i].start && meeting[i].end === currentDate){
                                meetings.push(meeting[i])
                            }
                        }
                    
                    })
                    return meetings
            }

            this.showsprint = function (sprinturl, sprints) {
                const currentDate = new Date ();
                resources.get(sprinturl)
                    .then(sprint => {
                        for(let i =0; i < sprint.length; i++){
                            // wnated to check for the whole week from start to finish so it keeps on shoinw the same sprint number
                            if(sprint[i].start === currentDate){
                                sprints.sprintNmber.push(sprint[i])
                            }
                        }
                    })
                    return sprints
            }

            this.showstories = function (storyurl, stories){
                
            }
        }]
    })