module Fortscale.layouts.user {
    'use strict';
    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;


    class TagsController {

        NEW_TAG_INPUT_SELECTOR:string = '.add-tag-section--new-tag-input';
        _user:{tags:string[]};
        user:{tags:string[]};
        _tags:ITagDefinition[];
        tags:ITagDefinition[];
        userTags:ITagDefinition[];
        addTagDelegate:(locals:{tag:ITagDefinition}) => void;
        //addNewTagDelegate:(locals:{tagName:string}) => void;
        newTagModel:string;

        showAddTag:boolean;
        showNewTag:boolean;
        dropDownWindowClickDeregister:() => void;

        /**
         * Click handler for clicking anywhere. It will check if the click is outside of the dropdown, and if so it will close.
         *
         * @param {Event} evt
         * @private
         */
        _dropDownWindowClickHandler (evt:Event):void {
            if (this.$element[0] === evt.target || !!this.$element.has(<Element>evt.target).length) {
                return;
            } else {
                this.$scope.$applyAsync(() => {
                    this._deactivateDropDown();
                });
            }
        }

        /**
         * Activate click watch for the entire document. It also contains the deregister assignment, and the cleanup.
         * @private
         */
        _addDropDownWindowClickWatch ():void {
            let ctrl = this;

            // click handler function. Wraps the "real" function to fix the "this" while allowing deregistering.
            function dropDownOnWindowClickHandler (evt:Event) {
                ctrl._dropDownWindowClickHandler(evt);
            }

            // Deregister assignment.
            this.dropDownWindowClickDeregister = ():void => {
                ctrl.$window.removeEventListener('click', dropDownOnWindowClickHandler, true);
            };

            // Add click handler
            ctrl.$window.addEventListener('click', dropDownOnWindowClickHandler, true);
            // Add cleanup handler
            ctrl.$scope.$on('$destroy', this.dropDownWindowClickDeregister);


        }

        /**
         * Activates the dropdown
         * @private
         */
        _activateDropDown ():void {
            this.showAddTag = true;
            this._addDropDownWindowClickWatch();
        }

        /**
         * Deactivates the dropdown
         * @private
         */
        _deactivateDropDown ():void {
            this.showAddTag = false;
            if (this.dropDownWindowClickDeregister) {
                this.dropDownWindowClickDeregister();
            }

        }

        /**
         * When component holds both the user object and the system tags, it will convert all the user tags into ITagDefinitions
         * @private
         */
        _initUserTags () {
            this.userTags = [];
            _.each(this.user.tags, tagName => {
                let tag:ITagDefinition = _.filter(this.tags, {name: tagName})[0];
                if (tag) {
                    this.userTags.push(tag);
                }
            });
        }

        /**
         * Watch for user and tags. Act when both are present.
         * @private
         */
        _initWatches () {
            this.$scope.$watchGroup(
                [
                    () => this.user,
                    () => this.tags
                ],
                ([user, tags]) => {

                    // // immute states
                    // this.user = _.cloneDeep(user);
                    // this.tags = _.cloneDeep(tags);
                    // init tags if there's a user and tags
                    if (user && tags) {
                        this._initUserTags();
                    }
                }
            )
        }

        /**
         * Toggles dropdown on and off
         */
        toggleDropDown ():void {
            if (this.showAddTag) {
                this._deactivateDropDown();
            } else {
                this._activateDropDown();
            }

        }

        /**
         * Toggles add-new-tag state
         */
        toggleAddNewTag ():void {
            this._deactivateDropDown();
            this.showNewTag = !this.showNewTag;
            this.newTagModel = null;
            if (this.showNewTag) {
                this.$timeout(function () {
                    this.$element.find(this.NEW_TAG_INPUT_SELECTOR).focus();
                }.bind(this),200);
            }
        }

        /**
         * Closes dropdown and invokes delegate
         * @param {ITagDefinition} tag
         */
        addTagItem (tag):void {
            this._deactivateDropDown();
            this.addTagDelegate({tag: tag});
        }

        /**
         * Handler for input keypress. Fires addNewTagDelegate when key is 13 (i.e. Enter)
         * @param {KeyboardEvent} evt
         */
        newTagKeyPressHandler (evt:KeyboardEvent):void {
            if (evt.keyCode === 13) {
                if (this.newTagModel) {
                    //this.addNewTagDelegate({tagName: this.newTagModel});


                    let tag:ITagDefinition = {name:this.newTagModel,displayName:this.newTagModel, active:true, createsIndicator: false, rules: undefined,
                        isAssignable:true, predefined:false};

                    this.addTagDelegate({tag: tag});
                    this.newTagModel = null;
                    this.showNewTag = false;
                }
            } else if (evt.keyCode === 27) {
                this.showNewTag = false;
                this.newTagModel = null;
            }
        }

        /**
         * Handler for input lost focus. Fires addNewTagDelegate.
         */
        newTagLostFocusHandler () {
            this.showNewTag = false;
            this.newTagModel = null;
        }

        $onInit () {
            this._initWatches();
        }

        static $inject = ['$window', '$scope', '$element', '$timeout'];

        constructor (public $window:ng.IWindowService, public $scope:ng.IScope, public $element:ng.IAugmentedJQuery,
            public $timeout:ng.ITimeoutService) {
        }
    }



    let userTagsComponent:ng.IComponentOptions = {
        controller: TagsController,
        templateUrl: 'app/layouts/user/components/user-tags/user-tags.component.html',
        bindings: {
            user: '<userModel',
            tags: '<tags',
            removeTagDelegate: '&',
            addTagDelegate: '&',
            //addNewTagDelegate: '&'
        }
    };

    angular.module('Fortscale.layouts.user')
        .component('userTags', userTagsComponent);
}
