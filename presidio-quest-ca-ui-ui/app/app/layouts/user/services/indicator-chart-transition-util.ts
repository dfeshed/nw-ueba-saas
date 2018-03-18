module Fortscale.layouts.user {


    export interface IIndicatorChartTransitionUtil {
        go (type:string, indicator:any, item:any):void;
    }


    class IndicatorChartTransitionUtil implements IIndicatorChartTransitionUtil {

        _transitions: any;

        _getFilterFieldNameByAnomalyContext (indicator, item) {
            return `${indicator.dataEntitiesIds[0]}.${indicator.anomalyTypeFieldName}=${item.item.originalCategory || item.item.category}`;
        }

        _getFilterFieldNameByEntityContext (indicator) {
            return `${indicator.dataEntitiesIds[0]}.normalized_username=${indicator.entityName}`;
        }

        _getDefaultFiltes (indicator, item) {
            let dateRange = this.dateRanges.getByDaysRange(90).replace(',', '::');
            return `${indicator.dataEntitiesIds[0]}.event_time_utc=:${dateRange},${indicator.dataEntitiesIds[0]}.event_score=>=0`
        }

        go (type:string, indicator:any, item:any):void {
            let transitionFn = this._transitions[type];
            if (transitionFn) {
                transitionFn(indicator, item);
            }
        }

        static $inject = ['dateRanges', '$location', '$rootScope'];

        constructor (public dateRanges:any, public $location:ng.ILocationService, public $rootScope: ng.IRootScopeService) {
            let ctrl = this;
            this._transitions = {
                columnAnomaly: function (indicator, item) {
                    ctrl.$rootScope.$applyAsync(() => {
                        ctrl.$location.path(`/d/explore/${indicator.dataEntitiesIds[0]}`)
                            .search({
                                filters: ctrl._getFilterFieldNameByAnomalyContext(indicator, item),
                                default_filters: ctrl._getDefaultFiltes(indicator, item)
                            });
                    });
                },
                columnEntity: function (indicator, item) {
                    ctrl.$rootScope.$applyAsync(() => {
                        ctrl.$location.path(`/d/explore/${indicator.dataEntitiesIds[0]}`)
                            .search({
                                filters: ctrl._getFilterFieldNameByEntityContext(indicator),
                                default_filters: ctrl._getDefaultFiltes(indicator, item)
                            });
                    });
                }
            };
        }
    }

    angular.module('Fortscale.shared.services.indicatorTypeMapper')
        .service('indicatorChartTransitionUtil', IndicatorChartTransitionUtil);
}
