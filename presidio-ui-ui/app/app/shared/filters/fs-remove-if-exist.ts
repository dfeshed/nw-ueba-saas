declare var moment: any;

(function () {
    'use strict';

    /**
     * Returns a filter
     *
     * @returns {function(any): number}
     */
    function fsRemoveIsExists ():(val: string, textToRemove: string, prefixOnly:boolean)=>string {

        /**
         * This filter get text, and part of substring of this text,
         * it returns the text without the prefix

         *
         *  prefixOnly - IF TRUE - mark only textToRemove which is in the beginning of the text
         *  condition-optional. If false, return the original value
         *
         */
        return function (val: string, textToRemove: string, prefixOnly:boolean):string {

            //If no val or no textToMakeStronger return the value.
            if (!textToRemove || !val) {
                return val;
            }


            let fullTextLower:string = val.toLowerCase();
            let strongTextLower:string = textToRemove.toLowerCase();
            let startIndex = -1;
            if (prefixOnly){
                startIndex = fullTextLower.startsWith(strongTextLower)?0:-1;
            } else {

                //Extract the start and end indexes of the part which should be emphasis
                startIndex = fullTextLower.indexOf(strongTextLower);

            }
            if (startIndex === -1) {
                return val;
            }

            let endIndex = startIndex + strongTextLower.length;

            //Rebuild the string without the removed part
            let beforeRemovePart = val.substr(0,startIndex);

            let afterRemovePart = val.substring(endIndex);
            let newText= beforeRemovePart+afterRemovePart;
            return newText;
        }
    }




    angular.module('Fortscale.shared.filters')
        .filter('fsRemoveIsExists', fsRemoveIsExists);

}());
