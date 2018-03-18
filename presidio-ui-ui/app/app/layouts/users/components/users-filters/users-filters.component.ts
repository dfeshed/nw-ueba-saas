module Fortscale.layouts.users {

    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;
    import IUsersUtils = Fortscale.layouts.users.IUsersUtils;
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    import FavoriteUserFilter = Fortscale.layouts.users.FavoriteUserFilter;
    import UserFilter = Fortscale.layouts.users.UserFilter;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;

    class UsersFiltersComponentController {

        saveFavoritesDialogOpened: boolean = false;
        riskyUsersCount: number;
        watchedUsersCount: number;
        taggedUsersCount: number;
        activeFilterId:string;

        tableSettings : any;
        stateId:string;
        clearStateDelegate: (state:UserFilter) => void;
        activeFilterUpdateDelegate: (filterId:string) => void;
        favoriteFilters: FavoriteUserFilter[] = [];

        //Arrays for filters input.
        //Should contain id & value (both can be number or string)
        //count is optional field
        alertTypes : {id:any,value:any, count?:number}[]=[];
        indicatorsTypes : {id:any,value:any, count?:number}[];
        locations : {id:any,value:any, count?:number}[]=[];
        departments: {id:any,value:any, count?:number}[]=[];
        positions: {id:any,value:any, count?:number}[]=[];

        tags : ITagDefinition[]; // Original tag from binding. Don't change it.
        _tags : {id:any,value:any, count?:number}[]=[];// Copy of tags with additions.


        //Promisess that trigger when alertTypes updated and when indicatorType updated
        alertTypesPromise: ng.IPromise<{attributeName:string, listOfOptions:{id:any}[]}>;
        indicatorTypesPromise: ng.IPromise<{attributeName:string, listOfOptions:{id:any}[]}>;


        getStateId():string{
            return this.stateId;
        }



        _initPresetFilters(){
            // this._initDepartmentsList();
            // this._initRolesList();
            // this._initCountriesList();
            this._initAlertTypesList();

        }

        /**
         * This method fetch data from url, and build array of "{id:string, value:string}, save it to this.arrayName
         * @param arrayName - the name of the attribute one "this" which the array will be saved
         * @param url - the url to being the data from
         * @param mappingFunction - function which itterate each of the results of the request from the url,
         *                          and return build object of "{id:string, value:string} for each of the values.
         * @returns {IPromise<TResult>}
         * @private
         */
        _loadFromUrlToArray(arrayName:string, url:string, mappingFunction:(value:any) => {id:string, value:string}){
            let ctrl:any = this;
            return this.$http
                .get(this.BASE_URL + url)
                .then((res:any)=> {
                    ctrl[arrayName] = _.map(res.data.data,  mappingFunction);
                });
        }

        // _initCountriesList(){
        //     let ctrl = this;
        //     this._loadFromUrlToArray('locations','/organization/activity/locations?time_range=999999&limit=999999',
        //         (country:any)=> { return { id: country.country, value: country.country };}).then(()=>{
        //             //Sort the countries by the display name
        //             ctrl.locations = _.sortBy(ctrl.locations,
        //                 (country:{id:any,value:any, count?:number})=>{return country.value});
        //     });
        //
        // }

        // /*
        // Load positions and store it on this.positions
        // Roles = Positions
        //  */
        // _initRolesList(){
        //     this._loadFromUrlToArray("positions",'/user/adInfo.position/distinctValues',
        //         (role:string)=> {  return { id: role,   value: role    };}  );
        // }
        //
        // /**
        //  * Load depratments and store it on this.departments
        //  * @private
        //  */
        // _initDepartmentsList(){
        //     this._loadFromUrlToArray("departments",'/user/adInfo.department/distinctValues',
        //         (role:string)=> {  return { id: role,   value: role    };}  );
        // }



        _initAlertTypesList(){

            let prettyAlertNameFunction:Function = <Function>this.$filter('prettyAlertName');
            this.$http.get(this.BASE_URL+"/user/exist-alert-types?ignore_rejected=true")
                .then((data:any) => {
                    this.alertTypes = [];
                    _.each(data.data.data, (dataType:any)=>{

                        let alertName = prettyAlertNameFunction({name:dataType.alertTypes[0]});
                        this.alertTypes.push({
                            'id' : dataType.alertTypes.join('@@@'),
                            'value': alertName,
                            'count' : dataType.count

                        });
                    });
                });
        }





        //Init favorites can take place only after alertTypes and indicatorTypes initiated,
        //so I have initiated two promiseses which deffered when alertTypes and indicatorTypes deffered

        _initPreconditionsToBeforeGetFavorites(){
            //Initiate the deffer objects
            let alertTypesDeffer:ng.IDeferred<any> = this.$q.defer();
            let indicatorTypesDeffer:ng.IDeferred<any> = this.$q.defer();

            //Save the promises on this
            this.alertTypesPromise = alertTypesDeffer.promise;
            this.indicatorTypesPromise = indicatorTypesDeffer.promise;


            //Init one time watched to resolve the promise
            let unwatchAlertsTypes:Function = this.scope.$watch(this._alertTypesWatchFn.bind(this),
                ()=>{
                    if (this.alertTypes && this.alertTypes.length>0) {
                        alertTypesDeffer.resolve({attributeName:"alertTypes", listOfOptions: this.alertTypes});
                        unwatchAlertsTypes();
                    }
                });

            let unwatchIndicatorsTypes:Function = this.scope.$watch(this._indicatorsTypesWatchFn.bind(this),
                ()=>{
                    if (this.indicatorsTypes && this.indicatorsTypes.length > 0) {
                        indicatorTypesDeffer.resolve({attributeName:"indicatorTypes", listOfOptions: this.indicatorsTypes});
                        unwatchIndicatorsTypes();
                    }
                });




        }

        _watchTagChanges():void{
            let ctrl:any = this;
            let unwatchIndicatorsTypes:Function = this.scope.$watch(()=>{return ctrl.tags},
                ()=>{
                    if (ctrl.tags && ctrl.tags.length>0) {
                        let assinableTags : ITagDefinition[]=<ITagDefinition[]>(_.filter(ctrl.tags,{ isAssignable: true }));
                        ctrl._tags = _.map(assinableTags, function (tag:ITagDefinition) {
                            return {
                                id: tag.name,
                                value: tag.displayName
                            };
                        });
                        //this._tags=_.clone(this.tags);
                        this._tags.splice(0, 0, {
                            id: "any",
                            value: "Tagged Users"
                        });
                        this._tags.splice(0, 0, {
                            id: "none",
                            value: "No Tags"
                        });
                    }
                });
        }

        $onInit () {


            this._initPreconditionsToBeforeGetFavorites();
            this._initPresetFilters();
            this._initFavorites();
            this._watchTagChanges();

        }

        _indicatorsTypesWatchFn():any{
            return this.indicatorsTypes;
        }

        _alertTypesWatchFn():any{
            return this.alertTypes;
        }

        /**
         * Send favorite to server. Present error if status is not 200.
         * @param filterName
         * @returns {IPromise<Error>} - so if some other method need to do something after save finished
         * it could be do so
         *
         */
        saveFavorite(filterName:string):ng.IPromise<void>{

            let state:any = this.stateManagementService.readCurrentState(this.stateId);
            return this.usersUtils.saveUsersFilter(state,filterName).then((res:any)=>{

                    this._initFavorites();
                    this.saveFavoritesDialogOpened=false;


                return res;
            }).catch(res => {
                //Display error res.statusText
                if (res.status===409){
                    //Duplicate names, do nothing, just return the response to the promise
                } else {
                    //Other error
                    this.toastrService.error(`Failed to save new filter. Please try again later.`);
                }

                return res;
            });


        }

        /**
         * Delete filter by id and show error if such exists
         * @param filterId
         */
        deleteFilter(filter:FavoriteUserFilter):any {
            if (this.activeFilterId === filter.id){
                this.activeFilterUpdateDelegate(null);
            }
            this.usersUtils.deleteUsersFilter(filter.id).then((res:any)=>{
                   //Success
                   this._initFavorites();
                   this.toastrService.success("Removed "+filter.filterName+" from Favorites.");

            }).catch(res => {
                    this.toastrService.error(`Failed to remove filter from Favorites. Please try again later.`);
                return res;
            });
        }

        //Hide the favorite popup
        cancelFavoriteSaving():void{
            this.saveFavoritesDialogOpened=false;
        }

        //Set the new favorite filter and activate the filter id
        applyFilter(filter:FavoriteUserFilter){
            this.clearStateDelegate(filter.filter);
            this.activeFilterUpdateDelegate(filter.id);
        }

        /**
         * Load favotires list and display them
         * @private
         */
        _initFavorites(){
            this.usersUtils.getUsersFilters([this.alertTypesPromise, this.indicatorTypesPromise]).then((res)=>{
               this.favoriteFilters = res;
            });
        }


        static $inject = ['$scope','stateManagementService','usersUtils','$http','BASE_URL','$filter','toastrService','$q','TAGS_FEATURE_ENABLED'];
        constructor (public scope:ng.IScope,
                     public stateManagementService:IStateManagementService,
                     public usersUtils:IUsersUtils,
                     public $http:ng.IHttpService,
                     public BASE_URL:string,
                     public $filter:ng.IFilterService,
                     public toastrService:IToastrService,
                     public $q:ng.IQService,
                     public TAGS_FEATURE_ENABLED:string
                     ) {


        }
    }



    let UsersFilterComponent:ng.IComponentOptions = {
        controller: UsersFiltersComponentController,
        templateUrl: 'app/layouts/users/components/users-filters/users-filters.component.html',
        bindings: {
            stateId: '<', // The ID of the state. Used for when using the name of the component from several places.
                         //Optional. If empty use STATE_ID constant
            indicatorsTypes: '=',
            activeFilterUpdateDelegate: '=',
            fetchStateDelegate: '=',
            updateStateDelegate: '=',
            clearStateDelegate: "=",
            activeFilterId: '=',
            riskyUsersCount: '<',
            watchedUsersCount: '<',
            taggedUsersCount: '<',
            tags: '<'


        }
    };
    angular.module('Fortscale.layouts.users')
        .component('usersFilters', UsersFilterComponent);
}
