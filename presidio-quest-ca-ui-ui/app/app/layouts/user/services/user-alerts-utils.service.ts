module Fortscale.layouts.user {

    import IAppConfigService = Fortscale.appConfigProvider.IAppConfigService;
    import IIndicator = Fortscale.shared.interfaces.IIndicator;
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    import ITagsUtilsService = Fortscale.shared.services.tagsUtilsService.ITagsUtilsService

    //Interface for alert utils service
    export interface IUserAlertsUtilsService {

        getAlertDescription (alert:{name:string}):string;
        getAlertRelatedText (alert:{name:string}):string;
        getDataSources (evidencesList:IIndicator[]) : string;
        getTags(tagEvidencesList:IIndicator[]) : ng.IPromise<string>;

    }

    class UserAlertsUtilsService implements IUserAlertsUtilsService {

        _tagsCache: ITagDefinition[];

        _CONFIG_LOCALE_KEY = 'system.locale.settings';
        _CONFIG_ALERT_KEY_PREFIX = `messages.${this.appConfig.getConfigItem(
            this._CONFIG_LOCALE_KEY).value}.alert`;
        _ALERT_CONFIG_DESCRIPTION_KEY = 'desc';
        _ALERT_CONFIG_RELATED_KEY='related'


        /**
         * Returns an alerts's description
         * @param indicator
         * @returns {any}
         */
        getAlertDescription (alert:{name:string}):string {
            let configItem = this.appConfig.getConfigItem(
                `${this._CONFIG_ALERT_KEY_PREFIX}.${alert.name}.${this._ALERT_CONFIG_DESCRIPTION_KEY}`);

            if (configItem && configItem.value) {
                return configItem.value;
            }

            return alert.name;
        }

        getAlertRelatedText (alert:{name:string}):string {
            let configItem = this.appConfig.getConfigItem(
                `${this._CONFIG_ALERT_KEY_PREFIX}.${alert.name}.${this._ALERT_CONFIG_RELATED_KEY}`);

            if (configItem && configItem.value) {
                return configItem.value;
            }

            return alert.name;
        }


        /**
         * The string for data sources with seperator from all the evidences, without duplication
         * @param evidencesList
         * @returns {string}
         */
        getDataSources (evidencesList:IIndicator[]) : string{


            let dataSources:string[]=_.map(evidencesList, (evidence) => {
                return this.$filter("entityIdToName")(evidence.dataEntitiesIds[0]);
            } );

            dataSources = _.uniq(dataSources);
            return dataSources.join(", ");

        }

        /**
         * The string for tag names with seperator from all tag evidences, without duplication
         * @param tagEvidencesList
         * @returns {promise}
         */
        getTags(tagEvidencesList:IIndicator[]) : ng.IPromise<string>{


            let tags:string[]=_.map(tagEvidencesList, (evidence) => {
                return evidence.anomalyValue;
            } );
            return this.$q((resolve, reject) => {
                if (this._checkIfAllTagsInCache(tags)){
                    resolve(this._getTagsDisplayNames(tags));
                } else {
                    this.tagUtilsService.getTags().then((tagList:{data:ITagDefinition[]})=>{
                        this._tagsCache = tagList.data;
                        resolve(this._getTagsDisplayNames(tags));
                    });
                }

            });


        }

        _checkIfAllTagsInCache (tags : string[]): boolean{
            let allTagExits:boolean = true;
            _.each(tags, (tag:string) => {
                let tagObject:ITagDefinition = _.find(this._tagsCache, {name: tag});
                if (!tagObject) {
                    allTagExits = false;
                }
            });
            return allTagExits;
        }

        /*
         * We assume that all tags exists in cache
         */
        _getTagsDisplayNames (tags : string[]): string{
            let prettyTags:string[] = [];
            _.each(tags, (tagName:string) => {
                let tag:ITagDefinition = _.find(this._tagsCache, {name: tagName});
                if (tag) {
                    prettyTags.push(tag.displayName);
                } else {
                    prettyTags.push(tagName);
                }
            });
            return prettyTags.join(", ");
        }

        static $inject = ['appConfig', '$filter','tagsUtils','$q'];

        constructor (public appConfig:IAppConfigService, public $filter:any,
                     public tagUtilsService:ITagsUtilsService, public $q:ng.IQService) {

        }
    }

    angular.module('Fortscale.layouts.user')
        .service('userAlertsUtils', UserAlertsUtilsService)
}
