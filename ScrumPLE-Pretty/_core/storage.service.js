"use strict"

angular
	.module('storage', [])
	.factory('storage', function () {
		return {
			/**
			 * Gets or sets an item in session storage.
			 * @param {string} key item key
			 * @param {Object} [item] item to store, if === null, removes key
			 * @return {Object} stored item if invoke only with key arg
			 */
			storage: (key, item) => {
				if (item) sessionStorage.setItem(key, JSON.stringify(item))
				else if (item === null) sessionStorage.removeItem(key)
				else {
					const item = sessionStorage.getItem(key)
					return item ? JSON.parse(item) : null
				}
			}
		}
	})
