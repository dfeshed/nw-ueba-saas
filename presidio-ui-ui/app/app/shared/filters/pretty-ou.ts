(function () {
    'use strict';


    function prettyOU ($sce:ng.ISCEService) {

        /**
         * Converts a raw string to pretty ou string
         * @example this: "ou=admin" will be this: "Admin"
         *
         * @param {string} ouValue
         * @returns {string}
         */
        function valToPrettyOu (ouValue:string):string {
            // If starts with OU=
            if (/^ou=/i.test(ouValue)) {

                // Remove first three chars and trim
                ouValue = ouValue.substr(3).trim();

                if (ouValue.length === 0) {
                    return '';
                }

                // If length is one
                if (ouValue.length === 1) {
                    return ouValue.toUpperCase();
                }

                // If length greater then one, uppercase first letter and return
                return ouValue.charAt(0).toUpperCase() + ouValue.substr(1);
            }

            return ouValue;
        }

        /**
         * Create an ou group element with href
         *
         * @param {string} ouValue
         * @returns {IAugmentedJQuery}
         */
        function createGroupElement (ouValue:string):ng.IAugmentedJQuery {

            // Create the pretty ou value
            let prettyVal = valToPrettyOu(ouValue);

            // create an anchor element
            let el = angular.element(`<a>${prettyVal}</a>`);

            // add ng-href attribute
            el.attr('href',
                `#/d/explore/users?filters=users.ou=~${prettyVal}`);

            // return the element
            return el;

        }

        /**
         * Create ou groups container element that holds all ou groups element.
         * @param {string} ouValues
         * @returns {IAugmentedJQuery}
         */
        function createGroupsElement (ouValues:string[]):ng.IAugmentedJQuery {
            // Create a span element to contain the groups
            let containerEl = angular.element(
                '<span class="ou-groups-container" style="display: flex; flex-flow: column nowrap;"></span>');
            // populate the element
            let ouGroups = _.map(ouValues, createGroupElement);
            _.each(ouGroups, (ouGroup) => {
                containerEl.append(ouGroup);
            });

            return containerEl;

        }

        /**
         * If received string starts with OU= then remove it and uppercase first letter
         *
         * @param {string} ouStr
         * @returns {IAugmentedJQuery|string}
         */
        function prettyOUFilter (ouStr:string):ng.IAugmentedJQuery|string {

            // Return the value as is if its not a sting.
            if (!_.isString(ouStr)) {
                return ouStr;
            }

            //Split into csv
            let ouValues = ouStr.split(',');


            // If length is zero
            if (ouValues.length === 0) {
                return 'No Organizational unit';
            }

            let el = createGroupsElement(ouValues);

            return $sce.trustAsHtml(el);

        }

        return prettyOUFilter;
    }

    angular.module('Fortscale.shared.filters')
        .filter('prettyOU', ['$sce', prettyOU]);
}());
