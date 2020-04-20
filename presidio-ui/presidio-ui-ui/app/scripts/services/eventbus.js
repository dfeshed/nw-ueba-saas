(function () {
    "use strict";

    angular.module("Fortscale").factory("eventBus", ["EventBus", function (EventBus) {
        return new EventBus();
    }]);
}());
