(function () {
	'use strict';

	angular.module('Fortscale.shared.services', [
		'Fortscale.shared.services.assert',
		'Fortscale.shared.services.dependencyMounter',
        'Fortscale.shared.services.objectUtils',
        'Fortscale.shared.services.interpolation',
        'Fortscale.shared.services.CSVConverter',
        'Fortscale.shared.services.URLUtils',
        'Fortscale.shared.services.tableSettingsUtil',
        'Fortscale.shared.services.jsonLoader',
        'Fortscale.shared.services.indicatorTypeMapper',
        'Fortscale.shared.services.modelUtils',
        'Fortscale.shared.services.fsModals',
        'Fortscale.shared.services.dateRanges',
        'Fortscale.shared.services.tagsUtils',
        'Fortscale.shared.services.countryCodesUtil',
        'Fortscale.shared.services.fsIndicatorTypes',
        'Fortscale.shared.services.fsIndicatorGraphsHandler',
        'Fortscale.shared.services.fsIndexedDBService',
        'Fortscale.shared.services.stringUtils',
        'Fortscale.shared.services.fsIndicatorErrorCodes',
        'Fortscale.shared.services.fsNanobarAutomation',
        'Fortscale.shared.services.entityActivityUtils',
        'Fortscale.shared.services.toastrService',
        'Fortscale.shared.services.fsWebsocketUtils'
	]);
}());
