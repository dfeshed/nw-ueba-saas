module Fortscale.layouts.users {

    import ITagsUtilsService = Fortscale.shared.services.tagsUtilsService.ITagsUtilsService;
    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;
    import UsersPage = Fortscale.layouts.users.UsersPage;
    import UserFilter = Fortscale.layouts.users.UserFilter;
    import User = Fortscale.layouts.users.User;
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    import IPromise = angular.IPromise;

    const GRID_CALLER_ID:string='grid-caller-id';
    const FS_TABLE_CALLER_ID:string='fs-tabLe-caller-id';

    class UsersGridComponentController {

        /**
         * List of incoming delegates
         */
        updateStateChanges: (state:any) => void;
        fetchStateDelegate: (attributeName:string) => any;
        reloadDelegate: (reloadUsersData:boolean,riskyUsers:boolean, watchedUsers:boolean, taggedUser:boolean, tagsList:boolean) =>void;
        reloadPredefinedFiltersUsersCount: (riskyUsers:boolean, watchedUsers:boolean, taggedUser:boolean, tagList:boolean) =>void;

        tagListAsIdNValueList:{id:any,value:any, isAssignable:boolean}[]=[];
        tags : ITagDefinition[];
        allUsersFollwoed:boolean = false;
        stateId:string;
        usersCount : number = 0;

        //List of sorting by options
        sortByPropertyMap: any = {
            score: "Risk Score",
            name: "Name",
            alertsCount: "Alerts"

        };


        //Prevent send to calls at the same time
        loadingNow:boolean = false;
        currentPage:number=1;
        pageSize: number = 25;
        usersPages : User[][] = [];
        stateDifferentThenOriginal:boolean = false;
        indicatorsTypes : {id:any,value:any, count?:number}[]=[];



        getStateId():string{
            return this.stateId;
        }

        /*****************************************************************************************************
         *      Methods which responsible for translating the filters values, into list of pretty values
         *****************************************************************************************************/

        /**
         * Translate indicators list to pretty display name
         * @returns {any}
         */
        getPrettifyIndicatorTypes():string{
            let state:any =this.stateManagementService.readCurrentState(this.getStateId());

            if ( _.isNil(state.indicatorTypes) ){
                return "";
            } else {

                let values:string[] = [];

                _.each(state.indicatorTypes.split(","),(value)=>{
                    let indicatorType:any = _.find(this.indicatorsTypes,{"id":value});
                    if (!_.isNil(indicatorType)) {
                        values.push(indicatorType.value);
                    }
                });

                return values.join(", ");

            }
        }

        getValueFromState(key:string):string{
            let state:any =this.stateManagementService.readCurrentState(this.getStateId());
            if ( _.isNil(state[key]) ){
                return "";
            } else {

                return state[key].split(",").join(", ");
            }
        }

        /**
         * Translate seperated string of tags list to pretty tags list
         * @returns {any}
         */
        getPrettifyTags():string{
            let state:any =this.stateManagementService.readCurrentState(this.getStateId());

            if ( _.isNil(state.userTags) ){
                return "";
            } else {
                let hideValue :boolean = false;
                let values:string[] = [];
                _.each(state.userTags.split(","),(value)=>{
                    let prettyValue:string;
                    if (value === "none"){
                        prettyValue = "No Tags";
                    } else if (value === "any"){
                        hideValue = true;
                    } else {
                        let tagObject:ITagDefinition = _.find(this.tags, { 'name': value });
                        if (tagObject) {
                            prettyValue = tagObject.displayName;
                        }
                    }

                    if (_.isNil(prettyValue)){
                        prettyValue = value;
                    }
                    values.push(prettyValue);
                });

                if (hideValue){
                    return "Tagged Users";
                } else {
                    let valuesStr =  values.join(", ");
                    valuesStr = _.startCase(_.toLower(valuesStr));
                    return valuesStr;
                }

            }
        }

        _setTagListAsIdValueList():void{
            this.tagListAsIdNValueList = _ .map(this.tags, function (tag:ITagDefinition) {
                return {
                    id: tag.name,
                    value: tag.displayName,
                    isAssignable: tag.isAssignable
                };
            });
            //return tagsList;
        }

        getPrettifySeverity():string{
            let state:any =this.stateManagementService.readCurrentState(this.getStateId())
            return state.severity;
        }

        /**
         * Translate seperated string of alert types list to pretty alert list
         * @returns {string}
         */
        getPrettifyAlertTypes():string{
            let state:any =this.stateManagementService.readCurrentState(this.getStateId());

            if ( _.isNil(state.alertTypes) ){
                return "";
            } else {

                let values:string[] = [];
                let prettyAlertNameFunction:Function = <Function>this.$filter('prettyAlertName');
                _.each(state.alertTypes.split(","),(value)=>{
                    values.push(prettyAlertNameFunction({name:value.split("@@@")[0]}));
                });

                return values.join(", ");


            }
        }


        /**
         * Load next page. Load only if not other loading happening
         */
        _loadMore():void{
                if (this.usersPages.length * this.pageSize < this.usersCount) {
                    this.readDataDelegate(this.currentPage+1, this.pageSize, false,false);
                }
        }

        /**
         * Register to state trigger
         * @private
         */
        _initStateChangeWatch(): void{
            this.stateManagementService.registerToStateChanges(this.stateId,GRID_CALLER_ID,this._reload.bind(this));
            //Check if state already different from original when component loads
            this.stateDifferentThenOriginal = this.stateManagementService.isStateChanged(this.stateId);


        }


        /**
         * Clear the page and users data and loading the whole grid again
         * @private
         */
        _reload():void {
            this.usersPages=[];
            this.readDataDelegate(1, this.pageSize,true,true);
            //Also refresh reset filters

            this.stateDifferentThenOriginal = this.stateManagementService.isStateChanged(this.stateId);

        }

        /**
         * reloadDelegate called from outside, and render the grid + trigger the delegate method
         * which reload the count of predefined filters (one or more)
         *
         * reloadUsersData:boolean - reload the grid
         * riskyUsersCount - reload count for riskyUsers predefined filter
         * watchedUsersCount - reload count for watchedUsers predefined filter
         * taggedUserCount - reload count for riskyUsers taggedUser filter
         * tagsList - reload the list of tags from server
         *
         * @private
         */
        _initAndbindReloadFunction(){
            let ctrl:any = this;
            this.reloadDelegate  = function (reloadUsersData:boolean, riskyUsersCount:boolean, watchedUsersCount:boolean,
                                             taggedUserCount:boolean, tagsList:boolean){
                if (reloadUsersData){
                    ctrl._reload();
                }
                ctrl.reloadPredefinedFiltersUsersCount(riskyUsersCount,watchedUsersCount,taggedUserCount, tagsList);
            };
        }

        /**
         * This method passed as delegate to the grid itself.
         * Each time the grid ask for new page it called this method and pass the page number and size.
         *
         * @param pageNumber
         * @param pageSize
         * @param forceReload - false, if we want to disable loading when loadingNow = true,
         *                      ture if we want to wait and trigger the loading after loading now will finish
         * @returns {ng.IPromise<Fortscale.layouts.users.UsersPage>}
         */
        readDataDelegate (pageNumber: number, pageSize:number, forceReload:boolean, askForAllWatchedCount:boolean):ng.IPromise<UsersPage> {
            //Waiting call - if we are doing paging, and there is page in loading, we ignore any new paging
            //request. If there is not paging, but change in filter, already loadingNow = true (some other request running)
            //we store the new request in the waitingCall and trigger it after the old request finish.
            //If we already have waiting call, we override it, so only the last waiting will actual trigger
            let waitingCall:{page:number, pageSize:number} = null;
            if (!this.loadingNow) {
                this.loadingNow = true;

                let filter:UserFilter=this._prepareFilterForGetUser(pageNumber,pageSize);
                return this.usersUtils.getUsers(filter,askForAllWatchedCount)
                    .then((page:UsersPage) => {
                        this._updatePageDataAfterDataRefreshed(page,pageNumber, askForAllWatchedCount);
                        this._triggerPendingCallOrFinishLoading(waitingCall);
                        return page;

                    });
            } else if (forceReload){ //If loadingNow = true & forceReload = true - store new waitingCall
                waitingCall = {
                  page: pageNumber,
                  pageSize: pageSize
                };
            }
        }

        /**
         * Merge state and additional data into filter request for the server
         * @param pageNumber
         * @param pageSize
         * @returns {UserFilter}
         * @private
         */
        _prepareFilterForGetUser(pageNumber:number, pageSize:number):UserFilter{
            let ctrl:any = this;
            let state:UserFilter = this.stateManagementService.readCurrentState(ctrl.getStateId());

            //Init the filter
            let filter:UserFilter = _.clone(state);
            filter.page = pageNumber;
            filter.pageSize = pageSize;
            filter.addAlertsAndDevices = true;
            return filter;
        }

        /**
         * Update users to display, counts, pageNumber, and so own.
         * @param page
         * @param pageNumber
         * @param askForAllWatchedCount
         * @private
         */
        _updatePageDataAfterDataRefreshed(page:UsersPage, pageNumber:number, askForAllWatchedCount:boolean ):void{
            let ctrl:any = this;
            if (page !== null) {
                ctrl.usersCount = page.total;
                ctrl.usersPages[pageNumber-1] = page.data;
            } else {
                ctrl.usersCount = 0;
                ctrl.users = [];
            }
            if (askForAllWatchedCount) {
                ctrl.allUsersFollwoed = page.allWatchedCount;
            }

            //After loading success, reset the page number and loadingNow flag
            ctrl.currentPage = pageNumber;
        }

        /**
         *
         * @param waitingCall
         * @private
         */
        _triggerPendingCallOrFinishLoading(waitingCall:{page:number, pageSize:number}):void {
            //If some other call waiting, trigger it
            let ctrl:any = this;
            if (waitingCall){
                ctrl.readDataDelegate(waitingCall.page, waitingCall.pageSize);
                waitingCall = null;
            } else {
            ctrl.loadingNow = false;
            }
         }

        /**
         * This method get user, and validate if the user match to current grid filter.
         * If the user is no longer match to the filter, the method will drop him, and refresh the grid
         * @param user
         */
        postRemoveTagDelegate(user:User){
            let ctrl:any=this;
            this._isUserMatchToFilter(user).then((isUserMatch:boolean)=>{
               if (isUserMatch){
                   //Do nothing, the user should not be removed
               }  else {
                   //User is not longer match to the filter
                   ctrl._deleteUserFromResults(user);
                   //Refresh last page again
                   this.readDataDelegate(this.currentPage, this.pageSize, true,false);//Load again current page
                   this.stateManagementService.triggerDelegatesWithoutUpdating(this.getStateId(), [GRID_CALLER_ID, FS_TABLE_CALLER_ID]);

               }

                let userWithTags:{tags:string[]} = <any>user;
                if (_.isNil(userWithTags.tags) || userWithTags.tags.length === 0 ){
                    //If the user is no longer have new tags
                    ctrl.reloadPredefinedFiltersUsersCount(false,false,true, false);
                }
            });
        }

        /**
         * This method calculate if the user match to exist filter
         * and return promise with the result.
         * Current implementation is synchrnous but it ready to async implementation
         * @returns {IPromise<boolean>}
         * @private
         */
        _isUserMatchToFilter(user:User):IPromise<boolean>{
            let isMatch:boolean;
            let state:UserFilter = this.stateManagementService.readCurrentState(this.getStateId());


            if (_.isNil(state.userTags) || state.userTags.trim() === ""){//No filter on tags
                isMatch = true;
            } else { //There are filter on tags
                let tagFilters:string[] = [];
                // Add all regular tags from the state filter to tagFilters, ignore "all", "none", "any" etc...
                let allTagNamesFromFilter:string[]=state.userTags.split(",");
                _.each(allTagNamesFromFilter,(tagName:string)=>{
                   if (_.find(this.tags,{name:tagName} )!=null){
                       tagFilters.push(tagName)
                   }
                });
                let userWithTags:{tags:string[]} = <any>user;
                if (tagFilters.length===0){ //No regular filter
                    if (allTagNamesFromFilter.length === 1 && allTagNamesFromFilter[0]==='any'){
                        //User need to have at least one tag
                        isMatch = userWithTags.tags && userWithTags.tags.length > 0;
                    }  else if (allTagNamesFromFilter.length === 1 && allTagNamesFromFilter[0]==='none'){
                        //User need to have no tags
                        isMatch = !userWithTags.tags || userWithTags.tags.length === 0;
                    } else {
                        //No tags in filter. Return true;
                        isMatch = true;
                    }
                } else { //Test if the user has at least one of the filtered tags
                    isMatch = false;

                    _.each(userWithTags.tags, (tagName)=>{
                       if (_.includes(tagFilters,tagName)){
                           isMatch=true; //At least one user tag in the filter list
                       }
                    });
                }

            }
            return this.$q.when(isMatch);
        }

        /**
         * This method remove a user locally from the grid (its not affect on the users on the server.
         * The method keep that each page will have the same amount of users, so if user deleted from the middle
         * the missing page will take the first user of the next page. it happen recursively from the first missing page to the last page.
         * @param user
         * @private
         */
        _deleteUserFromResults(user:User){
            let indexOfPageWithCurrentUser:number = this._findPageContainUser(user.id);

            let lastPageIndex:number = this.usersPages.length-1;
            let ctrl:any = this;
            this.$timeout(()=>{
                //Remove the user from the grid. But first hide and fade out for 0.5 second
                (<any>user).hidden = true;
                ctrl.$timeout(()=>{

                    _.remove(ctrl.usersPages[indexOfPageWithCurrentUser],{id:user.id});
                },200);
            });

            //sync the amount of users in each page, keep on the order
            this._waterfallUser(indexOfPageWithCurrentUser, lastPageIndex);


        }

        /**
         * This keep that all pages has the same length of users, by remove the first user in each page and had thim to be the last on previous page
         * @param firstPageIndex
         * @param lastPageIndex
         * @private
         */
        _waterfallUser(firstPageIndex:number, lastPageIndex:number){

            //Take the first user of each page, and move it to be the last user on previous page
            for (var i=firstPageIndex; i<lastPageIndex;i++){
                //Get the last record in page i+1, and save it as the last record in page i;
                //Then remove it from page i+1
                let firstRecordInNextPage:User = this.usersPages[i+1][0];
                let firstPage:User[] = this.usersPages[firstPageIndex];
                firstPage.push(firstRecordInNextPage);
                _.remove(this.usersPages[i+1],{id:firstRecordInNextPage.id});
            }
            //When moving the record cause the last page to be empty, remove the last page
            if (this.usersPages[lastPageIndex].length ==0){
                this.usersPages.pop();//Remove the empty last page.
                this.currentPage--;
            }
        }

        /**
         * Return the index of the page the contain the user
         * @param userId
         * @returns {any}
         * @private
         */
        _findPageContainUser(userId:string):number{
            if (!_.isNil(this.usersPages) && this.usersPages.length > 0){
                for (var i:number=0; i<this.usersPages.length;i++){
                    if (!_.isNil(_.find(this.usersPages[i],{id:userId}))){
                        return i;
                    }
                }
            }
            console.error("User id is not exists in any page");
            return null;

        }

        $onInit () {
            let ctrl:any = this;
            this._initStateChangeWatch();
            this._initAndbindReloadFunction()
            this.readDataDelegate(this.currentPage,this.pageSize,false,true);
            this.$scope.$watch(()=>{return this.tags},this._setTagListAsIdValueList.bind(this));

            //Listen to grid scroller, if arrive to end - load more data
            function listenScroll(e):void{
                let targetEl = $(e.target);
                if(targetEl.scrollTop() +targetEl.innerHeight()>=targetEl[0].scrollHeight - 100){
                    ctrl._loadMore();
                }

            }

            //Register scroll event and de-register
            this.$element.find(".grid-wrapper").scroll(listenScroll);
            this.$scope.$on('$destroy', function () {
                ctrl.$element.off('scroll', listenScroll);
            });
        }

        static $inject = ['$scope','usersUtils','tagsUtils','stateManagementService','$element','$filter','$q','$timeout'];
        constructor (public $scope:ng.IScope, public usersUtils:IUsersUtils,
                     public tagsUtils:ITagsUtilsService, public stateManagementService:IStateManagementService,
                        public $element:ng.IAugmentedJQuery, public $filter:ng.IFilterService,public $q:ng.IQService,
                        public $timeout:ng.ITimeoutService) {


        }
    }

    let UsersGridComponent:ng.IComponentOptions = {
        controller: UsersGridComponentController,
        templateUrl: 'app/layouts/users/components/users-grid/users-grid.component.html',
        bindings: {
            stateId: '<', // The ID of the state. Used for when using the name of the component from several places.
                         //Optional. If empty use STATE_ID constant
            fetchStateDelegate: '=',
            updateStateDelegate: '=',
            clearStateDelegate: "=",
            indicatorsTypes: "=",
            reloadPredefinedFiltersUsersCount: "=",
            tags:'<'
        }
    };
    angular.module('Fortscale.layouts.users')
        .component('usersGrid', UsersGridComponent);
}
