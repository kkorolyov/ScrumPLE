"use strict"

angular
	.module('title', [])
	.factory('title', function () {
		const obj = {
			title: function () {
				return this.title
			},
			default: function () {
				this.title = "ScrumPLE"
			}
		}
		obj.default()
		
		return obj
	})