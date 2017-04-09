"use strict"

angular
	.module('meetings')
	.component('edit', {
		templateUrl: "meetings/edit.template.html",

		bindings: {
			resolve: '<',
			close: '&',
			dismiss: '&'
		},
		controller: ['resources', function (resources) {
			this.$onInit = function () {
				this.edit = this.resolve.meeting ? true : false
				this.action = this.edit ? "Edit" : "Create"

				this.meeting = this.edit ? this.resolve.meeting : {}	// Edit if binding, create otherwise
			}

			this.return = function (del) {
				this.close({
					$value: {
						meeting: this.meeting,
						del: del
					}
				})
			}
			this.ok = function () { this.return() }
			this.delete = function () { this.return(true) }
		}]
	})
