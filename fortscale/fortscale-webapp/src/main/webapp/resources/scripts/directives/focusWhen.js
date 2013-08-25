angular.module("Fortscale").directive("focusWhen", ["$parse", function($parse){
    return {
        restrict: 'A',
        link: function postLink(scope, element, attrs) {
            scope.$watch(attrs.focusWhen, function(value){
                if (value){
                    setTimeout(function(){
                        element.focus().select();
                    }, 40);
                }
                else
                    element.blur();
            });
        }
    };
}]);
