// requestAnimationFrame polyfill by Erik MÃ¶ller. fixes from Paul Irish and Tino Zijdel
(function (window, document) {
    'use strict';


    /**
     * http://paulirish.com/2011/requestanimationframe-for-smart-animating/
     * http://my.opera.com/emoller/blog/2011/12/20/requestanimationframe-for-smart-er-animating
     *
     * requestAnimationFrame polyfill by Erik Möller. fixes from Paul Irish and Tino Zijdel
     *
     * MIT license
     */
    var lastTime = 0;
    var vendors = ['ms', 'moz', 'webkit', 'o'];
    for (var x = 0; x < vendors.length && !window.requestAnimationFrame; ++x) {
        window.requestAnimationFrame = window[vendors[x] + 'RequestAnimationFrame'];
        window.cancelAnimationFrame =
            window[vendors[x] + 'CancelAnimationFrame'] || window[vendors[x] + 'CancelRequestAnimationFrame'];
    }

    if (!window.requestAnimationFrame) {
        window.requestAnimationFrame = function (callback) {
            var currTime = new Date().getTime();
            var timeToCall = Math.max(0, 16 - (currTime - lastTime));
            var id = window.setTimeout(function () {
                    callback(currTime + timeToCall);
                },
                timeToCall);
            lastTime = currTime + timeToCall;
            return id;
        };
    }

    if (!window.cancelAnimationFrame) {
        window.cancelAnimationFrame = function (id) {
            clearTimeout(id);
        };
    }


    /**
     * UI.Layout
     */
    angular.module('ui.layout', [])
        .controller('uiLayoutCtrl', ['$scope', '$attrs', '$element', function uiLayoutCtrl($scope, $attrs, $element) {
            // Gives to the children directives the access to the parent layout.
            return {
                opts: angular.extend({}, $scope.$eval($attrs.uiLayout), $scope.$eval($attrs.options)),
                element: $element
            };
        }])

        .directive('uiLayout', ['$parse', '$timeout', '$compile', function ($parse, $timeout, $compile) {

            var splitBarElem_htmlTemplate = '<div class="stretch ui-splitbar"><i class="icon-ellipsis-horizontal"></i></div>';

            return {
                restrict: 'AE',
                link: function compile(scope, tElement, tAttrs) {
                    var childrenCount = tElement.children().length,
                        setLayoutTimeout,
                        settingLayout;

                    observeAdditions(tElement[0]);

                    function setLayout(){
                        var _i, _childens = tElement.children(), _child_len = _childens.length;
                        // Parse `ui-layout` or `options` attributes (with no scope...)
                        var opts = angular.extend({}, $parse(tAttrs.uiLayout)(), $parse(tAttrs.options)());
                        var isUsingColumnFlow = opts.flow === 'column';

                        tElement
                            // Force the layout to fill the parent space
                            // fix no height layout...
                            .addClass('stretch')
                            // set the layout css class
                            .addClass('ui-layout-' + (opts.flow || 'row'));

                        var childElement;
                        // Stretch all the children by default
                        for (_i = 0; _i < _child_len; ++_i) {
                            childElement = angular.element(_childens[_i]);
                            childElement.addClass('stretch');
                            if (_i)
                                childElement.addClass('stretch-after-split');
                        }

                        if (_child_len > 1) {
                            // Initialise the layout with equal sizes.

                            var flowProperty = ( isUsingColumnFlow ? 'left' : 'top');
                            var oppositeFlowProperty = ( isUsingColumnFlow ? 'right' : 'bottom');

                            var step = 100 / _child_len;
                            for (_i = 0; _i < _child_len; ++_i) {
                                var area = angular.element(_childens[_i])
                                    .css(flowProperty, (step * _i) + '%')
                                    .css(oppositeFlowProperty, (100 - step * (_i + 1)) + '%');

                                if (_i < _child_len - 1) {
                                    // Add a split bar
                                    var bar = angular.element(splitBarElem_htmlTemplate).css(flowProperty, step * (_i + 1) + '%');
                                    area.after(bar);
                                    $compile(bar)(scope);
                                }
                            }
                        }

                        scope.$broadcast("resize");
                        settingLayout = false;
                    }

                    function observeAdditions(target) {
                        var observer = new MutationObserver(function (mutations) {
                            mutations.forEach(function (mutation) {
                                var shouldSetLayout = false;

                                if (mutation.removedNodes && childrenCount > 1 && !tElement.find(".ui-splitbar").length)
                                    shouldSetLayout = true;
                                else if (mutation.addedNodes && mutation.addedNodes.length){
                                    for(var i= 0, addedNode; i < mutation.addedNodes.length && !shouldSetLayout; i++){
                                        addedNode = mutation.addedNodes[i];
                                        if (addedNode.parentNode === tElement[0] && addedNode.nodeType === 1 && !addedNode.classList.contains("stretch") && childrenCount !== tElement.children().length)
                                            shouldSetLayout = true;
                                    }
                                }

                                if (shouldSetLayout){
                                    childrenCount = tElement.children().length;
                                    $timeout.cancel(setLayoutTimeout);

                                    if (!settingLayout && tElement.children().length){
                                        settingLayout = true;
                                        $timeout(setLayout, 1000);
                                    }
                                }

                            });
                        });

                        var config = { childList: true };

                        observer.observe(target, config);

                        //observer.disconnect();
                        return observer;
                    }
                },
                controller: 'uiLayoutCtrl'
            };

        }])


        .directive('uiSplitbar', function () {

            // Get all the page.
            var htmlElement = angular.element(document.body.parentElement);

            return {
                require: '^uiLayout',
                restrict: 'EAC',
                link: function (scope, iElement, iAttrs, parentLayout) {
                    var animationFrameRequested, lastX;
                    var _cache = {};

                    // Use relative mouse position
                    var isUsingColumnFlow = parentLayout.opts.flow === 'column';
                    var mouseProperty = ( isUsingColumnFlow ? 'pageX' : 'pageY');

                    // Use bounding box / css property names
                    var flowProperty = ( isUsingColumnFlow ? 'left' : 'top');
                    var oppositeFlowProperty = ( isUsingColumnFlow ? 'right' : 'bottom');
                    var sizeProperty = ( isUsingColumnFlow ? 'width' : 'height');

                    // Use bounding box properties
                    var barElm = iElement[0];


                    // Stores the layout values for some seconds to not recalculate it during the animation
                    function _cached_layout_values() {
                        // layout bounding box
                        var layout_bb = parentLayout.element[0].getBoundingClientRect();

                        // split bar bounding box
                        var bar_bb = barElm.getBoundingClientRect();

                        _cache.time = +new Date();
                        _cache.barSize = bar_bb[sizeProperty];
                        _cache.layoutSize = layout_bb[sizeProperty];
                        _cache.layoutOrigine = layout_bb[flowProperty];
                    }

                    function _draw() {
                        var the_pos = (lastX - _cache.layoutOrigine) / _cache.layoutSize * 100;

                        // Keep the bar in the window (no left/top 100%)
                        the_pos = Math.min(the_pos, 100 - _cache.barSize / _cache.layoutSize * 100);

                        // The the bar in the near beetween the near by area
                        the_pos = Math.max(the_pos, parseInt(barElm.previousElementSibling.style[flowProperty], 10));
                        if (barElm.nextElementSibling.nextElementSibling) {
                            the_pos = Math.min(the_pos, parseInt(barElm.nextElementSibling.nextElementSibling.style[flowProperty], 10));
                        }

                        // change the position of the bar and the next area
                        barElm.style[flowProperty] = barElm.nextElementSibling.style[flowProperty] = the_pos + '%';
                        // change the size of the previous area
                        barElm.previousElementSibling.style[oppositeFlowProperty] = (100 - the_pos) + '%';

                        // Enable a new animation frame
                        animationFrameRequested = null;
                        scope.$broadcast("resize");
                    }

                    function _resize(mouseEvent) {
                        // Store the mouse position for later
                        lastX = mouseEvent[mouseProperty];

                        // Cancel previous rAF call
                        if (animationFrameRequested) {
                            window.cancelAnimationFrame(animationFrameRequested);
                        }

                        if (!_cache.time || +new Date() > _cache.time + 1000) { // after ~60 frames
                            _cached_layout_values();
                        }

                        // Animate the page outside the event
                        animationFrameRequested = window.requestAnimationFrame(_draw);
                    }


                    // Bind the click on the bar then you can move it all over the page.
                    iElement.bind('mousedown', function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        htmlElement.bind('mousemove', _resize);
                        return false;
                    });
                    htmlElement.bind('mouseup', function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        htmlElement.unbind('mousemove');
                        return false;
                    });
                }
            };
        });


}(window, document));
