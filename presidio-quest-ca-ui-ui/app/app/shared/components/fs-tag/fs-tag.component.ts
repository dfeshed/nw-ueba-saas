module Fortscale.shared.components.fsTag {

    import ITagDefinition = Fortscale.shared.services.tagsUtilsService.ITagDefinition;

    const ABOUT_TO_REMOVE_CLASS_NAME = 'about-to-remove';

    class TagController {

        tag:ITagDefinition;
        removeTagDelegate:Function;
        removeTagDelegateCheck: Function;
        enableRemoveTag:boolean;


        /**
         * Sets 'about-to-remove' class on the element, or removes it.
         *
         * @param {boolean} state
         */
        setAboutToRemove (state) {
            if (state) {
                this.$element.addClass(ABOUT_TO_REMOVE_CLASS_NAME);
            } else {
                this.$element.removeClass(ABOUT_TO_REMOVE_CLASS_NAME);
            }
        }

        $onInit () {
            this.enableRemoveTag = !_.isNil(this.removeTagDelegate); // If removeTagDelegate supplied enable tag removing
        }

        static $inject = ['$element', '$scope'];

        constructor (public $element:ng.IAugmentedJQuery, public $scope:ng.IScope) {
        }
    }

    let fsTagComponent:ng.IComponentOptions = {
        controller: TagController,
        templateUrl: 'app/shared/components/fs-tag/fs-tag.component.html',
        bindings: {
            tag: '<',
            removeTagDelegate: '&?',
        }
    };
    angular.module('Fortscale.shared.components.fsTags',[])
        .component('fsTag', fsTagComponent);
}
