module Fortscale.layouts.user {

    import INanobarAutomation = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomation;
    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    import ITagsUtilsService = Fortscale.shared.services.tagsUtilsService.ITagsUtilsService;
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;
    import IComment = Fortscale.shared.services.alertUpdatesService.IComment;
    import FeedbackObject = Fortscale.shared.services.alertUpdatesService.FeedbackObject;
    import AlertFeedback = Fortscale.shared.services.alertUpdatesService.AlertFeedback;
    import AlertStatus = Fortscale.shared.services.alertUpdatesService.AlertStatus;

    const LOADING_PROGRESS_PROPERTY_NAME = 'loadingProgress';

    class UserController {
        user:any;
        tags:ITagDefinition[];
        activities:IActivities;
        alerts:any[];
        sortedAlerts:any[];
        NANOBAR_ID = 'user-page';


        /**
         * Removes a tag from a user,
         * @param tag
         */
        removeTag (tag:ITagDefinition) {
            this.userTagsUtils.removeTag(tag, this.user)
                .then(user => {
                    this.user = _.cloneDeep(user);
                })

        }

        _findTagByNameIgnoreCase(tagName:string):ITagDefinition{
            let tagFound:ITagDefinition;
            _.each(this.tags,(tag:ITagDefinition)=>{
                if (tag.name && tagName && tag.name.toLowerCase() === tagName.toLowerCase()){
                    tagFound = tag;
                }
            })

            return tagFound;
        }

        /**
         * Adds a tag to the user
         * @param {ITagDefinition} tag
         */
        addTag (tag:ITagDefinition) {
            let ctrl:any=this;
            let tagFromTagsList:ITagDefinition = this._findTagByNameIgnoreCase(tag.name);
            if (!_.isNil(tagFromTagsList) && tagFromTagsList.isAssignable===false){
                //If tag exists and not assignable, show error:
                this.toastrService.error("This tag cannot be assigned to the user. Please try different tag name");
                return;
            }
            this.userTagsUtils.addTag(tag.name, this.user, tag.displayName?tag.displayName: tag.name)
                .then(user => {
                    //If tag is new - reload tags list

                    if (!tagFromTagsList){//Tag is new
                        ctrl._initLoadingTags();
                    }
                    ctrl.user = _.cloneDeep(user);

                })
        }

        /**
         * Adds a new tag to system and to user
         * @param {string} tagName
         */
        //addNewTag (tagName:string):void {
        //    this.userTagsUtils.addNewTag(tagName, this.tags, _.cloneDeep(this.user))
        //        .then(({user: user, tags: tags}:{user:any, tags:ITagDefinition[]})=> {
        //            if (user) {
        //                this.user = _.cloneDeep(user);
        //            }
        //
        //            if (tags) {
        //                this.tags = tags;
        //            }
        //
        //        });
        //}

        /**
         * Start of delegate methods of alert update (add comment, update comment, delete comment amd update feedbacka and status
         */
        addComment(alertId :String, comment: IComment){
            let alert: any = _.find(this.alerts, {id: alertId});
            alert.analystFeedback.unshift(comment);
            this.alerts = _.cloneDeep(this.alerts);//Change the referench to trigger watchers

        }

        updateComment(alertId :String, comment: IComment){
            let alert: any = _.find(this.alerts, {id: alertId});


            _.remove(alert.analystFeedback, function(commentInArray:any) {
                return commentInArray.id === comment.id;
            });

            alert.analystFeedback.unshift(comment);
            this.alerts = _.cloneDeep(this.alerts);//Change the referench to trigger watchers

        }

        deleteComment(alertId :String, commentId: String){
            let alert: any = _.find(this.alerts, {id: alertId});
            _.remove(alert.analystFeedback, function(commentInArray:any) {
                return commentInArray.id === commentId;
            });

            this.alerts = _.cloneDeep(this.alerts);//Change the referench to trigger watchers

        }

        updateFeedback(alertId :String, feedbackObject:FeedbackObject){
            //On feedback update we need to reload the whole user
            this._initLoadingSequence();
        }

        /**
         * End of delegate methods of alert update (add comment, update comment, delete comment amd update feedbacka and status
         */

        toggleUserWatch () {
            this.userWatchUtil.changeUserWatchState(this.user, !this.user.followed)
                .then(user => this.user = user)
                .catch((err) => {
                    console.error(err);
                    this.toastrService.error(
                        `There was an error trying to change user's ${this.user.fallbackDisplayName} watch state.<br>Please try again later.`);
                })

        }

        _initLoadingTags ():ng.IPromise<void> {
            let promise = this.tagsUtils.getTags()
                .then((res:ng.IHttpPromiseCallbackArg<any>) => {
                    this.tags = <ITagDefinition[]>res.data;
                })
                .catch((err) => {
                    console.error('There was an error fetching tags.', err);
                    this.tags = [];
                });

            this.fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
            return promise;
        }

        /**
         * Start the load user sequence
         * @private
         */
        _initLoadUser ():ng.IPromise<void> {
            let promise = this.userUtils.getUsersDetails([this.$stateParams.userId])
                .then((users:any) => {
                    if (!users.length) {
                        this._goBackHistoryOrHome(`No user was returned for id ${this.$stateParams.userId} .`);
                        return;
                    }
                    this.userUtils.setFallBackDisplayNames(users);
                    this.userUtils.setUsersFullAddress(users);
                    this.user = users[0];

                })
                .catch((err) => {
                    console.error('There was an error loading user.', err);
                    this.user = {};
                });

            this.fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
            return promise;


        }

        /**
         * Loads a user's alerts
         * @returns {IPromise<TResult>}
         * @private
         */
        _initLoadingAlerts () {
            let promise = this.$http.get(`${this.BASE_URL}/alerts`, {
                params: {
                    entity_id: this.$stateParams.userId,
                    load_comments: true
                }
            })
                .then((res:any) => {
                    this.alerts = res.data.data;
                })
                .catch((err) => {
                    console.error('There was an error loading user alerts.', err);
                    this.alerts = [];
                });

            this.fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promise);
            return promise;
        }


        /**
         * Initiates assets loading sequence
         *
         * @private
         */
        _initLoadingSequence ():void {
            let promiseTags = this._initLoadingTags();
            this.fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promiseTags);

            let promiseUser = this._initLoadUser()
                .then(() => {
                    if (!this.user) {
                        return;
                    }

                    this._initLoadingAlerts();
                });

            this.fsNanobarAutomationService.addPromise(this.NANOBAR_ID, promiseUser);

        }

        _goBackHistoryOrHome (warnMsg:string) {
            if (window.history.length) {
                console.warn(warnMsg + ' Going back to previous page.');

                // We use the applyAsync here to give angular a chance to complete the transition and register to the history.
                this.scope.$applyAsync(() => {
                    window.history.back();
                });
            } else {
                console.warn(warnMsg + ' Redirecting to Overview.');
                this.$state.go('overview');
            }
            return;
        }

        _init ():void {

            if (!this.$stateParams.userId) {
                this._goBackHistoryOrHome('No user id was provided in the url.');
                return;
            }

            this.activities = new Activities();

            // Start loading assets
            this._initLoadingSequence();

            this.scope.$on('userRiskScore:AlertsSorted', (evt: ng.IAngularEvent, sortedAlerts: any[]) => {
                if (sortedAlerts) {
                    this.scope.$applyAsync(() => {
                        this.sortedAlerts = sortedAlerts;
                    });
                }
            })
        }


        static $inject = ['$scope', '$element', '$http', '$stateParams', 'fsNanobarAutomationService', 'userUtils',
            'userTagsUtils', 'tagsUtils', 'userWatchUtil', 'toastrService', '$state',
            'BASE_URL'];

        constructor (public scope:ng.IScope, public element:ng.IAugmentedJQuery, public $http:ng.IHttpService,
                     public $stateParams:{userId:string},
                     public fsNanobarAutomationService:INanobarAutomationService, public userUtils:any,
                     public userTagsUtils:IUserTagsUtilsService, public tagsUtils:ITagsUtilsService,
                     public userWatchUtil:IUserWatchUtilService, public toastrService:IToastrService,
                     public $state:angular.ui.IStateService, public BASE_URL:string) {
            this._init();

        }
    }

    angular.module('Fortscale.layouts.user')
        .controller('UserController', UserController);
}
