module Fortscale.shared.components.fsUserTooltip {

    declare var Opentip:any;

    const TEMPLATE_URL = 'app/layouts/users/components/users-search-popup/users-search-popup-internal.template.html';
    const KEY_ARROW_UP:number=38;
    const KEY_ARROW_DOWN:number = 40;
    const NO_CURRENT_INDEX:number = -1;
    const USERS_SEARCH_CALLER_ID:string='users-search-popup';

    import IStateService = angular.ui.IStateService;
    import User = Fortscale.layouts.users.User;
    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;
    import UserFilter = Fortscale.layouts.users.UserFilter;

    class UsersSearchPopupComponentController {

        attributeName:string = "searchValue";
        searchText:string;
        timer:any;
        users:User[];

        //If the we hover a user using mouse or arrows the index should be 0 or greater.
        //If the user is on the search text input the index should be -1.
        currentHoverIndex:number = NO_CURRENT_INDEX;

        /**
         * Delegates
         */
        //Used to search userss for the dropdwon
        searchTriggeredDelegate: (searchText:{searchText:string}) => ng.IPromise<User[]>;

        updateStateDelegate: (state:any) => void;
        fetchStateDelegate: (attributeName:string) => any;
        stateId:string;

        /**
         * A string that holds a selector for the tooltip target
         */
        tooltipTargetSelector:string;

        /**
         * Received tooltip settings
         */
        _externalTooltipSettings:any;

        /**
         * Generated tooltip object
         */
        _tooltip:any;

        /**
         * Tooltip local settings object
         *
         * @type {{className: string, stem: boolean, stemLength: number, stemBase: number, hideDelay: number, tipJoint: string, fixed: boolean, removeElementsOnHide: boolean, group: string, background: string, borderRadius: number, borderColor: string, shadow: boolean, shadowBlur: number, shadowOffset: number[], shadowColor: string, containInViewport: boolean, offset: number[]}}
         * @private
         */
        _tooltipSettings:{} = {
            className: 'users-search-tooltip',
            showOn: 'creation',
            hideOn: 'tip',
            stem: true,
            stemLength: 8,
            stemBase: 12,
            hideDelay: 0.3,
            tipJoint: 'top',
            fixed: true,
            removeElementsOnHide: true,
            group: 'users-search-popup',
            background: '#f0f7f8',
            borderRadius: 3,
            borderColor: '#f0f7f8',
            shadow: true,
            shadowBlur: 15,
            shadowOffset: [0, 0],
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            containInViewport: true,
            offset: [0, 0]




        };

        /**
         * If tooltipTargetSelector was provided, this method returns the element the selector refers to.
         *
         * @returns {HTMLElement|null}
         * @private
         */
        _getTargetElement ():HTMLElement {
            if (this.tooltipTargetSelector) {
                let closest = this.$element.closest(this.tooltipTargetSelector);
                let inner = this.$element.find(this.tooltipTargetSelector);
                return closest.length ? closest[0] :
                    inner.length ? inner[0] : null;
            }
            return null;
        }

        /**
         * Set the current index for selected user
         */
        updateIndex(newIndex:number):void{
            this.currentHoverIndex = newIndex;
        }


        onSearchActive():void{
            let searchActive:boolean = this.searchText?this.searchText.length>0:false;

            if (this.timer){
                this.$timeout.cancel(this.timer);
            }

            if (searchActive) {
                this.timer = this.$timeout(this._loadUsers.bind(this), 500);
            }  else {
                   this._safeHide();
            }

        }

        //Get the users, clear the timer, and display / hide the list off users
        _loadUsers():void{
            let ctrl:any=this;
            ctrl.timer=null;
            ctrl.searchTriggeredDelegate({"searchText":this.searchText}).then((users:User[])=>{
                ctrl.users=users;

                this._safeHide();
                if (users.length>0) {
                    ctrl._initTooltip();
                }
            });
        }

        /**
         * Return true if the tooltip displayed
         * @returns {boolean|string}
         * @private
         */
        _isVisible(){
            return this._tooltip!=null && this._tooltip.visible;
        }

        _safeHide(){
            if (this._isVisible()) {
                this._tooltip.hide();
            };
            this.currentHoverIndex = NO_CURRENT_INDEX;
        }

        deleteSearchText():void{
            this.searchText=null;
            this.currentHoverIndex=NO_CURRENT_INDEX;
            this.applyFilter();
        }

        submitWithKeypress(e:any){

            if (e.keyCode===13) {
                //Key press happens before angular populate the value into the model
                let newValue:string = e.target.value;
                this.applyFilter(newValue);
            } else if ((this._isKeyUpPressed(e.keyCode,e.shiftKey) || e.keyCode===KEY_ARROW_DOWN) && this.users) {//Key up or down and there are users
                if (this._isKeyUpPressed(e.keyCode, e.shiftKey)) {//Arrow up
                    this.currentHoverIndex > 0 ? this.currentHoverIndex-- : this.currentHoverIndex = NO_CURRENT_INDEX;
                } else if (e.keyCode === KEY_ARROW_DOWN) { //Arrow down

                    this.currentHoverIndex < this.users.length - 1 ? this.currentHoverIndex++ : this.currentHoverIndex = this.users.length - 1;

                }
                this.stopEvent(e);
            } else if (e.keyCode===27){
                this._safeHide();
            }

        }

        /**
         * We need to prevent input curesur to move when arrow up or down. Submit with key press handle the actual event.
         * stop event should prevent keypress
         * @param e
         */
        stopEvent(e:any){
            if (this._isKeyUpPressed(e.keyCode, e.shiftKey)) {//Arrow up
                e.stopPropagation();
                e.preventDefault();
            } else if (e.keyCode===KEY_ARROW_DOWN) { //Arrow down
                e.stopPropagation();
                e.preventDefault();
            }
        }

        /**
         * Initiates the tooltip settings (creates an instance settings)
         *
         * @private
         */
        _initSettings ():void {
            this._tooltipSettings = _.merge(
                {},
                this._tooltipSettings,
                {
                    target: this._getTargetElement(),
                });
        }

        /**
         * Renders the tooltip
         *
         * @private
         */
        _initTooltip ():void {
            // Set this as ctrl for the callbacks
            let ctrl = this;

            let template = this.$templateCache.get(TEMPLATE_URL);
            let tooltipContent = this.$compile(angular.element(template))(ctrl.$scope);
            ctrl._tooltip = new Opentip(ctrl.$element, ctrl._tooltipSettings);
            ctrl._tooltip.setContent(tooltipContent);

            // Prevent close on mouseover tooltip
            ctrl._tooltip.content.on({
                mouseenter: function () {
                    ctrl._tooltip._abortHiding();
                },
                mouseleave: function () {
                    ctrl._tooltip.prepareToHide();
                    //ctrl._tooltip._abortHiding(); //If you want to show on leave
                }
            });
        }

        /**
         * Cleansup the tooltip when the scope is destroyed
         *
         * @private
         */
        _initCleanup ():void {
            this.$scope.$on('$destroy', () => {
                if (this._tooltip != null) {
                    this._tooltip.adapter.remove(this._tooltip.container);
                    this._tooltip.container = null;
                    this._tooltip.tooltipElement = null;
                    this._tooltip = null;
                }
            });

        }


        _initCloseOnWindowSizeChange(){
            let ctrl:any=this;
            angular.element(this.$window).bind('resize', function () {
                ctrl.closePopup();

            });
        }

        //Close the popup and reset the search text
        closePopup():void{
            this.searchText=null;
            this._safeHide();

        }

        $onInit ():void {
            this.tooltipTargetSelector="users-search-popup";
            this._initSettings();
            //this._initTooltip();
            this._initCleanup();
            this._initCloseOnWindowSizeChange();
            this.$scope.$watch(this._stateWatchFn.bind(this), this._stateWatchActionFn.bind(this));

            this.stateManagementService.registerToStateChanges(this.stateId,USERS_SEARCH_CALLER_ID,this._safeHide.bind(this));

        }

        //Apply filter actually affect the state
        applyFilter(differentSeachText?:string):void{

            if (this.currentHoverIndex>NO_CURRENT_INDEX){
                this._gotoUserPage();
            } else {
                let finalSearchString:string = differentSeachText?differentSeachText:this.searchText
                //Update the filter
                this._safeHide();
                this.updateStateDelegate({
                    id: this.attributeName,
                    type: 'DATA',
                    value: finalSearchString,
                    immediate: true
                });
            }
        }

        _gotoUserPage():void{
            //Redirect to profile page of specific user
            if (!_.isNil(this.currentHoverIndex) && this.currentHoverIndex>=0){
                this.$state.go("user.baseline",
                    {"userId": this.users[this.currentHoverIndex].id});

                this._safeHide();
            } else {
                console.error("Cannot go to user page if currentHoverIndex is smaller then 0 ");
            }

        }

        _stateWatchFn():void{
            if (this.fetchStateDelegate) {
                return this.fetchStateDelegate(this.attributeName);
            }
        }

        _isKeyUpPressed(keyCode:number, isShiftClicked:boolean){
            return keyCode === KEY_ARROW_UP && !isShiftClicked;
        }





        isRoleFilterTurnedOn():boolean{
            let userFilter: UserFilter = this.stateManagementService.readCurrentState(this.stateId);
            return !_.isNil(userFilter.positions) && userFilter.positions!='';
        }

        isDepartmentFilterTurnedOn():boolean{
            let userFilter: UserFilter = this.stateManagementService.readCurrentState(this.stateId);
            return !_.isNil(userFilter.departments) && userFilter.departments!='';
        }


        /**
         * Watch action function . Set the value for the searchTaxt from outside
         *
         * @param {string|number} value
         */
        _stateWatchActionFn(value):void {
           this._safeHide();
           this.searchText = value;
        }

        static $inject = ['$scope', '$element','$templateCache','$compile','$window','$timeout','$state','stateManagementService'];

        constructor (public $scope:ng.IScope, public $element:JQuery,
                     public $templateCache:ng.ITemplateCacheService,
                     public $compile:ng.ICompileService,
                     public $window:ng.IWindowService,
                     public $timeout: ng.ITimeoutService,
                     public $state:IStateService,
                     public stateManagementService:IStateManagementService
                     ) {

        }

    }

    let UsersSearchPopupComponent:ng.IComponentOptions = {

        controller: UsersSearchPopupComponentController,
        controllerAs: '$ctrl',
        templateUrl: 'app/layouts/users/components/users-search-popup/users-search-popup.template.html',
        bindings: {
            searchTriggeredDelegate: "&",
            fetchStateDelegate: '=',
            updateStateDelegate: '=',
            stateId: '<',
        }

    }



    angular.module('Fortscale.shared.components')
        .component('usersSearchPopup', UsersSearchPopupComponent);
}
