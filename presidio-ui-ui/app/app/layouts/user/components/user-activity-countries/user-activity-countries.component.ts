module Fortscale.layouts.user {
    'use strict';
    import IActivityUserCountry = Fortscale.shared.services.entityActivityUtils.IActivityUserCountry;
    import IActivityOrganizationCountry = Fortscale.shared.services.entityActivityUtils.IActivityOrganizationCountry;
    import IAmMapsUtilsService = Fortscale.shared.services.amMaps.IAmMapsUtilsService;

    declare var AmCharts:any;

    interface IAugmentedActivityUserCountry extends IActivityUserCountry {
        percent:number,
        alpha2?:string
    }


    class ActivityCountriesController {

        _userCountries:IActivityUserCountry[];
        userCountries:IAugmentedActivityUserCountry[];
        user:any;
        _organizationCountries:IActivityOrganizationCountry[];
        organizationCountries:IActivityOrganizationCountry[];
        mapAreas:{id:string, color?:string}[];
        map:any;
        areasOn:boolean = true;
        imagesOn:boolean = true;
        mapOriginalZoomX:number;
        mapOriginalZoomY:number;

        /**
         * Adds percent to each user-country
         * @returns {IAugmentedActivityUserCountry[]}
         * @private
         */
        _AugmentUserCountries ():IAugmentedActivityUserCountry[] {
            let userCountries =
                <IAugmentedActivityUserCountry[]>_.cloneDeep<IActivityUserCountry[]>(this._userCountries);
            let sum = _.sumBy(<any>userCountries, 'count');
            _.each(userCountries, userCountry => {
                userCountry.percent = Math.round(userCountry.count / sum * 10000) / 100;
                userCountry.alpha2 = this.countryCodesUtil.getAlpha2ByCountryName(userCountry.country)
            });

            return userCountries;
        }

        /**
         * Sorts user countries
         *
         * @returns {IAugmentedActivityUserCountry[]}
         * @private
         */
        _sortUserCountries ():IAugmentedActivityUserCountry[] {

            return _.orderBy<IAugmentedActivityUserCountry, IAugmentedActivityUserCountry>(
                this.userCountries,
                [
                    (userCountry:IAugmentedActivityUserCountry) => userCountry.country === 'Others',
                    'count'
                ],
                [
                    'asc',
                    'desc'
                ]);
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
                map: this.map,
                // Filter out countries without alpha2 (i.e. others) and map to return map config objects
                countries: _.map(_.filter(this.userCountries, 'alpha2'), userCountry => {
                    return {
                        alpha2: userCountry.alpha2,
                        name: userCountry.country,
                        value: userCountry.percent,
                        color: 'rgba(83, 194, 228, 0.5)'
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

            // take ids and get map objects. Push map object to zoomToAreas.
            let zoomToAreas = [];
            _.each(zoomToAreasIds, (id:string) => {
                let area = map.getObjectById(id);
                if (area) {
                    zoomToAreas.push(area);
                }
            });
            return zoomToAreas;
        }


        /**
         * Initiates the amMap map object
         * @private
         */
        _initMap ():void {

            if (!this.userCountries || !this.organizationCountries) {
                return;
            }

            let ctrl = this;

            // Create a map
            this.map = AmCharts.makeChart('top-countries-map', {

                type: 'map',

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
                listeners: [
                    {
                        event: 'rendered',
                        method: (evt) => {
                            ctrl.mapOriginalZoomX = evt.chart.zoomX();
                            ctrl.mapOriginalZoomY = evt.chart.zoomY();
                            setTimeout(() => {
                                evt.chart.zoomToGroup(ctrl._getRelevantMapObjects(evt.chart));
                            }, 500);
                        }
                    }
                ],
                zoomControl: {
                    top: 1,
                    buttonSize: window.innerHeight < 700 ? 25 :
                        window.innerHeight < 1000 ? 30 : 35
                },
                dataProvider: {
                    map: 'worldHigh',
                    getAreasFromMap: true,
                    areas: this._getAreasForMapDataProvider(),
                    images: this._getBubbles(),
                    zoomLevel: 1,
                    zoomLatitude: "",
                    zoomLongitude: ""
                }

            });
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

        $onInit () {
            this._initUserCountriesWatch();
            this._initOrganizationCountriesWatch();
            this._initResizeWatch();
        }

        static $inject = ['$scope', '$element', 'countryCodesUtil', 'amMapsUtils'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery, public countryCodesUtil:any,
            public amMapsUtils:IAmMapsUtilsService) {
        }
    }

    let userActivityCountriesComponent:ng.IComponentOptions = {
        controller: ActivityCountriesController,
        templateUrl: 'app/layouts/user/components/user-activity-countries/user-activities-countries.component.html',
        bindings: {
            _userCountries: '<userCountries',
            _organizationCountries: '<organizationCountries',
            user: '<userModel'
        }
    };

    angular.module('Fortscale.layouts.user')
        .component('userActivityCountries', userActivityCountriesComponent);
}
