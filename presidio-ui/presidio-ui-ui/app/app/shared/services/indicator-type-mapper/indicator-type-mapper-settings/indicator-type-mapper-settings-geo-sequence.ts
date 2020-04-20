(function () {
    'use strict';

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .factory('indicatorTypeMapper.geo-sequence', [
            function () {

                return {
                    settings: {
                        params: {
                            context_type: '{{entityTypeFieldName}}',
                            context_value: '{{entityName}}',
                            function: 'Count',
                            num_columns: 5,
                            feature: 'country'
                        },
                        usePlanes: true,
                        title: 'Geo Location Sequence Anomaly',
                        mapSettings: {

                            type: 'map',
                            mouseWheelZoomEnabled: true,
                            projection: 'miller',
                            imagesSettings: {
                                balloonText: '<span style="font-size:14px;"><b>[[title]]</b>: [[value]]%</span>',
                                selectable: false,
                                color: "#585869",
                                rollOverColor: "#585869",
                                selectedColor: "#585869",
                                pauseDuration: 0.2,
                                animationDuration: 2.5,
                                adjustAnimationSpeed: true
                            },
                            linesSettings: {
                                color: "#FF0000",
                                alpha: 0.6
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

