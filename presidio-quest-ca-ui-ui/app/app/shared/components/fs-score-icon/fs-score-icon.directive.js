(function () {
    'use strict';

    function FsScoreIcon() {


        function linkFn(scope, element, attr, ctrl) {
            element.addClass(ctrl._getScoreRangeClass(ctrl.score));
            ctrl.score=_.round(ctrl.score, 1);
            if (ctrl.score) {
                element.parent().attr('title', 'score: ' + ctrl.score);
            }
        }

        function FsScoreIconController() {
        }

        _.merge(FsScoreIconController.prototype, {

            /**
             * Holds the color ranges. gte -> greater then or equal
             */
            _scoreRanges: [
                {
                    gte: 95,
                    className: 'fs-score-icon-red'
                },
                {
                    gte: 80,
                    className: 'fs-score-icon-orange'
                },
                {
                    gte: 50,
                    className: 'fs-score-icon-yellow'
                },
                {
                    gte: 1,
                    className: 'fs-score-icon-low'
                }
            ],

            /**
             * Takes a score, and iterates through _scoreRanges to find the first condition it
             * matches. Once a match is made, _scoreRanges.className is returned.
             * If no match is found, fs-score-icon-hide is returned
             *
             * @param {number} score
             * @returns {string}
             * @private
             */
            _getScoreRangeClass: function (score) {

                var scoreRangeClass = 'fs-score-icon-hide';
                if (_.isNil(score)){
                    return scoreRangeClass;
                }

                _.some(this._scoreRanges, function (scoreRange) {
                    if (score >= scoreRange.gte) {
                        scoreRangeClass = scoreRange.className;
                        return true;
                    }

                    return false;
                });

                return scoreRangeClass;
            }
        });

        FsScoreIconController.$inject = [];

        return {
            restrict: 'E',
            replace: true,
            template: '<div class="fs-score-icon" title="{{\'Score: \' + scoreIcon.score}}">' +
            '<i class="fa fa-square"></i></div>',
            link: linkFn,
            scope: {},
            controller: FsScoreIconController,
            controllerAs: 'scoreIcon',
            bindToController: {
                score: '='
            }
        };
    }

    FsScoreIcon.$inject = [];

    angular.module('Fortscale.shared.components.fsScoreIcon', [])
        .directive('fsScoreIcon', FsScoreIcon);
}());
