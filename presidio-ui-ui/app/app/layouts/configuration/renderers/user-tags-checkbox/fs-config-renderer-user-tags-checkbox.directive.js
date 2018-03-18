(function () {
    'use strict';

    function fsConfigRendererUserTagsCheckboxDirective (tagsUtils) {

        /**
         *
         * @param {object} scope
         * @param {object} element
         * @param {object} attrs
         * @param {array<object>|object} ctrl
         */
        function linkFn (scope, element, attrs, ctrl) {
            // Link function logic

            // Set initial values
            if (scope.configItem.value !== null && scope.configItem.value !== '') {
                ctrl._populateTagsFromCSV(scope.configItem.value);
            }
        }

        /**
         * The directive's controller function
         *
         * @constructor
         */
        function FsConfigRendererUserTagsCheckboxController ($element, $scope) {
            // Put dependencies on the instance
            var ctrl = this;
            ctrl.$element = $element;
            ctrl.$scope = $scope;

            ctrl.tags = {
            };

            // Invoke init
            ctrl.init();
        }

        angular.extend(FsConfigRendererUserTagsCheckboxController.prototype, {

            /**
             * Takes a string and populates tag object
             * @param {string} csv
             * @private
             */
            _populateTagsFromCSV: function (csv) {
                var tags = csv.split(',');
                _.each(tags, tagId => {
                    if (this.tags[tagId]) {
                        this.tags[tagId].value = true;
                    }
                });
            },

            /**
             * Returns a csv of all tags that have a value set to true
             * @returns {string}
             * @private
             */
            _getCSV: function () {
                return _.map(_.filter(this.tags, tag => tag.value), tag => tag.id)
                    .join(',');
            },

            isEmpty: function () {
                return this._getCSV() === '';
            },

            changeTag: function () {

                // Get the csv
                var csv = this._getCSV();

                // Get the ngModel
                var el = this.$element.find('input.hidden-input');
                var ngModel = el.controller('ngModel');

                // Set the ng model value
                ngModel.$setViewValue(csv);
                ngModel.$setDirty('true');
                ngModel.$render();
            },

            /**
             * Init
             */
            init: function init () {
                var ctrl = this;

                ctrl.isLoading = true;

                // Get the tags
                tagsUtils.getTags()
                    .then(tags => {

                        // Create tags object
                        ctrl.tags = {};
                        _.each(tags.data, tag => {
                            ctrl.tags[tag.name] = {
                                displayName: tag.displayName,
                                id: tag.name,
                                value: false
                            };
                        });

                        // Check tags that are set
                        if (ctrl.$scope.configItem.value) {
                            ctrl._populateTagsFromCSV(ctrl.$scope.configItem.value);
                        }
                        ctrl.isLoading = false;
                    })
                .catch(err => {
                    console.error(err);
                    ctrl.isLoading = false;
                });
            }
        });

        FsConfigRendererUserTagsCheckboxController.$inject = ['$element', '$scope'];

        return {
            restrict: 'E',
            templateUrl: 'app/layouts/configuration/renderers/user-tags-checkbox/' +
            'fs-config-renderer-user-tags-checkbox.view.html',
            link: linkFn,
            controller: FsConfigRendererUserTagsCheckboxController,
            controllerAs: 'ctrl', //Change to the desired controller name
            bindToController: {}
        };
    }

    fsConfigRendererUserTagsCheckboxDirective.$inject = ['tagsUtils'];

    angular.module('Fortscale.layouts.configuration')
        .directive('fsConfigRendererUserTagsCheckbox', fsConfigRendererUserTagsCheckboxDirective);
}());
