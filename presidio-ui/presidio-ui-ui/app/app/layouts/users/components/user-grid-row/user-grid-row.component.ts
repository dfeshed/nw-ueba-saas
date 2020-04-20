module Fortscale.layouts.users {
    'use strict'

    //User intefaces
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;
    import ITagsUtilsService = Fortscale.shared.services.tagsUtilsService.ITagsUtilsService;
    import IUserTagsUtilsService = Fortscale.layouts.user.IUserTagsUtilsService;

    //Devices interfaces
    // import IUserDevice = Fortscale.shared.services.deviceUtilsService.IUserDevice;
    // import IDeviceUtilsService = Fortscale.shared.services.deviceUtilsService.IDeviceUtilsService;

    const MAX_DEVICE_TO_DISPLAY:number = 3;
    class UsersGridUserController {

        // enrichedUserDevices: IUserDevice[];
        user:any;   //Cloned user - all changes should be done on this user.
        _user:any; //original user- should be kept clean from changes
        userTags:ITagDefinition[] = [];
        userDisplayname: string;
        tags:ITagDefinition[]; //All tags in system
        //if miniView-true show only partial user-row.
        miniView:boolean=false;
        rolesFiltered:boolean
        departmentFiltered:boolean;
        postRemoveTagDelegate:Function;

        /**
         * Delegate to remove the tag from the user and refresh the userTags list
         * @param tag
         */
        removeTagDelegate (tag:ITagDefinition) : void {
            let ctrl:any = this;
            this.userTagUtils.removeTag(tag, this.user).then(() => {
                ctrl.tagsUtils.getTagsFromCacheOnly().then((tagsPromise:any) => {
                    ctrl._initUserTags(tagsPromise.data);
                    ctrl.postRemoveTagDelegate({"user":this.user});

                });

            });

        }

        // /**
        //  * This method counts the number of devices up to MAX_DEVICE_TO_DISPLAY
        //  * @returns {any} 0 if 0 devices or list is empty, return 3+ if there are more then 3 devices. Return 1-3 if there are 1-3 devices
        //  *
        //  */
        // getDevicesAmount():string  {
        //     if (this.user.sourceMachineCount <= 3){
        //         return this.user.sourceMachineCount;
        //     } else {
        //         //Return 3+ devices
        //         return MAX_DEVICE_TO_DISPLAY + "+";
        //     }
        // }

        // /**
        //  * This method prepare the devices list before displaying in the ui:
        //  * - Remove devices which not relevant (count  = 0)
        //  * - Move the "other devices" to the end of the list
        //  * - Add percentage attribute to each device in the list
        //  * @private
        //  */
        // _initEnrichedDevices() :void{
        //     let devices: IUserDevice[] = <IUserDevice[]>this.deviceUtilsService.removeZeroCount(this.user.devices);
        //     this.deviceUtilsService.repositionOthers(devices);
        //     if (devices && devices.length>0) {
        //
        //         this.enrichedUserDevices = <IUserDevice[]>this.deviceUtilsService.updatePercentageOnDevice(devices);
        //     } else {
        //         this.enrichedUserDevices = [];
        //     }
        // }



        $onInit () {
            this.user = _.cloneDeep(this._user);
            this.userUtils.setFallBackDisplayNames([this.user]);

            if (!this.miniView) {
                //Init following only for full view
                // this._initEnrichedDevices();
                this.scope.$watch(()=> {
                    return this.tags;
                }, () => {

                    if (this.tags && this.tags.length > 0) {
                        this._initUserTags(this.tags);
                    }
                });

            }
        }

        /**
         * Rebuild user tags list, build list of ITagDefinition out of String list
         * private
         * private
         */

        //first:boolean=true;
        _initUserTags (allTagsInSystem:ITagDefinition[]) {

            let ctrl:any = this;
            let userTagsTemp:ITagDefinition[]= [];
            _.each(ctrl.user.tags, tagName => {
                let tag:any= _.find(allTagsInSystem, {name: tagName});
                if (tag) {
                    userTagsTemp.push(tag);
                }
            });

            ctrl.userTags = userTagsTemp;

        }

        static $inject = ['$scope','tagsUtils','userTagsUtils', 'userUtils','$timeout'];
        constructor (public scope:ng.IScope,
                     public tagsUtils:ITagsUtilsService, public userTagUtils:IUserTagsUtilsService,
                     public userUtils:any, public $timeout:ng.ITimeoutService) {



        }
    }

    let UsersGridRowComponent:ng.IComponentOptions = {
        controller: UsersGridUserController,
        templateUrl: 'app/layouts/users/components/user-grid-row/user-grid-row.view.html',
        bindings: {
            _user: '<user',
            tags: '<?',
            miniView: '<?',
            searchText:'<?',
            rolesFiltered:'<?',
            departmentFiltered: '<?',
            postRemoveTagDelegate: '&',


        }
    };
    angular.module('Fortscale.layouts.users')
        .component('userGridRow', UsersGridRowComponent);
}



