(function () {
    'use strict';

    function DataView (Widget, utils) {

        return {
            /**
             * Returns the join property of the dataQuery, if required, otherwise returns null;
             * @param dataQueryConfig
             * @param currExplore
             */
            getDataQueryJoin: function (dataQueryConfig, currExplore) {

                //At this case if we have currExplore its mean that we must be consist with him and not with the one
                // that exist in "this" cause he keep the old explore that can be related to other entity
                if (currExplore) {
                    this.explore = currExplore;
                }

                var dataView = this;

                var entitiesJoinIndex = {};

                if (dataView.explore.dataEntity.linkedEntities) {

                    dataView.explore.dataEntity.linkedEntities.forEach(function (linkedEntity) {
                        entitiesJoinIndex[linkedEntity.entity] = linkedEntity;
                    });
                }

                if (!Object.keys(entitiesJoinIndex).length) {
                    return null;
                }

                return utils.objects.toArray(entitiesJoinIndex);
            },

            getDataQueryReport: function (dataQueryConfig, mockDataName, api) {
                return {
                    endpoint: {
                        api: api || "dataQuery",
                        dataQuery: dataQueryConfig
                    },
                    mock_data: "explore." + (mockDataName || dataQueryConfig.entity)
                };
            },
            /**
             * Given a string, returns the paramName relevant to this DataView, using the DataView's explore ID.
             * @param param
             * @returns {*|string}
             */
            getParamName: function (param) {
                var paramName = this.explore.settings.id || "";
                if (paramName) {
                    paramName += ".";
                }

                paramName += param;
                return paramName;
            },
            getWidgetFlags: function () {
                return {};
            },
            setParams: function () {
                // Implement this in each view, the view should be updated if the params are relevant to the view.
            },
            init: function (explore) {
                if (explore.constructor.name !== "Explore") {
                    throw new TypeError("Can't initialize DataView, expected an instance of Explore.");
                }

                this.explore = explore;
                this.setParams(explore.getState(), false);
            },
            /**
             * Interface only, should be implemented in objects that use DataView as prototype.
             */
            update: function () {
                throw new Error("update is not implemented for this dataView.");
            },
            get widget () {
                return this._widget;
            },
            set widget (widget) {
                if (!(widget instanceof Widget)) {
                    throw new TypeError("Invalid widget, expected an instance of Widget.");
                }

                this._widget = widget;
            }
        };

    }

    DataView.$inject = ["Widget", "utils"];

    angular.module("Explore.DataViews").factory("DataView", DataView);
})();
