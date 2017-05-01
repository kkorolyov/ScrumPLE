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
            const tasks = []
            const sprint = []
            this.user = resources.user()
            function toDate(meeting) {
                meeting.start = new Date(meeting.start)
                meeting.end = new Date(meeting.end)
                return meeting
            }
            function presentify(meetings) {
                for(let i = 0; i < meetings.length; i++) {
                    toDate(meetings[i])
                }
                return meetings
            }
            this.$onInit = function() {
                presentify(this.meetings)
                console.log("DEBUGGING: " + this.meetings)
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