(function () {
	'use strict';

	angular.module('Fortscale.shared', [
		'Fortscale.shared.services',
		'Fortscale.shared.directives',
		'Fortscale.shared.components',
        'Fortscale.shared.filters',
        'Fortscale.shared.service.fsDownloadFile',
	]);
}());
