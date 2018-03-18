declare var moment: any;

(function () {
    'use strict';

    /**
     * Returns a filter
     *
     * @returns {function(any): number}
     */
    function fsPartialStrong (
        $sce:ng.ISCEService):(val: string, textToMakeStronger: string, prefixOnly:boolean, allowBoldCondition?:boolean)=>string {

        /**
         * This filter get text, and part of substring of this text,
         * it returns html which emphasis the the sub text (if such given).
         * Pay attention that angular process html only if you use it has ng-html-bind and not {{}}
         *
         *  prefixOnly - IF TRUE - mark only textToMakeStronger which is in the begining of the text
         *  condition-optional. If false, return the original value
         *
         */
        return function (val: string, textToMakeStronger: string, prefixOnly:boolean, allowBoldCondition?:boolean):string {

            if (allowBoldCondition===false){
                return val;
            }
            //If no val or no textToMakeStronger return the value.
            if (!textToMakeStronger || !val) {
                return val;
            }


            let fullTextLower:string = val.toLowerCase();
            let strongTextLower:string = textToMakeStronger.toLowerCase();
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

            //Rebuild the string with the strong part
            let beforeStrongPart = val.substr(0,startIndex);
            let strongPart = val.substr(startIndex,strongTextLower.length);
            let afterStrongPart = val.substring(endIndex);
            let newHTMLUntrusted = beforeStrongPart +"<span style='color: #024d89;font-weight: 700;'>"+strongPart+"</span>"+afterStrongPart;
            return $sce.trustAsHtml(newHTMLUntrusted);
        }
    }




    angular.module('Fortscale.shared.filters')
        .filter('fsPartialStrong', ['$sce',fsPartialStrong]);

}());
