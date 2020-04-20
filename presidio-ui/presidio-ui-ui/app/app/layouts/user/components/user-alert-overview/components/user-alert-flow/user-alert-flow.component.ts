module Fortscale.layouts.user {

    import IIndicator = Fortscale.shared.interfaces.IIndicator;
    import IAlert = Forstcale.shared.interfaces.IAlert;
    declare var Swiper:any;

    let indicatorSlideTemplate = `
<div class="indicator-slide" ng-repeat="indicator in $ctrl.indicators"  ng-class="{'last-slide': $last}">
    <div class="upper-row">
        <div class="indicator-icon-container" ui-sref="user.indicator({alertId: $ctrl.alert.id, indicatorId: indicator.id})">
            <fs-svg-icon symbol-name="{{::indicator.symbolName}}"></fs-svg-icon>

        </div>
        <div class="slide-separator"></div>
    </div>
    <div class="slide-description" ui-sref="user.indicator({alertId: $ctrl.alert.id, indicatorId: indicator.id})">
        {{indicator.timeLineDescription}}
    </div>
    <div class="slide-date-time" ui-sref="user.indicator({alertId: $ctrl.alert.id, indicatorId: indicator.id})">
        {{indicator.startDate | date:\"MM/dd/yyyy | hh\\:mm a\":\"UTC\"}}
    </div>
</div>`;

    interface IIndicatorSlideBound {
        offsetLeft:number,
        width:number
    }

    interface IAlertFlowIndicator extends IIndicator {
        symbolName: string;
        timeLineDescription: string;
    }

    class UserAlertFlowController {

        _swiperElement:ng.IAugmentedJQuery;
        _swiperContainer:ng.IAugmentedJQuery;
        _swipperWrapperElement:ng.IAugmentedJQuery;
        _indicatorSlidesElement:HTMLElement[];
        _IndicatorSlidesBounds:IIndicatorSlideBound[];
        _swiperWrapperOffset:number = 0;

        currentIndex:number = 0;
        isLastIndicator:boolean;
        alert:IAlert;
        indicators: IAlertFlowIndicator[];

        SWIPER_CONTAINER_SELECTOR:string = '.swiper-container';
        SWIPER_WRAPPER_SELECTOR:string = '.swiper-wrapper';
        INDICATOR_SLIDER_SELECTOR:string = '.indicator-slide';
        WRAPPER_TRANSITION: string = 'transform 1s cubic-bezier(0.68, 0.1, 0.27, 0.98)';

        /**
         * Takes an index number and performs a slide animation to that index.
         * @param index
         * @param doNotExecute
         * @returns {IPromise<TResult>}
         */
        scrollToSlideIndex (index:number, doNotExecute?:boolean) {

            if (!!doNotExecute) {
                return;
            }

            if (index > this._getNumberOfIndicators() - 1) {
                index = this._getNumberOfIndicators() - 1;
            }

            if (index < 0) {
                index = 0;
            }

            // get slide offset.
            let offset = this._IndicatorSlidesBounds[index].offsetLeft;

            if ((this._getTotalIndicatorsWidth() > this._getSwiperContainer().width()) && (this._getTotalIndicatorsWidth() - offset) < this._getSwiperWrapper().parent().width()) {
                offset = this._getTotalIndicatorsWidth() - this._getSwiperWrapper().parent().width();
            }

            // set _swiperWrapperOffset.
            this._swiperWrapperOffset = offset;

            // set swiper offset
            return this._updateSwiperWrapperElementOffset()
                .then(() => {
                    return this._findCurrentIndex();
                })
                .then(currentIndex => {
                    this.currentIndex = currentIndex;
                    return this._findIfLastPossibleIndex();
                })
                .then((isLast:boolean) => {
                    this.isLastIndicator = isLast;
                });
        }

        _getNumberOfIndicators () {
            return this.indicators.length;
        }

        /**
         * Finds the current index of slides.
         * The heuristics: the first indicator that has both edges in the space
         * @returns {IPromise<TResult>}
         * @private
         */
        _findCurrentIndex ():ng.IPromise<number> {
            // iterate through slides and find which one has one side before and on side after the left container side
            return this._getIndicatorSlidesAsync()
                .then((slides:HTMLElement[]) => {

                    let containerBounds = this._getSwiperContainer()[0].getBoundingClientRect();
                    let currentIndex = this.currentIndex;

                    _.some(slides, (slide:HTMLElement, index:number) => {
                        let slideBoundingRect = slide.getBoundingClientRect();
                        if (slideBoundingRect.left >= containerBounds.left) {
                            currentIndex = index;
                            return true;
                        }
                    });

                    return currentIndex;
                });

        }

        /**
         * Returns a promise that resolves on a boolean that states if the current index is the right most index
         * (which means that scrolling to the next index will cause the inner scroll to move too far to the left)
         * The heuristics: If the last indicator's right is less than container's element right.
         * @private
         */
        _findIfLastPossibleIndex () {
            return this._getIndicatorSlidesAsync()
                .then((slides:HTMLElement[]) => {

                    let containerBounds = this._getSwiperContainer()[0].getBoundingClientRect();
                    let slideBoundingRect = slides[slides.length - 1].getBoundingClientRect();

                    return slideBoundingRect.right <= containerBounds.right;

                });
        }

        /**
         * Returns a promise. Animates the slider-wrapper (translate3d) and resolves the promise when animation has finished.
         * @returns {IPromise<T>}
         * @private
         */
        _updateSwiperWrapperElementOffset ():ng.IPromise<any> {
            let ctrl = this;

            return ctrl.$q((resolve, reject) => {

                function transitionEndHandler () {
                    ctrl._getSwiperWrapper()[0].removeEventListener('transitionend', transitionEndHandler);
                    resolve();
                }

                ctrl._getSwiperWrapper()[0].addEventListener('transitionend', transitionEndHandler);
                ctrl._getSwiperWrapper().css({'transform': `translate3d(${-this._swiperWrapperOffset}px, 0px, 0px)`});
            });
        }

        /**
         * Returns a sum of all indicators width
         * @returns {number}
         * @private
         */
        _getTotalIndicatorsWidth ():number {
            return _.sumBy(this._IndicatorSlidesBounds, indicatorSlideBound => indicatorSlideBound.width);
        }

        /**
         * Returns the swiper container element
         * @returns {ng.IAugmentedJQuery}
         * @private
         */
        _getSwiperContainer () {
            if (!this._swiperContainer) {
                this._swiperContainer = this.$element.find(this.SWIPER_CONTAINER_SELECTOR);
            }

            return this._swiperContainer;
        }

        /**
         * Sets the swiper wrapper element to _swiperWrapper (if needed) and returns it
         * @returns {ng.IAugmentedJQuery}
         * @private
         */
        _getSwiperWrapper ():ng.IAugmentedJQuery {
            if (!this._swipperWrapperElement) {
                this._swipperWrapperElement = this.$element.find(this.SWIPER_WRAPPER_SELECTOR);
            }

            return this._swipperWrapperElement;
        }

        /**
         * Return (Asynchronously) the list of indicator slides. It counts the amount of times it tries to find the
         * slides. It will try 10 times before throwing.
         * @param {number=} iteration
         * @returns {IPromise<any>}
         * @private
         */
        _getIndicatorSlidesAsync (iteration:number = 0):ng.IPromise<any> {

            return this.$q((resolve, reject) => {

                if (iteration > 10) {
                    reject('Could not find indicator slides even after 10 iterations.');
                }

                this.$scope.$applyAsync(() => {
                    if (!this._indicatorSlidesElement || !this._indicatorSlidesElement.length) {
                        this._indicatorSlidesElement =
                            <HTMLElement[]>Array.from(this._getSwiperWrapper().find(this.INDICATOR_SLIDER_SELECTOR));
                        if (this._indicatorSlidesElement.length) {
                            resolve(this._indicatorSlidesElement);
                        } else {
                            resolve(null);
                        }
                    } else {
                        resolve(this._indicatorSlidesElement);
                    }
                });
            })
                .then((indicatorSlidesElement:HTMLElement[]) => {
                    if (indicatorSlidesElement === null) {
                        iteration += 1;
                        return this._getIndicatorSlidesAsync(iteration);
                    } else {
                        return indicatorSlidesElement;
                    }
                })

        }

        /**
         * Renders the swiper element and appends it to its container element
         * @private
         */
        _renderSwiper () {
            this.$compile(indicatorSlideTemplate)(this.$scope, (clonedElement:ng.IAugmentedJQuery, scope) => {
                //cleanup (if not first time alert is rendered)
                this._getSwiperWrapper().empty();
                this._indicatorSlidesElement = null;

                // Create content
                this._swiperElement = clonedElement;
                this._getSwiperWrapper().append(this._swiperElement);
            });
        }

        /**
         * Iterates through all the slides and marks the bounds
         * @private
         */
        _markSlidesBounds () {
            return this._getIndicatorSlidesAsync()
                .then((slides:HTMLElement[]) => {
                    this._IndicatorSlidesBounds = _.map<HTMLElement, IIndicatorSlideBound>(slides, slide => {
                        return {
                            offsetLeft: slide.offsetLeft - slide.parentElement.offsetLeft,
                            width: slide.offsetWidth
                        };
                    });
                })
                .catch(err => {
                    throw err;
                })
        }

        /**
         * Takes the received alert, finds the indicators, and digests them.
         * @private
         */
        _digestAlert () {
            // Filter and sort indicators
            this.indicators = this.userIndicatorUtils.filterIndicators(this.alert.evidences);
            this.indicators = this.userIndicatorUtils.orderIndicators(this.indicators);
            // Duplicate for immutability
            this.indicators = _.cloneDeep(this.indicators);
            // Add symbolName (for svg icon) and time line description to each indicator
            _.each(this.indicators, (indicator: IAlertFlowIndicator) => {
                indicator.symbolName = this.userIndicatorUtils.getIndicatorSymbolName(indicator);
                indicator.timeLineDescription = this.userIndicatorUtils.getIndicatorTimelineDescription(indicator);
            });
        }

        _initAlertWatch () {
            this.$scope.$watch(
                () => this.alert,
                (alert:IAlert) => {
                    if (alert) {
                        this._digestAlert();
                        this._renderSwiper();
                        this._markSlidesBounds()
                            .then(() => {
                                return this._findCurrentIndex();
                            })
                            .then((currentIndex:number) => {
                                this.currentIndex = currentIndex;
                                return this._findIfLastPossibleIndex();
                            })
                            .then((isLastIndicator:boolean) => {
                                this.isLastIndicator = isLastIndicator;
                            });
                    }
                }
            )
        }


        _initContainerDragWatch () {

            let ctrl = this;
            let originalX: number;
            let xDelta:number = 0;
            let wrapperHasMoved: boolean = false;

            /**
             * Handler for mouse move
             * @param evt
             */
            function mousemoveHandler (evt: JQueryMouseEventObject) {
                // Raise move flag;
                wrapperHasMoved = true;

                // prevent movement when indicators width is less than the container
                if (ctrl._getTotalIndicatorsWidth() < ctrl._getSwiperContainer().width()) {
                    return;
                }

                // Calc movement delta
                xDelta = originalX - evt.pageX;

                // Move wrapper
                ctrl._getSwiperWrapper().css('transform', `translate3d(${-(ctrl._swiperWrapperOffset+xDelta)}px, 0px, 0px)`);
            }

            /**
             * Handler for mouse click up
             * @param evt
             */
            function mouseupHandler (evt: JQueryMouseEventObject) {
                // Remove handler for mouse move
                ctrl._getSwiperContainer().off('mousemove', mousemoveHandler);

                // Enter delta into current offset and zero delta
                ctrl._swiperWrapperOffset += xDelta;
                xDelta = 0;

                // Start sequence that will scroll back to first or last indicator if the wrapper
                // is too far to the left or right.
                ctrl._findIfLastPossibleIndex()
                    .then(isLast => {
                        ctrl.isLastIndicator = isLast;
                        return ctrl._findCurrentIndex();
                    })
                    .then(currentIndex => {

                        // put current index on the controller
                        ctrl.currentIndex = currentIndex;

                        // Change scroll time to 0.3 so the pull back will be fast
                        ctrl._getSwiperWrapper().css('transition', 'transform 0.3s ease-in-out');

                        // if current index is 0 than scroll back to first indicator
                        if (currentIndex === 0) {
                            return ctrl.scrollToSlideIndex(ctrl.currentIndex);

                            // if current index is not 0, and isLastIndicator is on, that means that the wrapper
                            // has been moved too far to the left and it needs to scroll back to last possible indicator
                        } else if (ctrl.isLastIndicator) {
                            return ctrl.scrollToSlideIndex(ctrl._getNumberOfIndicators() - 1);
                        }
                    })
                    .then(() => {
                        // Place the original transition back on the wrapper
                        ctrl._getSwiperWrapper().css('transition', ctrl.WRAPPER_TRANSITION);
                    })

            }

            /**
             * Handler for mouse down
             * @param evt
             */
            function mousedownHandler (evt: JQueryMouseEventObject) {
                // Set listener for mousemove
                ctrl._getSwiperContainer().on('mousemove', mousemoveHandler);

                // Find the original mouse location for future delta calculation
                originalX = evt.pageX;

                // Remove transition time so all transition (while in move) will be immediate.
                ctrl._getSwiperWrapper().css('transition', 'all 0s');
            }

            /**
             * Handler for mouse click. It was set on the container, on Capture-Phase. When wrapperHasMoved flag is on
             * (which means that move process was made on this particular mouse click sequence) then propagation
             * is stopped, so it will not fire the ui-sref change.
             * @param evt
             */
            function mouseClickHandler (evt: MouseEvent) {
                if (wrapperHasMoved) {
                    evt.stopImmediatePropagation();
                }

                // After flag was evaluated, put it down.
                wrapperHasMoved = false;
            }

            // Add up down and click listeners
            ctrl._getSwiperContainer().on('mousedown', mousedownHandler);
            ctrl._getSwiperContainer().on('mouseup', mouseupHandler);
            ctrl._getSwiperContainer()[0].addEventListener('click', mouseClickHandler, true);

            // Cleanup listeners on scope destroy
            ctrl.$scope.$on('$destory', () => {
                this._getSwiperContainer().off('mousedown', mousedownHandler);
                this._getSwiperContainer().off('mouseup', mouseupHandler);
                ctrl._getSwiperContainer()[0].removeEventListener('click', mouseClickHandler, true);

            });
        }


        $onInit () {

            this._initAlertWatch();

            this._initContainerDragWatch();


        }

        static $inject = ['$scope', '$element', '$compile', '$q', 'userIndicatorsUtils'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery, public $compile:ng.ICompileService,
            public $q:ng.IQService, public userIndicatorUtils:IUserIndicatorsUtilsService) {
        }
    }

    let fsUserAlertFlowComponent:ng.IComponentOptions = {
        controller: UserAlertFlowController,
        templateUrl: 'app/layouts/user/components/user-alert-overview/components/user-alert-flow/user-alert-flow.component.html',
        bindings: {
            alert: '<alertModel',
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('fsUserAlertFlow', fsUserAlertFlowComponent);
}
