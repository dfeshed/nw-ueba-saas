module Fortscale.layouts.user {
    'use strict';
    import IActivityUserCountry = Fortscale.shared.services.entityActivityUtils.IActivityUserCountry;
    import IActivityOrganizationCountry = Fortscale.shared.services.entityActivityUtils.IActivityOrganizationCountry;
    import eEntityType = Fortscale.shared.services.entityActivityUtils.eEntityType;
    import IEntityActivityUtilsService = Fortscale.shared.services.entityActivityUtils.IEntityActivityUtilsService;
    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;
    import IAmMapsUtilsService = Fortscale.shared.services.amMaps.IAmMapsUtilsService;
    import IAmMapsUtilsLinesConfig = Fortscale.shared.services.amMaps.IAmMapsUtilsLinesConfig;

    declare var AmCharts:any;

    interface IAugmentedActivityUserCountry extends IActivityUserCountry {
        percent:number;
        alpha2?:string;
        anomaly?:boolean;
    }


    class ActivityCountriesController {

        _userCountries:IActivityUserCountry[];
        userCountries:IAugmentedActivityUserCountry[];
        // user:any;
        _organizationCountries:IActivityOrganizationCountry[];
        organizationCountries:IActivityOrganizationCountry[];
        mapAreas:{id:string, color?:string}[];
        map:any;
        areasOn:boolean = true;
        imagesOn:boolean = true;
        mapOriginalZoomX:number;
        mapOriginalZoomY:number;
        settings:any;
        indicator:any;
        eventCountries:any[];

        _INDICATORS_DATA_PATH_NAME:string = 'evidences';
        _HISTORICAL_DATA_PATH_NAME:string = 'historical-data';
        NANOBAR_ID:string = 'user-page';


        /**
         * Adds percent to each user-country
         * @returns {IAugmentedActivityUserCountry[]}
         * @private
         */
        _AugmentUserCountries ():IAugmentedActivityUserCountry[] {
            let userCountries =
                <IAugmentedActivityUserCountry[]>_.cloneDeep<IActivityUserCountry[]>(this._userCountries);
            let sum = _.sumBy(<any>userCountries, 'count');
            let percentSum = 0;
            _.each(userCountries, (userCountry, index, userCountries) => {
                userCountry.alpha2 = this.countryCodesUtil.getAlpha2ByCountryName(userCountry.country);

                if (userCountries.length - 1 === index) {
                    // Last item should always complete to 100%
                    userCountry.percent = Math.round((100 - percentSum) * 100) / 100;
                } else {
                    userCountry.percent = Math.round(userCountry.count / sum * 10000) / 100;
                    percentSum += userCountry.percent;
                }

            });



            return userCountries;
        }

        _sortUserCountries () {
            return _.orderBy<IAugmentedActivityUserCountry>(this.userCountries, [
                (userCountry: IAugmentedActivityUserCountry) => userCountry.country === 'Others',
                (userCountry: IAugmentedActivityUserCountry) => userCountry.count
            ], [
                'asc',
                'desc'
            ])
        }


        /**
         * Returns a list of areas to be used in the map
         * @returns {Array<{id: string, color: string}>}
         * @private
         */
        _getAreasForMapDataProvider ():{id:string, color?:string}[] {
            return _.map(this.organizationCountries, (orgCountry:IActivityOrganizationCountry) => {
                return {
                    id: this.countryCodesUtil.getAlpha2ByCountryName(orgCountry.country),
                    color: '#024d88'
                };
            });
        }

        /**
         * Returns a list of image map config objects.
         * @returns {IAmMapsUtilsBubbleConfig[]}
         * @private
         */
        _getBubbles () {
            return this.amMapsUtils.getBubbles({
                // Filter out countries without alpha2 (i.e. others) and map to return map config objects
                countries: _.map(_.filter(this.userCountries, 'alpha2'), userCountry => {
                    return {
                        alpha2: userCountry.alpha2,
                        name: userCountry.country,
                        value: userCountry.percent,
                        color: userCountry.anomaly ? 'rgba(255, 0, 0, 0.7)' : 'rgba(83, 194, 228, 0.5)',
                        anomaly: userCountry.anomaly
                    };
                })
            });
        }

        _getPlaneBubbles () {
            return this.amMapsUtils.getBubbles({
                // Filter out countries without alpha2 (i.e. others) and map to return map config objects
                countries: _.map(_.filter(this.eventCountries, 'alpha2'), eventCoutnry => {
                    return {
                        alpha2: eventCoutnry.alpha2,
                        name: eventCoutnry.name,
                        value: eventCoutnry.percent,
                        color: 'rgba(255, 0, 0, 0.6)'
                    };
                })
            });
        }

        /**
         * Returns a list of image map config objects.
         * @returns {IAmMapsUtilsBubbleConfig[]}
         * @private
         */
        _getLines ():IAmMapsUtilsLinesConfig[] {
            return this.amMapsUtils.getLines({
                // Filter out countries without alpha2 (i.e. others) and map to return map config objects
                countries: _.map(_.filter(this.eventCountries, 'alpha2'), eventCountry => {
                    return {
                        alpha2: eventCountry.alpha2,
                    };
                })
            });
        }

        /**
         * Returns a list of relevant map objects to be used as a zoom group
         * @param {{}} map
         * @param {boolean=} withoutOrganization
         * @param {boolean=} withoutUser
         * @returns {Array<{}>}
         * @private
         */
        _getRelevantMapObjects (map:any, withoutOrganization?:boolean, withoutUser?:boolean):any[] {

            let zoomToAreasIds;
            // Add organization map ids (if not withoutOrganization)
            if (withoutOrganization) {
                zoomToAreasIds = [];
            } else {
                zoomToAreasIds = _.map(this.organizationCountries,
                    (oCountry) => this.countryCodesUtil.getAlpha2ByCountryName(oCountry.country));
            }

            // Add user map ids (if not withoutUser)
            if (!withoutUser) {
                _.each(this.userCountries, (userCountry) => zoomToAreasIds.push(userCountry.alpha2));
            }

            //Add event countries
            _.each(this.eventCountries, (eventCountry) => zoomToAreasIds.push(eventCountry.alpha2));

        // take ids and get map objects. Push map object to zoomToAreas.
            let zoomToAreas = [];
            _.each(zoomToAreasIds, (id:string) => {
                if (id) {
                    let area = map.getObjectById(id);
                    if (area) {
                        zoomToAreas.push(area);
                    }
                }
            });
            return zoomToAreas;
        }


        /**
         * Initiates the amMap map object
         * @private
         */
        _initMap ():void {

            if (!this.userCountries || !this.organizationCountries || !this.eventCountries || this.map) {
                return;
            }

            let ctrl = this;
            let settings:any = {
                listeners: [
                    {
                        event: 'rendered',
                        method: (evt) => {
                            ctrl.mapOriginalZoomX = evt.chart.zoomX();
                            ctrl.mapOriginalZoomY = evt.chart.zoomY();

                            if (ctrl.settings.usePlanes) {
                                evt.chart.zoomDuration = 0;
                                evt.chart.zoomToGroup(ctrl._getRelevantMapObjects(evt.chart));
                                setTimeout(() => {
                                    evt.chart.zoomDuration = 1;
                                }, 1500);
                            } else {
                                setTimeout(() => {
                                   evt.chart.zoomToGroup(ctrl._getRelevantMapObjects(evt.chart));
                                }, 500);
                            }

                        }
                    }
                ],
                dataProvider: {
                    areas: this._getAreasForMapDataProvider(),
                    images: this.settings.usePlanes ? this._getPlaneBubbles() : this._getBubbles(),
                }

            };

            if (this.settings.usePlanes) {
                settings.dataProvider.lines = this._getLines();
                settings.dataProvider.images = _.concat(settings.dataProvider.images, this.amMapsUtils.getPlaneImage());
            }

            settings = _.merge({}, this.settings.mapSettings, settings);
            // Create a map
            this.map = AmCharts.makeChart('top-countries-map', settings);
        }

        /**
         * Rerenders the map in the previous zoom level and position
         * @private
         */
        _rerenderMap ():void {
            this.map.dataProvider.zoomLevel = this.map.zoomLevel();
            this.map.dataProvider.zoomLatitude = this.map.zoomLatitude();
            this.map.dataProvider.zoomLongitude = this.map.zoomLongitude();
            this.map.validateData();
            let groups = this._getRelevantMapObjects(this.map, !this.areasOn, !this.imagesOn);
            if (groups.length) {
                this.map.zoomToGroup(groups);
            } else {
                this.map.zoomTo(1, this.mapOriginalZoomX, this.mapOriginalZoomY);
            }
        }

        /**
         * Initiates user countries watch
         * @private
         */
        _initUserCountriesWatch ():void {
            this.$scope.$watch(
                () => this._userCountries,
                () => {
                    if (this._userCountries) {
                        this.userCountries = this._AugmentUserCountries();
                        this.userCountries = this._sortUserCountries();
                        this._initMap();
                    }
                }
            );
        }


        /**
         * Initiates watch on _organizationCountries. Clones it when arrives (for immutability)
         * @private
         */
        _initOrganizationCountriesWatch ():void {
            this.$scope.$watch(
                () => this._organizationCountries,
                () => {
                    if (this._organizationCountries) {
                        this.organizationCountries =
                            _.cloneDeep<IActivityOrganizationCountry[]>(this._organizationCountries);
                        this.mapAreas = this._getAreasForMapDataProvider();
                        this._initMap();
                    }
                }
            );
        }

        /**
         * Resize handler. Will redraw the map on a resize.
         *
         * @private
         */
        _initResizeWatch () {
            let ctrl = this;

            function resizeHandler () {
                ctrl.map.clear();
                ctrl.map = null;
                delete ctrl["map"];
                ctrl._initMap();
            }

            window.addEventListener('resize', resizeHandler, false);

            this.$scope.$on('$destroy', () => {
                window.removeEventListener('resize', resizeHandler, false);
            });
        }

        /**
         * A cb function that is used in a filter to remove out any members with a count of zero
         * @param {IActivityUserCountry} userCountry
         * @returns {boolean}
         */
        hideZeroCountFilter (userCountry:IActivityUserCountry):boolean {
            return userCountry.count !== 0;
        }

        /**
         * Changes the state of the map's Areas (i.e. organization).
         */
        toggleAreas ():void {
            if (!this.areasOn) {
                this.map.dataProvider.areas = this._getAreasForMapDataProvider();
            } else {
                this.map.dataProvider.areas = [];
            }
            this.areasOn = !this.areasOn;
            this._rerenderMap();
        }

        /**
         * Changes the state of the map's images (i.e. user bubbles)
         */
        toggleImages () {
            if (!this.imagesOn) {
                this.map.dataProvider.images = this._getBubbles();
            } else {
                this.map.dataProvider.images = [];
            }
            this.imagesOn = !this.imagesOn;
            this._rerenderMap();
        }


        /**
         * Start the user top countries load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadUserTopCountriesActivity ():ng.IPromise<void> {
            let params = this.interpolation.interpolate(this.settings.params, this.indicator);
            return this.$http.get(
                `${this.BASE_URL}/${this._INDICATORS_DATA_PATH_NAME}/${this.indicator.id}/${this._HISTORICAL_DATA_PATH_NAME}`,
                {params: params})
                .then((res:any) => {
                    this._userCountries = _.filter(_.map(res.data.data, (countryItem:any) => {
                        return {country: countryItem.keys[0], count: countryItem.value, anomaly: countryItem.anomaly};
                    }), (countryItem: any) => {
                        // filter out all elements without a country
                        return !!countryItem.country;
                    });


                    let eventCountriesTemp:any[] = [];

                    _.each(res.data.info && res.data.info.countries,(country)=>{
                        let eventCountry:any = {};
                        eventCountry.name = country;
                        eventCountry.percent = 50;
                        eventCountry.alpha2 = this.countryCodesUtil.getAlpha2ByCountryName(country);

                        eventCountriesTemp.push(eventCountry);
                    });
                    this.eventCountries=eventCountriesTemp;

                    return this._userCountries;
                })
                .catch((err) => {
                    console.error('There was an error loading organization top countries.', err);
                    this._userCountries = [];
                });
        }


        /**
         * Starts the organization top countries load
         * @returns {IPromise<void>}
         * @private
         */
        _initLoadOrganizationTopCountriesActivity ():ng.IPromise<void> {
            return this.entityActivityUtils.getTopCountries<IActivityOrganizationCountry>(eEntityType.ORGANIZATION)
                .then((countries:IActivityOrganizationCountry[]) => {
                    this._organizationCountries = countries;
                })
                .catch((err) => {
                    console.error('There was an error loading organization top countries.', err);
                    this._organizationCountries = [];
                });
        }


        $onInit () {

            this._initUserCountriesWatch();
            this._initOrganizationCountriesWatch();

            this._initResizeWatch();

            //this._initEventsLoadedWatch();

            this.$scope.$watch(
                () => this.indicator,
                (indicator) => {
                    if (indicator) {
                        // let promiseUser = this._initLoadUserTopCountriesActivity();
                        this.fsNanobarAutomationService.addPromises(this.NANOBAR_ID, [
                            this._initLoadUserTopCountriesActivity(),
                            this._initLoadOrganizationTopCountriesActivity()
                        ]);
                    }
                }
            );


        }

        static $inject = ['$scope', '$element', 'countryCodesUtil', 'amMapsUtils', 'entityActivityUtils',
            'interpolation', '$http', 'BASE_URL', 'fsNanobarAutomationService'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery, public countryCodesUtil:any,
            public amMapsUtils:IAmMapsUtilsService, public entityActivityUtils:IEntityActivityUtilsService,
            public interpolation:any, public $http:ng.IHttpService, public BASE_URL:string,
            public fsNanobarAutomationService:INanobarAutomationService) {
        }
    }

    let indicatorAmGeoLocationComponent:ng.IComponentOptions = {
        controller: ActivityCountriesController,
        templateUrl: 'app/layouts/user/components/user-indicator/components/fs-indicator-am-geo-location/fs-indicator-am-geo-location.component.html',
        bindings: {
            indicator: '<',
            user: '<userModel',
            settings: '<'
        }
    };

    angular.module('Fortscale.layouts.user')
        .component('fsIndicatorAmGeoLocation', indicatorAmGeoLocationComponent);
}
