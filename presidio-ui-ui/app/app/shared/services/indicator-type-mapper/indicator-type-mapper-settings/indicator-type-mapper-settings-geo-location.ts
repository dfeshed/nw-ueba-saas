(function () {
    'use strict';

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .factory('indicatorTypeMapper.geo-location', [
            function () {

                return {
                    settings: {
                        params: {
                            context_type: '{{entityTypeFieldName}}',
                            context_value: '{{entityName}}',
                            feature: '{{anomalyTypeFieldName}}',
                            'function': 'Count',
                            num_columns: 4
                        },
                        mapSettings: {

                            type: 'map',

                            mouseWheelZoomEnabled: true,
                            projection: 'miller',
                            imagesSettings: {
                                balloonText: '<span style="font-size:14px;"><b>[[title]]</b>: [[value]]%</span>',
                                selectable: false
                            },

                            areasSettings: {
                                selectedColor: '#024d88',
                                color: '#babdbe',
                                selectable: false
                            },
                            zoomControl: {
                                top: 1,
                                buttonSize: window.innerHeight < 700 ? 25 :
                                    window.innerHeight < 1000 ? 30 : 35
                            },
                            dataProvider: {
                                map: 'worldHigh',
                                getAreasFromMap: true,
                                zoomLevel: 1,
                                zoomLatitude: "",
                                zoomLongitude: ""
                            }


                        }
                    },

                };
            }]
        );
}());

