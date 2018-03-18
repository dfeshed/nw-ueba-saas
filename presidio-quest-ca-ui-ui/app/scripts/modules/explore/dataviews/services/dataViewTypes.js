(function () {
    "use strict";

    function dataViewTypes (DataViewType) {
        var types = [{id: "table", name: "Table", icon: "#table-icon"},
                {id: "graphs", name: "Graphs", icon: "#graph-icon"}].map(function (typeConfig) {
                    return new DataViewType(typeConfig);
                }),
            index = {};

        types.forEach(function (type) {
            index[type.id] = type;
        });

        return {
            typesArray: types,
            types: index
        };
    }

    dataViewTypes.$inject = ["DataViewType"];

    angular.module("Explore.DataViews").factory("dataViewTypes", dataViewTypes);
})();
