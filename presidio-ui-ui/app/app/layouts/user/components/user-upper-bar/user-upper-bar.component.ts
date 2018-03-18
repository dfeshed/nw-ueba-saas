module Fortscale.layouts.user {
    'use strict';

    import IEntityActivityUtilsService = Fortscale.shared.services.entityActivityUtils.IEntityActivityUtilsService;

    class UpperBarController {

        dlpMode:boolean;
        user:any;
        isUserFollowed:boolean;
        isWatchRequestPending: boolean = false;
        _toggleUserWatchDelegate: () => void;

        WATCH_BUTTON_SELECTOR:string = '.watch-user-button-watch';
        UNWATCH_BUTTON_SELECTOR:string = '.watch-user-button-unwatch';
        ANIMATION_TIME:number = 500;


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
            let selector = this.isUserFollowed ? this.UNWATCH_BUTTON_SELECTOR : this.WATCH_BUTTON_SELECTOR;
            let element = this.$element.find(selector);
            this._expandElement(element);
        }

        _watchButtonFlip () {
            let shrinkSelector = this.isUserFollowed ? this.WATCH_BUTTON_SELECTOR : this.UNWATCH_BUTTON_SELECTOR;
            let expandSelector = this.isUserFollowed ? this.UNWATCH_BUTTON_SELECTOR : this.WATCH_BUTTON_SELECTOR;
            let shrinkElement = this.$element.find(shrinkSelector);
            let expandElement = this.$element.find(expandSelector);
            this._setTransitions(shrinkElement, expandElement);
            this.$scope.$applyAsync( () => {
                this._shrinkElement(shrinkElement);
                this._expandElement(expandElement);
            });



        }

        toggleUserWatchDelegate () {
            if (this.isWatchRequestPending) {
                return;
            }

            this.isWatchRequestPending = true;
            this._toggleUserWatchDelegate();
        }

        updateDLPMode(){
            this.dlpMode = this.entityActivityUtils.updateDlpMode();
        }

        $onInit () {
            this.dlpMode = this.entityActivityUtils.reloadDlpModeFromConfiguration();
            this.$scope.$watch(() => this.user && this.user.followed, (isUserFollowed:boolean) => {
                if (isUserFollowed !== undefined) {
                    this.isWatchRequestPending = false;

                    if (this.isUserFollowed !== undefined) {
                        this.isUserFollowed = isUserFollowed;
                        this._watchButtonFlip();
                    } else {
                        this.isUserFollowed = isUserFollowed;
                        this._initialWatchButtonFlip();
                    }
                }
            });
        }

        static $inject = ['$scope', '$element', '$timeout','entityActivityUtils','TAGS_FEATURE_ENABLED'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery, public $timeout:ng.ITimeoutService, public entityActivityUtils:IEntityActivityUtilsService,public TAGS_FEATURE_ENABLED:string) {
        }
    }

    let userUpperBarComponent:ng.IComponentOptions = {
        controller: UpperBarController,
        templateUrl: 'app/layouts/user/components/user-upper-bar/user-upper-bar.component.html',
        bindings: {
            user: '<userModel',
            tags: '<',
            removeTagDelegate: '&',
            addTagDelegate: '&',
            //addNewTagDelegate: '&',
            _toggleUserWatchDelegate: '&toggleUserWatchDelegate',
        }
    };

    angular.module('Fortscale.layouts.user')
        .component('userUpperBar', userUpperBarComponent);
}
