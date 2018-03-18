module Fortscale.layouts.user {

    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;
    import IIndicator = Fortscale.shared.interfaces.IIndicator;

    export interface IUserIndicatorsUtilsService {
        orderIndicators (indicators:IIndicator[]):any[];
        filterIndicators (indicators:IIndicator[]):any[];
        getIndicatorDescription (indicator:IIndicator):string;
        getIndicatorTimelineDescription (indicator:IIndicator):string;
        getIndicatorSymbolName (indicator:IIndicator):string;
        getTagsIndicators (indicators:any[]):any[];
    }

    class UserIndicatorsUtilsService implements IUserIndicatorsUtilsService {


        _CONFIG_LOCALE_KEY = 'system.locale.settings';
        _CONFIG_INDICATOR_KEY_PREFIX = `messages.${this.appConfig.getConfigItem(
            this._CONFIG_LOCALE_KEY).value}.evidence`;
        _INDICATOR_CONFIG_DESCRIPTION_KEY = 'desc';
        _INDICATOR_CONFIG_TIME_LINE_KEY = 'timeline';


        /**
         * Returns an indicator's description
         * @param indicator
         * @returns {any}
         */
        getIndicatorDescription (indicator:{anomalyTypeFieldName:string, dataEntitiesIds:string[]}):string {
            //Try specific per DS setting.
            let configItem = this.appConfig.getConfigItem(
                `${this._CONFIG_INDICATOR_KEY_PREFIX}.${indicator.dataEntitiesIds[0]}.${indicator.anomalyTypeFieldName}.${this._INDICATOR_CONFIG_DESCRIPTION_KEY}`);

            if (configItem && configItem.value) {
                return configItem.value;
            }
            //If not data source settings, take the geenral one
            configItem = this.appConfig.getConfigItem(
                `${this._CONFIG_INDICATOR_KEY_PREFIX}.${indicator.anomalyTypeFieldName}.${this._INDICATOR_CONFIG_DESCRIPTION_KEY}`);

            if (configItem && configItem.value) {
                return configItem.value;
            }

            return '';
        }

        /**
         * Returns an indicator timeLine description
         * @param indicator
         * @returns {any}
         */
        getIndicatorTimelineDescription (indicator:IIndicator):string {

            let configItem = this.appConfig.getConfigItem(
                `${this._CONFIG_INDICATOR_KEY_PREFIX}.${indicator.dataEntitiesIds[0]}.${indicator.anomalyTypeFieldName}.${this._INDICATOR_CONFIG_TIME_LINE_KEY}`);

            if (!configItem || !configItem.value) {
                configItem = this.appConfig.getConfigItem(
                    `${this._CONFIG_INDICATOR_KEY_PREFIX}.${indicator.anomalyTypeFieldName}.${this._INDICATOR_CONFIG_TIME_LINE_KEY}`);
            }
            if (configItem && configItem.value) {
                let filterFn: (any, IIndicator)=>string = this.$filter('anomalyTypeFormatter');
                let locals = {
                    value: filterFn(indicator.anomalyValue, indicator),
                    username: indicator.entityName,
                    entityName: indicator.entityName
                };

                return this.$interpolate(configItem.value)(locals);
            }

            return '';
        }

        /**
         * Returns an indicator's symbol name (svg icon)
         * @param indicator
         * @returns {string}
         */
        getIndicatorSymbolName (indicator:any):string {
            return this.indicatorSymbolMap.getSymbolName(indicator);
        }

        /**
         * Orders the indicators list and returns a sorted list
         * @param indicators
         * @returns {T[]}
         */
        orderIndicators (indicators:any[]):any[] {
            if (!indicators) {
                return;
            }

            return _.orderBy(indicators, ['startDate'], ['asc']);
        }

        /**
         * Filters the indicators list (takes out 'tag' indicators) and returns the list
         * @param indicators
         * @returns {any[]|string[]|T[]}
         */
        filterIndicators (indicators:any[]):any[] {
            if (!indicators) {
                return;
            }

            return _.filter(indicators, (indicator:any) => indicator.anomalyTypeFieldName !== 'tag');
        }

        /**
         * Get subset of tag indicators only
         * @param indicators
         * @returns {any[]|string[]|T[]}
         */
        getTagsIndicators (indicators:any[]):any[] {
            if (!indicators) {
                return;
            }

            return _.filter(indicators, (indicator:any) => indicator.anomalyTypeFieldName === 'tag');
        }

        static $inject = ['appConfig', 'indicatorSymbolMap', '$interpolate', '$filter'];

        constructor (public appConfig:IAppConfigService, public indicatorSymbolMap:IIndicatorSymbolMapService,
            public $interpolate:ng.IInterpolateService, public $filter: any) {
        }
    }

    angular.module('Fortscale.layouts.user')
        .service('userIndicatorsUtils', UserIndicatorsUtilsService)
}
