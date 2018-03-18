/**
 * This service is a util service for high risk users asset
 */
module Fortscale.layouts.users {

    import IConvertUsersStateUtils = Fortscale.layouts.users.IConvertUsersStateUtils;
    export interface User {
        username: string;
        score: string;
        id: string;
    }


    export interface FavoriteUserFilter {
        id: string,
        filterName: string,
        filter: UserFilter
    }

    export class UsersPage {
        data: User[];
        total: number;
        allWatchedCount: number;


        constructor(data: User[], total: number) {
            this.data = data;
            this.total = total;
        }

    }

    export interface SeverityCounts {
        administrator:number;
        watch:number;
        userCount:number;

    }

    export interface SeveritiesCounts {
        Critical:SeverityCounts;
        High:SeverityCounts;
        Medium: SeverityCounts;
        Low: SeverityCounts;
        total: number;
    }

    export interface IUsersUtils {
        getUsers (userFilter:UserFilter, askForAllWatchedCount:boolean):ng.IPromise<UsersPage>;
        countUsers (isWatched:boolean,userTags:boolean, minScore:number):ng.IPromise<number>;
        countUsersByFilter (userFilter:UserFilter):ng.IPromise<number>
        getUsersFilters (listOfAllOptionsListPromisses:ng.IPromise<{attributeName:string, listOfOptions:{id:any}[]}>[]):ng.IPromise<FavoriteUserFilter[]>;
        saveUsersFilter (userFilter:UserFilter, filterName:string):ng.IHttpPromise<any>;
        deleteUsersFilter (filterId:string):ng.IHttpPromise<any>;
        getUsersExportUrl (userFilter:UserFilter, topResultsLimit:number):string;
        getUsersSeveritiesCounts (userFilter:UserFilter):ng.IPromise<SeveritiesCounts>;
        watchUsers (watch:boolean, userFilter:UserFilter): ng.IHttpPromise<any>;
        addTags(userFilter:UserFilter,tagIds:string[]):ng.IHttpPromise<any>;
        removeTags(userFilter:UserFilter,tagIds:string[]):ng.IHttpPromise<any>;
        freeTextSearch(userFilter:UserFilter,searchTaxt):ng.IHttpPromise<any>;
    }

    class UsersUtils implements IUsersUtils {
        /**
         * Gets the top scored users.
         *
         * @returns {IPromise<TResult>}
         */

        convertUsersStateUtils:IConvertUsersStateUtils;

        /**
         *
         * @param userFilter - the filter
         * @param askForAllWatchedCount - flag which indicate if the server should count how many watched user
         *                                  relevant to filter and return it on the info.
         * @returns {IPromise<TResult>}
         */
        getUsers (userFilter:UserFilter, askForAllWatchedCount:boolean):ng.IPromise<UsersPage>{
            let extraData:any = {
                addAllWatched : askForAllWatchedCount
            };
            return this.$http.get(`${this.BASE_URL}/user`, {
                params: this._buildUserFilterParams(userFilter,true,false,extraData)
            })
                .then((res:any) => {
                    // Validate data
                    if (!res.data.data) {
                        throw new ReferenceError(`getUsers: The was no "data" property in the response body.`);
                    }

                    let page:UsersPage = new UsersPage(res.data.data, res.data.total);
                    page.allWatchedCount = askForAllWatchedCount && res.data.info? res.data.info.allWatched : null;
                    return page;
                })
                .catch(err => {
                    this.$log.error(`getUsers: Couldn't get users data`, err);
                    return null;
                })
        }

        getUsersExportUrl (userFilter:UserFilter, topResultsLimit:number):string {
            let filterClone:UserFilter = _.cloneDeep(userFilter);

            //Set page and size only for the clone so it will not affect the original objecr
            filterClone.page = 1;
            filterClone.pageSize= topResultsLimit || 999999;

            let url:string = this.BASE_URL + '/user/export?' +
                this.$httpParamSerializerJQLike(this._buildUserFilterParams(filterClone, true, false));
            return url;
        }
        /**
         * Gets the top scored users.
         *
         * @returns {IPromise<TResult>}
         */
        countUsers (isWatched:boolean,userTags:boolean, minScore:number):ng.IPromise<number>{
            return this.$http.get(`${this.BASE_URL}/user/count`, {
                params: {
                    user_tags: userTags ? "admin":"",
                    is_watched: isWatched ? isWatched:"",
                    min_score: _.isNil(minScore) ? "" : minScore
                }
            })
                .then((res:any) => {
                    // Validate data
                    if (typeof res.data.dataBean === "undefined" || typeof res.data.dataBean.data === "undefined") {
                        throw new ReferenceError(`getUsers: The was no "data" property in the response body.`);
                    }

                    let count:number = res.data.dataBean.data;
                    return count;
                })
                .catch(err => {
                    this.$log.error(`getUsers: Couldn't get users data`, err);
                    return 0;
                })
        }

         /**
         * Gets the users count according to the filter received.
         *
         * @returns {IPromise<TResult>}
         */
        countUsersByFilter (userFilter:UserFilter):ng.IPromise<number>{
            return this.$http.get(`${this.BASE_URL}/user/count`, {
              params: this._buildUserFilterParams(userFilter,false, false)
            })
                .then((res:any) => {
                    // Validate data
                    if (typeof res.data.dataBean === "undefined" || typeof res.data.dataBean.data === "undefined") {
                        throw new ReferenceError(`getUsers: The was no "data" property in the response body.`);
                    }

                    let count:number = res.data.dataBean.data;
                    return count;
                })
                .catch(err => {
                    this.$log.error(`getUsers: Couldn't get users data`, err);
                    return 0;
                })
        }

        /**
         * This method return the status code (success / error).
         * 400 - The user name already exists
         * 500 - Server side error
         * 200 - Sucess
         *
         * @param userFilter
         * @param filterName
         * @returns {IPromise<TResult>}
         */
        saveUsersFilter (userFilter:UserFilter, filterName:string):ng.IHttpPromise<any>{

            let clonedFilter = _.clone(userFilter);
            delete clonedFilter.searchValue;
            return this.$http.post(`${this.BASE_URL}/user/${filterName}/favoriteFilter`, this._buildUserFilterParams(clonedFilter,true,true));
        }

        /**
         * getUsersFilters the all the possible filters
         * listOfAllOptionsListPromisses is array of promisses.
         * Is promise, after it resolved, should return object {attributeName, listOfOtpions}
         * while list of options is array of object, each of the object should contain member named id.
         * In other words, each promise should return: {attributeName:string, listOfOptions:{id:any}[]}
         *
         * @returns {IPromise<TResult>}
         */
        getUsersFilters (listOfAllOptionsListPromisses:ng.IPromise<{attributeName:string, listOfOptions:{id:any}[]}>[]):ng.IPromise<FavoriteUserFilter[]>{

            //listOfAllOptionsListPromisses:ng.IPromise<{attributeName:string, listOfOptions:{id:any}[]}>[]

            //Set the get favoriteFilter promisess firts, and add any addition promisses from listOfAllOptionsListPromisses
            let promisses:ng.IPromise<any>[] = [this.$http.get(`${this.BASE_URL}/user/favoriteFilter`)];
            _.each(listOfAllOptionsListPromisses,(promiss:ng.IPromise<{attributeName:string, listOfOptions:{id:any}[]}>)=>{
               promisses.push(promiss);
            });

            return this.$q.all(promisses)
                .then((res:any[]) => {
                    // Validate data

                    let favoriteUserFilters:FavoriteUserFilter[] = res[0].data.dataBean.data;
                    if (!favoriteUserFilters) {
                        throw new ReferenceError(`getUsersFilters: The was no "data" property in the response body.`);
                    }

                    let allOptionsList:{[key:string]:{id:any,value:any}[]} ={};
                    for (let i=1; i<res.length;i++){//Iterate all the results except the first one
                        allOptionsList[res[i].attributeName] = res[i].listOfOptions;
                    }

                    this._usersFiltersAdaptorFromRest(favoriteUserFilters, allOptionsList);


                    return favoriteUserFilters;
                })
                .catch(err => {
                    this.$log.error(`getUsersFilters: Couldn't get users data`, err);
                    return null;
                })
        }

        getUsersSeveritiesCounts (userFilter:UserFilter):ng.IPromise<SeveritiesCounts>{
            return this.$http.get(`${this.BASE_URL}/user/severityBar`, {
                params: this._buildUserFilterParams(userFilter, false, false, {severity: null})
                })
                .then((res:any) => {
                    // Validate data
                    let data:any= res.data.data;


                    let severities:SeveritiesCounts = data ? data:{};
                    severities.total = data ? res.data.total : 0;

                    return severities;
                })
                .catch(err => {
                    this.$log.error(`getUsersSeveritiesCounts: Couldn't get severities data`, err);
                    return null;
                })
        }


        /**
         * Delete the favorite filter by id from server
         * @param filterId
         * @returns {IHttpPromise<T>}
         */
        deleteUsersFilter (filterId:string):ng.IHttpPromise<any>{
            return this.$http.delete(`${this.BASE_URL}/user/favoriteFilter/${filterId}`);
        }

        watchUsers (watch:boolean, userFilter:UserFilter): ng.IHttpPromise<any>{
            return this.$http.post(`${this.BASE_URL}/user/${watch}/followUsers`,  this._buildUserFilterParams(userFilter, false, true)  );

        }

        addTags(userFilter:UserFilter,tagIds:string[]):ng.IHttpPromise<any>{
            return this.$http.post(`${this.BASE_URL}/user/true/${tagIds.join(",")}/tagUsers`,  this._buildUserFilterParams(userFilter, false, true)  );
        }
        removeTags(userFilter:UserFilter,tagIds:string[]):ng.IHttpPromise<any>{
            return this.$http.post(`${this.BASE_URL}/user/false/${tagIds.join(",")}/tagUsers`,  this._buildUserFilterParams(userFilter, false, true)  );
        }
        ///////////////////////////////////////////////**********************************//////////////////////////////
        //
        //                Internal helper methods
        //
        ///////////////////////////////////////////////**********************************//////////////////////////////

        /**
         *
         * @param userFilter - current user\ filter
         * @param includePageDetails - indicator if we need to include the paging details (page and page size)
         * @param isPostPut - if true, all the fields that should be arrays converted from seperated string to array,
         *                  if false, the fields are not converted into array
         * @param additionalData - if we need to merge another data on the object other then the filter
         * @returns {any} OBJECT OF {PARAMS:...}
         * @private
         */
        _buildUserFilterParams(userFilter:UserFilter, includePageDetails:boolean, isPostPut:boolean, additionalData?:any){


            //Convert filter attributes to param object for get or post response
            let params:any = {};
            _.each(PARAMS_REQUEST_TO_FILTER_ATTRIBUTE_NAME, (attribute:string, paramName:string) =>{
               params[paramName] = this.convertUsersStateUtils.buildParam(userFilter,attribute, isPostPut );
            });


            //Page number and page size - optional
            if (includePageDetails){
                _.merge(params,{
                    size: userFilter.pageSize,
                    fromPage: userFilter.page,
                });

            }

            //Add additional data if needed
            _.merge(params,additionalData);

            return params;
        }

        freeTextSearch(userFilter:UserFilter,searchTaxt):ng.IHttpPromise<any>{
            let extraData:any = {
                searchValue : searchTaxt
            };
            userFilter.sortByField="name";
            userFilter.page=1;
            userFilter.pageSize=6;

            return this.$http.get(`${this.BASE_URL}/user/extendedSearch`, {
                params: this._buildUserFilterParams(userFilter,true,false,extraData)
            });

        }

        /**
         * This method iterate all the filters responsed from server and convert the values as arrive in rest
         * to the values that the state familiar with.
         * @param favoriteUserFilters
         * @param allOptionsList - a dictionary from attribute name (I.E. alertTypes) to list of options of the attribute.
         *                          each option is an object that must have id member.
         * @private
         */
        _usersFiltersAdaptorFromRest(favoriteUserFilters:FavoriteUserFilter[],allOptionsList:{[key:string]:{id:any,value:any}[]}){
            //For each favorite filter
            _.each(favoriteUserFilters, (favoriteUserFilter:FavoriteUserFilter)=>{
                //For each attribute in the filter
                _.each(favoriteUserFilter.filter,(value:any,key:any)=>{
                    //Convert and replace the new value
                    let convertedValue = this.convertUsersStateUtils.getAsStateSeperatedValues(key, value,allOptionsList);
                    if (typeof convertedValue !== "undefined") {
                        favoriteUserFilter.filter[key] = convertedValue;
                    }
                });
            });
        }



        static $inject = ['BASE_URL', '$http', '$log','$httpParamSerializerJQLike','convertUsersStateUtilsFactory','$q'];

        constructor (public BASE_URL:string, public $http:ng.IHttpService, public $log:ng.ILogService,
                     public $httpParamSerializerJQLike:any, convertUsersStateUtilsFactory:IConvertUsersStateUtilsFactory,
                     public $q:ng.IQService) {

            this.convertUsersStateUtils = convertUsersStateUtilsFactory.getConvertUsersStateUtil(TYPE_OF_KEY);
        }
    }


    /*
     This definition of UserFilter must be sync with TYPE_OF_KEY & PARAMS_REQUEST_TO_FILTER_ATTRIBUTE_NAME
     If new attribute added it must be added to TYPE_OF_KEY and to PARAMS_REQUEST_TO_FILTER_ATTRIBUTE_NAME
     */
    export interface UserFilter {
        page?: number;
        pageSize?: number;
        sortByField?: string;
        sortDirection?: string;
        addAlertsAndDevices?:boolean;
        isWatched?:boolean;
        userTags?:string;
        minScore?:number;
        alertTypes?:string;
        indicatorTypes?:string;
        locations?:string;
        severity?:string;
        searchValue?:string;
        positions?:string;
        departments?:string;

    }

    const TYPE_OF_KEY:{[key:string]:string} = {
        page:"NumberState",
        pageSize: "NumberState",
        sortByField: "StringState",
        sortDirection: "StringState",
        addAlertsAndDevices:"BooleanState",
        isWatched:"BooleanState",
        userTags:"StringsArrayState",
        minScore:"NumberState",
        alertTypes:"AlertTypesState",
        indicatorTypes:"StringsArrayState",
        locations:"StringsArrayState",
        severity:"StringState",
        searchValue: "StringState",
        positions:"StringsArrayState",
        departments:"StringsArrayState"

    };

    const PARAMS_REQUEST_TO_FILTER_ATTRIBUTE_NAME:{[paramName:string]:string}= {
        sortField: "sortByField",
        sortDirection: "sortDirection",
        addAlertsAndDevices: "addAlertsAndDevices",
        userTags: "userTags",
        isWatched: "isWatched",
        minScore: "minScore",
        alertTypes: "alertTypes",
        indicatorTypes: "indicatorTypes",
        locations: "locations",
        severity: "severity",
        searchValue: "searchValue",
        positions: "positions",
        departments: "departments"

    };

    //End of UserFilter, TYPE_OF_KEY, PARAMS_REQUEST_TO_FILTER_ATTRIBUTE_NAME

    angular.module('Fortscale.layouts.users')
        .service('usersUtils', UsersUtils);
}
