(function () {
    'use strict';

    function DataViewTypeClass () {
        function DataViewType (config) {
            this.name = config.name;
            this.id = config.id;
            this.icon = config.icon;
        }

        return DataViewType;
    }

    angular.module("Explore.DataViews").factory("DataViewType", DataViewTypeClass);

})();
