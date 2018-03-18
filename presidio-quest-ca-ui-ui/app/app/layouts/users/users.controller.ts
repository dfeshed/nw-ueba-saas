/**
 * Created by shays on 01/08/2016.
 * Main controller for users page
 */
module Fortscale.layouts.users {

    const STATE_ID:string = "users-page";
    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;

    class UsersController {

        updateStateChanges: (state: any) => void;
        fetchStateDelegate: (attributeName: string) => any;
        clearStateDelegate: (attributeName: string) => void;
        reloadPredefinedFiltersUsersCount: (riskyUsers: boolean, watchedUsers: boolean, taggedUser: boolean, tagsList: boolean) => void;


        indicatorTypes: {id: any, value: any, count?: number}[] = [];
        tags: ITagDefinition[] = [];

        activeFilterUpdateDelegate: (filterId: string) => void;
        activeFilterId: string;

        riskyUsersCount: number;
        watchedUsersCount: number;
        taggedUsersCount: number;


        /**
         * This state contains all the attributes required to filter and sort the grid
         * @type {{sortByField: string}}
         */
        initialState: any = {
            sortByField: "score",
            sortDirection: "DESC",
            minScore: 0

        };

        sortDefaultDriection: any = {
                score: "DESC",
                name: "ASC",
                alertsCount: "DESC"
            };


        currentState:any = {};

        getStateId():string{
            return STATE_ID;
        }

        /******************************************************************************************************
         *                      Methods which releate to state change / update / clert etc...
         ******************************************************************************************************/

        /**
         * Reset the current state.
         * If filter given - the old state will be replace by the given state.
         * If no filter given - the old state will be replace by the initial state
         * @param filter
         * @private
         */
        _clearState(filter?:UserFilter): void{
            let ctrl:any = this;
            let fieldsToKeep:any = this._getSortingFields();
            if (filter){ //Set specific filter

                let newState = _.clone(filter);
                _.merge(newState,fieldsToKeep);
                this.stateManagementService.updateState(this.getStateId(),newState);

            } else { //Set initial state except sort

                this.stateManagementService.clearState(this.getStateId(),fieldsToKeep);
                this.activeFilterId = "";

            }
            //Apply the new filter
            this.currentState =  this.stateManagementService.readCurrentState(this.getStateId());

        }


        _getSortingFields():any {
            return this._getBackupFiedls(["sortByField","sortDirection"]); //Keep old sorting
        }

        _getBackupFiedls(fieldNames:string[]){
            let objectWithFields:any = {};
            _.each(fieldNames,(fieldName:string)=>{
               objectWithFields[fieldName] = this.currentState[fieldName];
            });
            return objectWithFields;
        }


        /**
         * Get state change param
         * @param change (id - attribute name, value- the new value)
         * @private
         */
        _updateStateChanges(change:{id:string, value:any}):void {

            let attributeName:string = change.id;
            let value:any = change.value;


            if (value === null || typeof value !== "object") { //The property is nubmer, boolean, or String

                let propertyAndValue = {};
                propertyAndValue[attributeName] = value;


                if (attributeName="sortByField") {
                    propertyAndValue["sortDirection"]=this.sortDefaultDriection[value];
                }

                _.merge(this.currentState, propertyAndValue);

            } else {
                throw new Error("UsersGridComponentController.updateStateChange can get only flat values");
            }
            this.stateManagementService.updateState(this.getStateId(), this.currentState);
            this.activeFilterId = "";
        }

        _fetchStateDelegate(attributeName:string):any{
            let state = this.stateManagementService.readCurrentState(this.getStateId());
            return state[attributeName];
        }

        /******************************************************************************************************
         *                      Methods which bind delegators to this
         ******************************************************************************************************/

        /*
         the update state function called from controls, so it must contain the state
         of this component controller.
         That's why updateStateChanges must call _updateStateChanges with the current this.

         */
        _initAndbindUpdateStateFunction(){
            let ctrl:any = this;

            //updateStateChanges get stateChange and call the internal method bound to this
            this.updateStateChanges = function(stateChange){
                ctrl._updateStateChanges(stateChange);
            };
        }

        /*
         the fetch state function called from controls, so it must contain the state
         of this component controller.
         That's why updateStateChanges must call _fetchStateDelegate with the current this.

         */
        _initAndbindFetchStateFunction(){
            let ctrl:any = this;
            //fetchStateDelegate get stateChange and call the internal method bound to this
            this.fetchStateDelegate  =  function(attributeName){
                return ctrl._fetchStateDelegate(attributeName);
            };
        }

        _initAndbindClearStateFunction(){
            let ctrl:any = this;
            //fetchStateDelegate get stateChange and call the internal method bound to this
            this.clearStateDelegate  =  function(filter?:UserFilter){
                return ctrl._clearState(filter);
            };
        }

        _initAndbindActiveFilterUpdateFunction(){
            let ctrl:any = this;
            //fetchStateDelegate get stateChange and call the internal method bound to this
            this.activeFilterUpdateDelegate  =  function(filterId:string){
                this.activeFilterId = filterId;
            };
        }

        _initAndBindReloadPredefinedFiltersUsersCountFunction(){
            let ctrl:any = this;
            this.reloadPredefinedFiltersUsersCount  =  function(riskyUsers:boolean, watchedUsers:boolean, taggedUser:boolean, tagsList:boolean){
                return ctrl._initPredefinedFiltersUsersCount(riskyUsers, watchedUsers, taggedUser,tagsList);
            };
        }
        /******************************************************************************************************
         *                      Methods which loads/init data from the server
         ******************************************************************************************************/

        //Load list of indicators
        _initIndicatorsList(){
            let indicatorsList:any = this.fsIndicatorTypes.getIndicatorsList('/user/exist-anomaly-types')
                .then((data:any) => {
                    this.indicatorTypes = data;

                });
        }


        //Load list of tags
        _initTagsPreset(){
            let ctrl:any = this;
            return this.$http
                .get(this.BASE_URL + '/tags/user_tags')
                .then((res:any) => {
                    ctrl.tags=<ITagDefinition[]>_.filter(res.data.data,(tag:any) => { return tag.deleted !== true; });

                });

        }

        /**
         * Init or reload predefined filters count, and reload filter values for multi select according to configuration
         * @param riskyUsers - if true, reload the count of risky users
         * @param watchedUsers - if true, reload the count of watched users
         * @param taggedUser - if true, reload the count of tagged users
         * @param tagsList - if true, reload the list of tags for the tags multi select
         * @private
         */
        _initPredefinedFiltersUsersCount(riskyUsers:boolean, watchedUsers:boolean, taggedUser:boolean, tagsList:boolean){
            //Init risky users
            if (riskyUsers) {
                this.usersUtils.countUsers(false, false, 0).then((count:number)=> {
                    this.riskyUsersCount = count;
                });
            }

            if (watchedUsers) {
                //Init watched users
                this.usersUtils.countUsers(true, false, null).then((count:number)=> {
                    this.watchedUsersCount = count;
                });
            }
            //Init tagged users
            if (taggedUser) {
                this.usersUtils.countUsers(false, true, null).then((count:number)=> {
                    this.taggedUsersCount = count;
                });
            }

            if (tagsList) {
                this._initTagsPreset();
            }
        }

        _init () {
            this.page.setPageTitle("Users");
            //Init and bind methods
            this._initAndbindUpdateStateFunction();
            this._initAndbindFetchStateFunction();
            this._initAndbindClearStateFunction();
            this._initAndbindActiveFilterUpdateFunction();
            this._initAndBindReloadPredefinedFiltersUsersCountFunction();

            this._initPredefinedFiltersUsersCount(true,true,true,true);
            this._initIndicatorsList();
            this.currentState=this.stateManagementService.initState(this.getStateId(),this.initialState);
            const USERS_CONTROLLERS_CALLER_ID:string = 'users_controller_caller_id';
            this.stateManagementService.registerToStateChanges(this.getStateId(),USERS_CONTROLLERS_CALLER_ID,(newFilter)=>{
                this.currentState = newFilter;
            });


        }


        static $inject = ['stateManagementService','fsIndicatorTypes','usersUtils','$http','BASE_URL','page'];
        constructor (public stateManagementService:IStateManagementService,
                     public fsIndicatorTypes:any,
                     public usersUtils:IUsersUtils,
                     public $http:ng.IHttpService,
                     public BASE_URL:string,
                     public page:any) {
            this._init();

        }

    }

    angular.module('Fortscale.layouts.users')
        .controller('UsersController', UsersController);

}
