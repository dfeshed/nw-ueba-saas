module Fortscale.shared.components.fsPercentageCircle {
    class PercentageCircleController {

        percentage:number;
        radius: number;

        $onInit () {
            let ctrl:PercentageCircleController = this;
            this.$element.ready(() => {
                let el:ng.IAugmentedJQuery = <ng.IAugmentedJQuery>ctrl.$element.find('.fs-percentage-circle');
                // let s:string =  '<div style="width: 38px;height: 38px;><svg viewBox="0 0 38 38">';
                // s+="<circle cx='12.5' cy='12.5' r='10' fill='#016a9c'></circle>";
                // s+="</svg></div>";
                // el.add(s);
                // ctrl.percentage=80;

                let boxSize = ctrl.radius*2+4;
                let WRAPPER_TEMPLATE = '<div title="Indicator Contribution" style="width: <%= boxSize %>px;height: <%= boxSize %>px;"><div></div>';
                let wrapper:ng.IAugmentedJQuery  =$(_.template(WRAPPER_TEMPLATE)({boxSize: ctrl.radius*2+4}));
                el.append(wrapper);

                let circumference:number = ctrl.radius*2 * 3.14159;
                let circumferencePercentage = circumference * ctrl.percentage/100;

                let params:any={
                 'radius':ctrl.radius,
                  'diameter':ctrl.radius*2,
                  'circumference':circumference,
                  'circumferencePercentage': circumferencePercentage,
                  'boxSize' : boxSize,
                  'textX' :  parseInt(ctrl.radius) + parseInt(2),
                  'textY' : ctrl.radius*2/3,
                  'percent' : ctrl.percentage,
                  'color': '#fff',
                   'fontSize': 8,
                    'circleCenter': parseInt(ctrl.radius)+parseInt(1)
                };


                let templateString: string = '<svg viewBox="0 0 <%= boxSize %> <%= boxSize %>">' +
                     // '<circle cx="19" cy="17" r="15" fill="#ccf7ff"></circle>'+
                    '<path d="M<%= circleCenter %> 1       a <%= radius %> <%= radius %> 0 0 1 0 <%= diameter %>       a <%= radius %> <%= radius %> 0 0 1 0 -<%= diameter %> " ' +
                    'fill="none" stroke="<%= color %>" stroke-width="3" stroke-dasharray="<%= circumferencePercentage %>,<%= circumference %>"></path>' +
                    '<text x="<%= textX %>" y="<%= textY %>" text-anchor="middle" dy="7" font-size=" <%= fontSize %>" fill="<%= color %>" > <%= percent %>%</text>'+
                    '</svg>'

                let template = _.template(templateString)(params);
                var svgElement = $(template);
                wrapper.append(svgElement);

            });
        }

        static $inject = ['$scope', '$element'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery){
        }
    }


    let fsPercentageCircleComponent: ng.IComponentOptions = {
        controller: PercentageCircleController,
        templateUrl: 'app/shared/components/fs-percentage-circle/fs-percentage-circle.component.html',
        bindings: {
            radius: '@',
            percentage: '@'
        }
    };

    angular.module('Fortscale.shared.components.fsPercentageCircle', [])
        .component('fsPercentageCircle', fsPercentageCircleComponent);

}
