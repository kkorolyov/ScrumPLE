"use strict"

angular
	.module('title', [])
	.factory('title', function () {
		let _title = ""

		return {
			/**
			 * Gets or sets the current page title.
			 * @param {string} [title] new page title
			 * @return current page title if invoked without arguments
			 */
			title: function (title) {
				if (title) _title = title
				else return _title
			}
		}
	})