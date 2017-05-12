"use strict"

angular
	.module('dashboard', ['resources','users','stories', 'sprints', 'meetings'])
	.component('dashboard', {
		templateUrl: "dashboard/dashboard.template.html",

        bindings: {
			meetings: '<',
            stories: '<',
            sprints: '<',
            users: '<',
            tasks: '<'
		},

        controller:['$uibModal', 'resources', function ($uibModal, resources) {

            this.names = [ "InProgress", "Not Started","Completed"];

            const meetingurl = resources.projectUrl() + "/meetings"
            const storyurl = resources.projectUrl() + "/stories"
            const sprinturl = resources.projectUrl() + "/sprints"
            const tasksList = []
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
                console.log("STORIES: " + this.stories)
                console.log("SPRINTS" + this.sprints)
                for(let i = 0; i < this.tasks.length; i++) {
                    console.log(this.tasks[i].description)
                }
                for(let i = 0; i < this.sprints.length; i++) {
                    console.log("DEBUGGING SPRINT" + this.sprints[i].sprintNumber)
                }
            }
            this.showsprint = function (sprinturl, sprints) {
                const currentDate = new Date ();
                resources.get(sprinturl)
                    .then(sprint => {
                        for(let i =0; i < sprint.length; i++){
                            // wnated to check for the whole week from start to finish so it keeps on shoinw the same sprint number
                            if(currentDate >= sprint[i].start){
                                sprints.sprintNmber.push(sprint[i])
                            } else if (currentDate <= sprint[i].end){
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