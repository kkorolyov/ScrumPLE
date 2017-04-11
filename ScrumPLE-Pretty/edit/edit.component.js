"use strict"

angular
	.module('edit', ['ui.bootstrap', 'ui.bootstrap.datetimepicker'])
	.component('edit', {
		templateUrl: "edit/edit.template.html",

		bindings: {
			resolve: '<',
			close: '&',
			dismiss: '&'
		},
		controller: ['resources', function (resources) {
			this.$onInit = function () {
				this.title = this.resolve.meta.title

				this.fields = this.resolve.fields

				this.edit = this.resolve.data ? true : false
				this.data = this.edit ? angular.copy(this.resolve.data) : {}	// Edit if binding, create otherwise
			}

			this.return = function (del) {
				this.close({
					$value: {
						data: this.data,
						del: del
					}
				})
			}
			this.ok = function () { this.return() }
			this.delete = function () { this.return(true) }
		}]
	})
