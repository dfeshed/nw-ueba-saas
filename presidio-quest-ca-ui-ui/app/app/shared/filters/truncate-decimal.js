(function () {
    'use strict';

    function trancateDecimal () {


        /**
         * This function get a string, if the string is number,
         * and the fraction part is equals to zero, return the number without the fraction part.
         * For any other scenario, return the string as it is.
         * @param valueAsString
         * @returns {*}
         */
        function trancateDecimalFilter(valueAsString) {

            //IF not a number - return the value as it is

            var trimmedValueAsString = valueAsString.trim();
          if (isNaN(trimmedValueAsString) ||
          !/[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?/.test(valueAsString) || /^0x/.test(valueAsString)){
          return valueAsString;
          }
            var numberAsInt = parseInt(trimmedValueAsString);
            var numberAsFloat = parseFloat(trimmedValueAsString);
            if (numberAsInt - numberAsFloat === 0){
                return numberAsInt;
            } else {
                return numberAsFloat;
            }
        }

        return trancateDecimalFilter;
    }

    trancateDecimal.$inject = [];

    angular.module('Fortscale.shared.filters')
        .filter('trancateDecimal', trancateDecimal);
}());
