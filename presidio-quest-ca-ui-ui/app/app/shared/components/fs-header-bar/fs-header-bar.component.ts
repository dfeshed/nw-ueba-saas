module fortscale.shared.components.fsHeaderBar {

    import IStateService = angular.ui.IStateService;
    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;

    class FsHeaderBarController {
        static $inject = ['$element', '$scope', 'userUtils', '$state', 'auth','stateManagementService','FORTSCALE_BRAND_UI'];

        constructor (public $element:JQuery, public $scope:ng.IScope, public userUtils:any,
            public $state:IStateService, public auth: any,
                     public stateManagementService:IStateManagementService, public FORTSCALE_BRAND_UI:boolean) {

            // Defined in constructor so the 'this' will be bound to the instance
            this.goToUserProfile = (updateObj:any) => {
                // Clear user search control
                $('#main_user_search').find('input').val('');
                // Go to state
                this.$state.go('user.baseline', {userId: updateObj.value});
            };

        }

        userControlSettings:any;
        userControlResource:any;
        searchBoxElement:JQuery;
        aboutBullets:any[];

        private _initSettingsMenu () {
            let el:any = this.$element.find('.fs-header-bar-component--icons-container--settings-menu');
            el.kendoMenu({
                openOnClick: true,
                closeOnClick: true
            });
        }


        private _initAboutBullets():void{
            this.aboutBullets = [];
            this.aboutBullets[0] = {
                id: '0',
                name: 'About',
                templateUrl: 'app/layouts/about-templates/about.tamplate.html'
            };
            this.aboutBullets[1] = {
                id: '1',
                name: 'Legal Notice',
                templateUrl: 'app/layouts/about-templates/legal.tamplate.html'
            };
            // this.aboutBullets[2] = {
            //     id: '2',
            //     name: 'EULA',
            //     templateUrl: 'app/layouts/about-templates/eula.tamplate.html'
            // };

            this.aboutBullets[2] = {
                id: '3',
                name: 'Contact',
                templateUrl: 'app/layouts/about-templates/contact.tamplate.html'
            };
        }

        private _initUserControllSettings () {
            var ctrl = this;

            ctrl.userControlSettings = {
                dataValueField: 'id',
                dataTextField: 'fallBackDisplayName',
                /**
                 * Takes received users and creates fallBack display name for each, and prevents duplications.
                 * @param users
                 */
                dataTextFn: (users) => {
                    ctrl.userUtils.setFallBackDisplayNames(users);
                    ctrl.userUtils.preventFallBackDisplayNameDuplications(users);
                },
                placeholder: 'Search User'
            }
        }

        private _initUserControlResource () {
            this.userControlResource = {
                entity: 'user',
                params: {
                    page: 1,
                    size: 10,
                    sort_field: 'displayName',
                    sort_direction: 'ASC',
                    search_field_contains: '{{search}}'
                }
            };
        }

        goToUserProfile:Function;

        toggleSearch () {
            this.searchBoxElement.toggleClass('active');

            // Set focus to search
            if (this.searchBoxElement.hasClass('active')) {
                this.searchBoxElement.find('input').focus();
            }
        }

        closeSearch () {
            this.searchBoxElement.removeClass('active');
        }

        logout () {
            let stateManagementService:IStateManagementService = this.stateManagementService;
            stateManagementService.clearAllStates();
            this.auth.logout();
        }

        $onInit () {
            this._initUserControllSettings();
            this._initUserControlResource();
            this._initSettingsMenu();
            this._initAboutBullets();
            this.searchBoxElement = this.$element.find('.main-user-search');

            this.$scope.$root.$on('$stateChangeSuccess', this.closeSearch.bind(this));
        }

    }

    let fsHeaderBarComponent:ng.IComponentOptions = {
        templateUrl: 'app/shared/components/fs-header-bar/fs-header-bar.component.html',
        controller: FsHeaderBarController,
        controllerAs: '$ctrl',
        bindings: {
            loggedInUser: '<'
        }
    };

    angular.module('Fortscale.shared.components')
        .component('fsHeaderBar', fsHeaderBarComponent);
}
