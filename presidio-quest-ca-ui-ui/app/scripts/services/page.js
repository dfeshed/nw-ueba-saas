(function(){
    'use strict';

    angular.module("Page",[]).factory("page", ["FORTSCALE_BRAND_UI",function(FORTSCALE_BRAND_UI){
        return {
            setPageTitle: function(title) {
                if (FORTSCALE_BRAND_UI) {
                    document.title = "Fortscale - " + title;
                } else {
                    document.title = "Threat Detection - " + title;
                }

            }
        };
    }]);
})();
