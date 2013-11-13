angular.module("Fortscale").directive("hideOnError", [function(){
    return {
        restrict: 'A',
        link: function postLink(scope, element, attrs) {
            element.on("error", function(event){
                element.css("display", "none");
            });
        }
    };
}]);