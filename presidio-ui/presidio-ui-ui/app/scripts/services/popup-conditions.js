(function () {

    "use strict";

    /**
     * Service for checking the conditions for displaying the popup to potentially prevent the user from running large
     * queries
     */
    function popupConditions (config) {

        var scoreThreshold = config.scoreThreshold;
        var daysThreshold = config.daysThreshold;

        function shouldNotifyPopup (params) {
            var minScore = 100;
            var timeStart = 0;
            var timeEnd = 0;
            var filter;
            //sanity - if no control params
            if (!params) {
                return "";
            }
            //if explore screen
            if (params.filters) {
                filter = params.filters;
                minScore = filter[1].value;
                timeStart = filter[0].value.timeStart;
                timeEnd = filter[0].value.timeEnd;
                //else - rest of the screens
            } else {
                filter = params.urlParams;
                //can only be one of these cases, order does not matter
                if (filter.top_events_dates) {
                    timeStart = filter.top_events_dates.split(",")[0];
                    timeEnd = filter.top_events_dates.split(",")[1];
                } else if (filter.notifications_events_dates) {
                    timeStart = filter.notifications_events_dates.split(",")[0];
                    timeEnd = filter.notifications_events_dates.split(",")[1];
                } else if (filter.notifications_events_dates) {
                    timeStart = filter.high_privileged_accounts_event_dates.split(",")[0];
                    timeEnd = filter.high_privileged_accounts_event_dates.split(",")[1];
                } else if (filter.notifications_events_dates) {
                    timeStart = filter.sensitive_resources_events_dates.split(",")[0];
                    timeEnd = filter.sensitive_resources_events_dates.split(",")[1];
                } else if (filter.ip_investigation_events_dates) {
                    timeStart = filter.ip_investigation_events_dates.split(",")[0];
                    timeEnd = filter.ip_investigation_events_dates.split(",")[1];
                } else if (filter.default_filters) {
                    timeStart = filter.default_filters.split(":")[1];
                    timeEnd = filter.default_filters.split(":")[3].split(",")[0];
                }

                if (filter.filters && filter.filters.indexOf("score") > 0) {
                    var array = filter.filters.split("=");
                    minScore = array[array.length - 1];
                } else if (filter.minscore !== undefined && _.isNumber(filter.minscore)) {
                    minScore = filter.minscore;
                }
            }

            var daysDiff = (timeEnd - timeStart) / (1000 * 60 * 60 * 24);
            if (minScore < scoreThreshold) {
                return config.popupScoreMessage;
            }
            if (daysDiff > daysThreshold) {
                return config.popupDaysMessage;
            }
            return "";
        }

        return {
            shouldNotifyPopup: shouldNotifyPopup
        };

    }

    popupConditions.$inject = ["config"];

    angular.module("PopupConditions", [])
        .factory("popupConditions", popupConditions);

})();
