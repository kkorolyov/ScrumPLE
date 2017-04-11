"use strict"

angular
	.module('title', ['resources'])
	.factory('title', ['resources', function (resources) {
		let _title = () => ""

		return {
			/**
			 * Gets or sets the current page title.
			 * @param {string} [title] new page title
			 * @return current page title if invoked without arguments
			 */
			title: function (title) {
				if (title) _title = () => title
				else return _title()
			},

			/**
			 * Sets a subtitle of the current project.
			 * @param {string} [subTitle] project subtitle
			 */
			projectSub: function (subTitle) {
				_title = () => resources.project().name + (subTitle ? " - " + subTitle : "")
			}
		}
	}])