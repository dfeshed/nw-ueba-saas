(function () {
    'use strict';

    var fsIndicatorGraphsHandler;

    angular.module('Fortscale.shared.services.fsIndicatorGraphsHandler')
        .run(['$location', '$rootScope', 'fsIndicatorGraphsHandler',
            function (_$location_, _$rootScope_, _fsIndicatorGraphsHandler_) {

                // Get the service so it will be available on angular's event_loop phase
                fsIndicatorGraphsHandler = _fsIndicatorGraphsHandler_;
            }])
        .config(['fsIndicatorGraphsHandlerProvider', function (fsIndicatorGraphsHandlerProvider) {

            // Source Machine Click Handler
            fsIndicatorGraphsHandlerProvider.addIndicatorQuery({
                    anomalyTypeFieldName: 'source_machine'
                },
                /**
                 *
                 * @param {{entityName: string, anomalyValue: string, dataEntitiesIds: array<string>,
                 * startDate: number}} indicator
                 * @param {{name: string}} point
                 * @param identifier
                 */
                function sourceMachineIndicatorGraphHandler (indicator, point, identifier) {

                    if (point.name.toLocaleLowerCase() === "others") {
                        return;
                    }

                    var user, dataSourceId, sourceDevice, startTime, endDate, minScore;

                    // populate user and sourceDevice based on chart identifier
                    if (identifier === "left-column") {
                        user = indicator.entityName;
                        sourceDevice = point.name;
                    } else if (identifier === "right-column") {
                        user = point.name;
                        sourceDevice = indicator.anomalyValue;
                    } else {
                        return;
                    }

                    // Populate dataSourceId, startTime, endDate, minScore
                    dataSourceId = indicator.dataEntitiesIds[0];
                    startTime = moment.utc(indicator.startDate).subtract(90, 'days').valueOf();
                    endDate = moment.utc(indicator.startDate).valueOf();
                    minScore = 0;

                    // Populate filters and defaultFilters
                    var filters = "users.normalized_username=" + user + ',' +
                        dataSourceId + ".source_machine=" + sourceDevice;

                    var defaultFilters = dataSourceId + ".event_time_utc=:" + startTime + '::' + endDate + ',' +
                        dataSourceId + ".event_score=>=" + minScore;

                    // Go to Explore
                    fsIndicatorGraphsHandler
                        .goToExplore(dataSourceId, {filters: filters, default_filters: defaultFilters});

                }
            );

            // Source Machine Click Handler
            fsIndicatorGraphsHandlerProvider.addIndicatorQuery({
                    anomalyTypeFieldName: "destination_machine"
                },
                function destinationMachineIndicatorGraphHandler(indicator, point, identifier) {

                    if (point.name.toLocaleLowerCase() === "others") {
                        return;
                    }

                    var user, dataSourceId, targetDevice, startTime, endDate, minScore;

                    // populate user and targetDevice based on chart identifier
                    if (identifier === "left-column") {
                        user = indicator.entityName;
                        targetDevice = point.name;
                    } else if (identifier === "right-column") {
                        user = point.name;
                        targetDevice = indicator.anomalyValue;
                    } else {
                        return;
                    }

                    // Populate dataSourceId, startTime, endDate, minScore
                    dataSourceId = indicator.dataEntitiesIds[0];
                    startTime = moment.utc(indicator.startDate).subtract(90, 'days').valueOf();
                    endDate = moment.utc(indicator.startDate).valueOf();
                    minScore = 0;

                    // Populate filters and defaultFilters
                    var filters = "users.normalized_username=" + user + ',' +
                        dataSourceId + ".destination_machine=" + targetDevice;
                    var defaultFilters = dataSourceId + ".event_time_utc=:" + startTime + '::' + endDate + ',' +
                        dataSourceId + ".event_score=>=" + minScore;

                    // Go to Explore
                    fsIndicatorGraphsHandler
                        .goToExplore(dataSourceId, {filters: filters, default_filters: defaultFilters});
                }
            );

            // Country Click Handler
            fsIndicatorGraphsHandlerProvider.addIndicatorQuery({
                    anomalyTypeFieldName: "country"
                },
                function countryIndicatorGraphHandler(indicator, point, identifier) {

                    if (point.name.toLocaleLowerCase() === "others") {
                        return;
                    }

                    var user, dataSourceId, sourceCountry, startTime, endDate, minScore;

                    // populate user and targetDevice based on chart identifier
                    if (identifier === "left-column") {
                        user = indicator.entityName;
                        sourceCountry = point.name;
                    } else if (identifier === "right-column") {
                        user = point.name;
                        sourceCountry = indicator.anomalyValue;
                    } else {
                        return;
                    }

                    // Populate dataSourceId, startTime, endDate, minScore
                    dataSourceId = indicator.dataEntitiesIds[0];
                    startTime = moment.utc(indicator.startDate).subtract(90, 'days').valueOf();
                    endDate = moment.utc(indicator.startDate).valueOf();
                    minScore = 0;

                    // Populate filters and defaultFilters
                    var filters = "users.normalized_username=" + user + ',' +
                        dataSourceId + ".country=" + sourceCountry;
                    var defaultFilters = dataSourceId + ".event_time_utc=:" + startTime + '::' + endDate + ',' +
                        dataSourceId + ".event_score=>=" + minScore;

                    // Go to Explore
                    fsIndicatorGraphsHandler
                        .goToExplore(dataSourceId, {filters: filters, default_filters: defaultFilters});
                }
            );
        }]);

}());
