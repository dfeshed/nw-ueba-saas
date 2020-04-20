module Fortscale.layouts.users {
    'use strict'
    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;


    //Manage the actions bar above the users grid
    class UsersActionsBarController {
        usersCount:number;
        reloadDelegate: (reloadUsersData:boolean,riskyUsers:boolean, watchedUsers:boolean, taggedUser:boolean, tagsList:boolean) =>void;

        allUsersFollowed:boolean = false;
        isWatchRequestPending: boolean = false;
        tags : {id:any,value:any, count?:number}[]=[];

        //State delegates and ID
        updateStateDelegate: (state:any) => void;
        fetchStateDelegate: (attributeName:string) => any;
        stateId:string;


        //Selectors for watch users
        WATCH_BUTTON_SELECTOR:string = '.watch-user-button-watch';
        UNWATCH_BUTTON_SELECTOR:string = '.watch-user-button-unwatch';
        ANIMATION_TIME:number = 500;

        /*
        Export to csv
         */
        exportCSV():void {
            if (!this.usersCount || this.usersCount===0){
                return;
            }
            let state:UserFilter = this.stateManagementService.readCurrentState(this.stateId);

            //Init the filter
            let filter:UserFilter = _.clone(state);
            filter.addAlertsAndDevices = true;

            let src:string = this.usersUtils.getUsersExportUrl(filter,this.usersCount);
            this.fsDownloadFile.openIFrame(src);
        }

        /**
         * Watch action
         * Trigger update watch for all users which match to the filter,
         * and reload grid and counts
         */
        watchAction():void {
            if (!this.usersCount || this.usersCount===0){
                return;
            }
            //If already action execution - stop execute
            if (this.isWatchRequestPending){
                return;
            }
            this.isWatchRequestPending = true;


            //Get the state and call watchUsers with the RELEVANT FILTER filter
            let state:UserFilter = this.stateManagementService.readCurrentState(this.stateId);
            let filter:UserFilter = _.clone(state);
            filter.addAlertsAndDevices = false;

            this.usersUtils.watchUsers(!this.allUsersFollowed, filter).then(()=>{
                //When sucess - update the flag, reload the grid and upfate predefined watch filter with the new count

                this.allUsersFollowed =  !this.allUsersFollowed;
                this.reloadDelegate(true, false, true,false,false);
                this.isWatchRequestPending = false;

            }).catch(err => {
                    //If error take place- shouw toast
                    this.isWatchRequestPending = false;
                    this.toastrService.error("Could not add users to watchlist - please try again.");
                    return null;
                })
            ;

        }

        /**
         * this method add tag(s) to all the users matching to current filter.
         * In addition, after operation success, we update the state with the new selected tags,
         * and relaod the complete tags list (because if add not existing tag it should refresh the tags list.
         *
         * @param tagIds
         * @returns {IPromise<TResult>}
         */
        addTagsDelegate (tagIds:string[]):any{

                if (!this.usersCount || this.usersCount===0){
                    return;
                }
                //Get the current filter, update it, and call "add tags".
                let state:UserFilter = this.stateManagementService.readCurrentState(this.stateId);

                //Init the filter
                let filter:UserFilter = _.clone(state);
                filter.addAlertsAndDevices = false;

                return this.usersUtils.addTags(filter, tagIds).then((res:{data:{count:number}})=> {

                    //Refresh the list of tags because add tag to user may add it to the system
                    this._updateStateTagsWithNewTags(tagIds,null);
                    this.reloadDelegate(false,false, false, true,true);


                    this.toastrService.success(this._getTagsSuccessMessage(tagIds.length,res.data.count,"assigned to"));


                }).catch(err => {
                    this.toastrService.error("Failed to tag users. Please try again later.");

                });
        }

        _getTagsSuccessMessage(tagCount:number, usersAffected:number, addedOrRemoved:string):string{
            let message:string = tagCount + " tag";
            message += tagCount>1 ? "s have" : " has";
            message += " been " + addedOrRemoved + " ";
            message += usersAffected + " user";
            message += usersAffected > 1 ? "s" : "";

            return message;
        }

        removeTagsDelegate(tagIds:string[]):ng.IPromise<any>{
            if (!this.usersCount || this.usersCount===0){
                return;
            }
            let state:UserFilter = this.stateManagementService.readCurrentState(this.stateId);

            //Init the filter
            let filter:UserFilter = _.clone(state);
            filter.addAlertsAndDevices = false;

            return this.usersUtils.removeTags(filter,tagIds).then((res:{data:{count:number}})=>{
                this._updateStateTagsWithNewTags(null, tagIds);
                this.reloadDelegate(false,false,false,true,false);
                this.toastrService.success(this._getTagsSuccessMessage(tagIds.length,res.data.count,"removed from"));

            }).catch(err => {
                this.toastrService.error("Add tags to all users failed");

            });
        }

        /**
         *
         *
         * @param addedTags- list of tag ids to add. Might be empty or null.
         * @param removedTags list of tag ids to remove. Might be empty or null.
         * @private
         */
        _updateStateTagsWithNewTags(addedTags:string[], removedTags:string[]):void{
            //Get current filter tags:
            let state:UserFilter = this.stateManagementService.readCurrentState(this.stateId);
            let filterTagIds:string[];
            //If no current tags -
            if (_.isNil(state.userTags)){
                if (addedTags && addedTags.length>0) {
                    filterTagIds = _.clone(addedTags);
                } else { //Remove tags
                    //Do nothing. Remove all
                    filterTagIds=null;
                }
            } else {
                //Update current list of tags for the state
                filterTagIds = state.userTags.split(",");
                _.remove(filterTagIds, (value)=>{
                    return value === 'any' || value === 'none';
                });


                _.each(addedTags, (tagId:string)=> {
                    filterTagIds.push(tagId);
                });
                _.each(removedTags, (tagId:string)=> {
                    filterTagIds = _.remove(filterTagIds, tagId);
                });
            }

            //Update state userTags seperated string from the filterTagsIds arrays
            if ( filterTagIds===null || filterTagIds.length===0){
                state.userTags=null;
            } else {

                //Remove duplicates if any
                filterTagIds = _.uniq(filterTagIds);
                state.userTags = filterTagIds.join(",");
            }
            //State always need to be update, so the gird will be refreshed
            this.stateManagementService.updateState(this.stateId,state);

        }
        searchTriggeredDelegate(searchText:string):ng.IPromise<any> {
            //Get the state and call freeTextSearch with the RELEVANT FILTER filter
            let state:UserFilter = this.stateManagementService.readCurrentState(this.stateId);
            let filter:UserFilter = _.clone(state);
            filter.addAlertsAndDevices = true;


            return this.usersUtils.freeTextSearch(filter,searchText).then((res:{data:{data:User[]}})=>{
                //When sucess - update the flag return the data
                if (res.data.data){
                    let users:User[]= res.data.data;
                    return users;//Extrace the list of users and return it
                }
            }).catch(err => {
                return null;
            })
            ;
        }


        $onInit () {
            //Init the button. The button has 2 states  - "watched / unwatched". Each time that allUserFollowed update it should flip to the other state
            this.$scope.$watch(() => this.allUsersFollowed, (allUsersFollowed:boolean) => {
                if (allUsersFollowed !== undefined) {
                    this.isWatchRequestPending = false;

                    if (this.allUsersFollowed !== undefined) {
                        this.allUsersFollowed = allUsersFollowed;
                        this._watchButtonFlip();
                    } else {
                        this.allUsersFollowed = allUsersFollowed;
                        this._initialWatchButtonFlip();
                    }
                }
            });
        }


        /**
         * All follwing method handle the button flip flop

         */

        _shrinkElement (element) {

            element.css('transform', 'scaleY(0)');
        }

        _expandElement (element) {

            element.css('transform', 'scaleY(1)');
        }

        _setTransitions (shrinkElement, expandElement) {
            shrinkElement[0].style['WebkitTransition'] = `transform ${this.ANIMATION_TIME / 2 / 1000}s cubic-bezier(0, 0, 0.84, 0.15) 0s`;
            expandElement[0].style['WebkitTransition'] = `transform ${this.ANIMATION_TIME / 2 / 1000}s cubic-bezier(0, 0, 0.5, 1) ${this.ANIMATION_TIME / 2 / 1000}s`;
        }

        _initialWatchButtonFlip () {
            let selector = this.allUsersFollowed ? this.UNWATCH_BUTTON_SELECTOR : this.WATCH_BUTTON_SELECTOR;
            let element = this.$element.find(selector);
            this._expandElement(element);
        }

        _watchButtonFlip () {
            let shrinkSelector = this.allUsersFollowed ? this.WATCH_BUTTON_SELECTOR : this.UNWATCH_BUTTON_SELECTOR;
            let expandSelector = this.allUsersFollowed ? this.UNWATCH_BUTTON_SELECTOR : this.WATCH_BUTTON_SELECTOR;
            let shrinkElement = this.$element.find(shrinkSelector);
            let expandElement = this.$element.find(expandSelector);
            this._setTransitions(shrinkElement, expandElement);
            this.$scope.$applyAsync( () => {
                this._shrinkElement(shrinkElement);
                this._expandElement(expandElement);
            });

        }

        /**
         * End of button flip flop
         */
        static $inject =['$scope','stateManagementService','usersUtils','fsDownloadFile','toastrService','$element','TAGS_FEATURE_ENABLED'];
        constructor (public $scope:ng.IScope,
                     public stateManagementService:IStateManagementService,
                     public usersUtils:IUsersUtils,
                     public fsDownloadFile:any,
                     public toastrService:IToastrService,
                     public $element:ng.IAugmentedJQuery,
                     public TAGS_FEATURE_ENABLED:string){


        }
    }

    let UsersActionsBarComponent:ng.IComponentOptions = {
        controller: UsersActionsBarController,
        templateUrl: 'app/layouts/users/components/users-action-bar/users-action-bar.view.html',
        bindings: {
            stateId: '<',
            usersCount: '<',
            reloadDelegate: '=',
            allUsersFollowed: '<',
            tags: '<',
            fetchStateDelegate: '=',
            updateStateDelegate: '='
        }
    };
    angular.module('Fortscale.layouts.users')
        .component('usersActionBar', UsersActionsBarComponent);
}
