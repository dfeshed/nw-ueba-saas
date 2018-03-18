module Fortscale.shared.services.amMaps {
    'use strict';
    export interface ILatLong {
        latitude:number,
        longitude:number
    }

    export interface IAmMapsUtilsInsertCountriesConfig {
        alpha2:string;
        name:string;
        value:number,
        color?:string,
        anomaly?:boolean
    }

    export interface IAmMapsUtilsInsertConfig {
        map:any,
        minBubbleSize?:number,
        maxBubbleSize?:number,
        countries:IAmMapsUtilsInsertCountriesConfig[]
    }

    export interface IAmMapsUtilsGetBubblesConfig {
        map?:any,
        minBubbleSize?:number,
        maxBubbleSize?:number,
        countries:IAmMapsUtilsInsertCountriesConfig[]
    }

    export interface IAmMapsUtilsGetLinesConfig {
        minBubbleSize?:number,
        maxBubbleSize?:number,
        countries:{
            alpha2:string
        }[]
    }

    export interface IAmMapsUtilsBubbleConfig {
        type?:string,
        svgPath?:string,
        width:number,
        height:number,
        color:string,
        longitude:number,
        latitude:number,
        title:string,
        value:number
    }

    export interface IAmMapsUtilsPlanesConfig {
        svgPath:string;
        positionOnLine:number;
        color:string;
        animateAlongLine:boolean;
        lineId:string;
        flipDirection:boolean;
        loop:boolean;
        scale:number;
        positionScale:number;
        alpha?:number;
    }

    export interface IAmMapsUtilsLinesConfig {
        id:string;
        arc:number;
        alpha:number;
        latitudes:number[];
        longitudes:number[];
    }

    export interface IAmMapsUtilsService {
        getLatLongByAlpha2Code (Alpha2:string):ILatLong;
        insertBubbles (config:IAmMapsUtilsInsertConfig):void;
        getBubbles (config:IAmMapsUtilsGetBubblesConfig):IAmMapsUtilsBubbleConfig[];
        getLines (config:IAmMapsUtilsGetLinesConfig):IAmMapsUtilsLinesConfig[];
        getPlaneImage ():IAmMapsUtilsPlanesConfig[];
    }


    const amMapsUtilsInsertConfigDefault = {
        minBubbleSize: 10,
        maxBubbleSize: 30
    };

    class AmMapsUtils implements IAmMapsUtilsService {

        _ERR_MSG:'amMaps: amMapsUtils.service: ';

        /**
         * Validates inser config countries
         * @param {Array<IAmMapsUtilsInsertCountriesConfig>} countries
         * @param {string} errMsg
         * @private
         */
        _validateCountries (countries:IAmMapsUtilsInsertCountriesConfig[], errMsg:string):void {
            this.assert.isArray(countries, 'countries', errMsg);
            _.each(countries, (country:IAmMapsUtilsInsertCountriesConfig, index) => {
                this.assert.isString(country.alpha2, `countries: index: ${index}, country.alpha2`, errMsg);
                this.assert.isString(country.name, `countries: index: ${index}, country.name`, errMsg);
                this.assert(_.isNumber(country.value),
                    errMsg + `countries: index: ${index}, country.value: Must be a number.`, TypeError);
            });
        }

        /**
         * Validates insert bubbles config
         * @param {IAmMapsUtilsInsertConfig|IAmMapsUtilsGetBubblesConfig} config
         * @param {string} errMsg
         * @param {boolean} mapIsOptional
         * @private
         */
        _validateInsertConfig (config:IAmMapsUtilsGetBubblesConfig, errMsg:string, mapIsOptional:boolean):void
        _validateInsertConfig (config:IAmMapsUtilsGetLinesConfig, errMsg:string, mapIsOptional:boolean):void
        _validateInsertConfig (config:IAmMapsUtilsInsertConfig, errMsg:string, mapIsOptional:boolean):void {
            // validate config
            this.assert.isObject(config, 'config', errMsg);
            // validate map
            this.assert.isObject(config.map, 'map', errMsg, mapIsOptional);
            if (config.map) {
                this.assert(config.map.dataProvider, errMsg + 'config.map object is not a valid map object.');
            }
            // validate minBubbleSize
            if (!_.isUndefined(config.minBubbleSize)) {
                this.assert(_.isNumber(config.minBubbleSize), errMsg + `config.minBubbleSize must be a number.`,
                    TypeError);
                this.assert(config.minBubbleSize > 0, errMsg + `config.minBubbleSize must be greater than zero.`,
                    TypeError);
            }
            // validate maxBubbleSize
            if (!_.isUndefined(config.maxBubbleSize)) {
                this.assert(_.isNumber(config.maxBubbleSize), errMsg + `config.maxBubbleSize must be a number.`,
                    TypeError);
                this.assert(config.maxBubbleSize > 0, errMsg + `config.maxBubbleSize must be greater than zero.`,
                    TypeError);
            }

            // validate maxBubbleSize relation to minBubbleSize
            if (!_.isUndefined(config.minBubbleSize) && !_.isUndefined(config.maxBubbleSize)) {
                this.assert(config.maxBubbleSize >= 0,
                    config.minBubbleSize + `config.maxBubbleSize must be greater or equal to config.minBubbleSize.`,
                    TypeError);
            }

            // validate maxBubbleSize when no minBubbleSize
            if (_.isUndefined(config.minBubbleSize) && !_.isUndefined(config.maxBubbleSize)) {
                this.assert(config.maxBubbleSize >= amMapsUtilsInsertConfigDefault.minBubbleSize, errMsg +
                    `config.maxBubbleSize must be greater or equal to config.minBubbleSize default which is ` +
                    amMapsUtilsInsertConfigDefault.minBubbleSize, RangeError);
            }

            // validate minBubbleSize when no maxBubbleSize
            if (!_.isUndefined(config.minBubbleSize) && _.isUndefined(config.maxBubbleSize)) {
                this.assert(config.minBubbleSize <= amMapsUtilsInsertConfigDefault.maxBubbleSize, errMsg +
                    `config.maxBubbleSize must be greater or equal to config.minBubbleSize default which is ` +
                    amMapsUtilsInsertConfigDefault.minBubbleSize, RangeError);
            }

        }

        /**
         * Returns a merged new object with the defaults and user config
         *
         * @param {IAmMapsUtilsInsertConfig} config
         * @returns {IAmMapsUtilsInsertConfig}
         * @private
         */
        _getInsertConfig (config:IAmMapsUtilsInsertConfig):IAmMapsUtilsInsertConfig {
            return _.merge<IAmMapsUtilsInsertConfig>({}, amMapsUtilsInsertConfigDefault, config);
        }

        getBubbleDef (country:IAmMapsUtilsInsertCountriesConfig, maxSquare:number, minSquare:number, maxValue:number,
            minValue:number):IAmMapsUtilsBubbleConfig {

            var value = country.value;
            // calculate size of a bubble
            var square = ((value - minValue) || 0.001) / ((maxValue - minValue) || 0.001) * (maxSquare - minSquare) +
                minSquare;
            if (square < minSquare) {
                square = minSquare;
            }
            var size = Math.sqrt(square / (Math.PI * 2));

            let def:any = {
                title: country.name,
                value: country.value,
                color: country.color || '#FF0000',
                longitude: this.amMapsCountryLatLong[country.alpha2].longitude,
                latitude: this.amMapsCountryLatLong[country.alpha2].latitude,
            };

            if (country.anomaly) {
                def.imageURL = 'assets/images/location_anomaly.png';
                def.width = 24;
                def.height = 24;
            } else {
                def.type = 'circle';
                def.width = size;
                def.height = size;
            }

            return def;

        }

        _getBubblesDef (config:IAmMapsUtilsGetBubblesConfig):IAmMapsUtilsBubbleConfig[]
        _getBubblesDef (config:IAmMapsUtilsInsertConfig):IAmMapsUtilsBubbleConfig[] {

            if (config.countries.length === 0) {
                return [];
            }

            // get config options
            config = this._getInsertConfig(config);

            let images = (config.map && config.map.dataProvider.images) || [];

            // it's better to use circle square to show difference between values, not a radius
            var maxSquare = config.maxBubbleSize * config.maxBubbleSize * 2 * Math.PI;
            var minSquare = config.minBubbleSize * config.minBubbleSize * 2 * Math.PI;
            var maxValue = _.maxBy(config.countries, 'value').value;
            var minValue = _.minBy(config.countries, 'value').value;

            _.each(config.countries, country => {
                images.push(this.getBubbleDef(country, maxSquare, minSquare, maxValue, minValue));
            });

            return images;
        }


        /**
         * Returns a LatLong object for a specific alpha2 country code. Returns null if no country code is found
         *
         * @param {string} Alpha2
         * @returns {{latitude: number, longitude: number}|null}
         */
        getLatLongByAlpha2Code (Alpha2:string):ILatLong {
            return this.amMapsCountryLatLong[Alpha2] || null;
        }


        getBubbles (config:IAmMapsUtilsGetBubblesConfig):IAmMapsUtilsBubbleConfig[] {
            // validate config
            let errMsg = this._ERR_MSG + 'getBubbles: ';
            this._validateInsertConfig(config, errMsg, true);
            this._validateCountries(config.countries, errMsg + 'config: ');

            return this._getBubblesDef(config);
        }

        /**
         * Inserts bubbles into a map.
         *
         * @param {IAmMapsUtilsInsertConfig} config
         */
        insertBubbles (config:IAmMapsUtilsInsertConfig):void {
            // validate config
            let errMsg = this._ERR_MSG + 'insertBubbles: ';
            this._validateInsertConfig(config, errMsg, false);
            this._validateCountries(config.countries, errMsg + 'config: ');

            config.map.dataProvider.images = this._getBubblesDef(config);
        }

        _getCountriesLatLong (config:IAmMapsUtilsGetLinesConfig):{longitude:number, latitude:number}[] {
            return _.map(
                // filter countries to only include countries that exist in amMapsCountryLatLong
                _.filter(config.countries, country => this.amMapsCountryLatLong[country.alpha2]),
                // return
                country => {
                    return {
                        longitude: this.amMapsCountryLatLong[country.alpha2].longitude,
                        latitude: this.amMapsCountryLatLong[country.alpha2].latitude
                    }
                });
        }

        getLines (config:IAmMapsUtilsGetLinesConfig):IAmMapsUtilsLinesConfig[] {
            // validate config
            let errMsg = this._ERR_MSG + 'getBubbles: ';
            this._validateInsertConfig(config, errMsg, true);
            let lines:{longitude:number, latitude:number}[] = this._getCountriesLatLong(config);
            this._getCountriesLatLong(config);
            return [
                {
                    id: "plane-line",
                    arc: -0.85,
                    alpha: 0.3,
                    latitudes: _.map<{longitude:number, latitude:number}, number>(lines, 'latitude'),
                    longitudes: _.map<{longitude:number, latitude:number}, number>(lines, 'longitude')
                },
                {
                    id: "plane-shadow",
                    arc: 0,
                    alpha: 0,
                    latitudes: _.map<{longitude:number, latitude:number}, number>(lines, 'latitude'),
                    longitudes: _.map<{longitude:number, latitude:number}, number>(lines, 'longitude')
                }
            ];
        }

        getAlertImage () {
            return {
                svgPath: "m2,106h28l24,30h72l-44,-133h35l80,132h98c21,0 21,34 0,34l-98,0 -80,134h-35l43,-133h-71l-24,30h-28l15,-47",
                color: "#AA0000",
                scale: 0.03,
                positionScale: 1.8
            };
        }

        getPlaneImage ():IAmMapsUtilsPlanesConfig[] {
            return [
                {
                    svgPath: "m2,106h28l24,30h72l-44,-133h35l80,132h98c21,0 21,34 0,34l-98,0 -80,134h-35l43,-133h-71l-24,30h-28l15,-47",
                    positionOnLine: 0,
                    color: "#AA0000",
                    animateAlongLine: true,
                    lineId: "plane-line",
                    flipDirection: true,
                    loop: true,
                    scale: 0.03,
                    positionScale: 1.8
                },
                {
                    svgPath: "m2,106h28l24,30h72l-44,-133h35l80,132h98c21,0 21,34 0,34l-98,0 -80,134h-35l43,-133h-71l-24,30h-28l15,-47",
                    positionOnLine: 0,
                    color: "#000000",
                    alpha: 0.1,
                    animateAlongLine: true,
                    lineId: "plane-shadow",
                    flipDirection: true,
                    loop: true,
                    scale: 0.03,
                    positionScale: 1.3
                }
            ]
        }

        static $inject = ['assert', 'amMapsCountryLatLong'];

        constructor (public assert:any, public amMapsCountryLatLong:IAmMapsCountryLatLong) {
        }
    }


    angular.module('Fortscale.shared.services')
        .service('amMapsUtils', AmMapsUtils)
}
