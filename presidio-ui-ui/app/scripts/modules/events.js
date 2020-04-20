(function(){
    'use strict';



    /**
     * Manages app-wide events for operations that can't be done by function calls
     * @param EventBus
     * @returns {{triggerDashboardEvent: triggerDashboardEvent}}
     */
    function events(EventBus){
        function triggerDashboardEvent(event, data, params){
            var eventObj = {};

            if (event.constructor === Array) {
                eventObj.events = event;

            } else {
                eventObj.event = event;
            }

            if (data) {
                eventObj.data = data;
            }

            if (params) {
                eventObj.params = params;
            }

            eventsEventBus.triggerEvent("dashboardEvent", eventObj);
        }

        var api = {
            triggerDashboardEvent: triggerDashboardEvent
        };

        var eventsEventBus = EventBus.setToObject(api, ["dashboardEvent"]);

        return api;


    }

    events.$inject = ["EventBus"];

    angular.module("Events", ["EventBus"])
        .factory("events", events);


})();
