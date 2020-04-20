module Fortscale.layouts.user {


    import IStateParamsService = angular.ui.IStateParamsService;
    import IComment = Fortscale.shared.services.alertUpdatesService.IComment;


    class RiskScoreController {

        _user:any;
        user:any;
        _alerts:any[];
        alerts:any[];
        activeAlertId:any;
        activeIndicatorId:any;
        USER_RISK_SCORE_LIST_SELECTOR_NAME:string = '.user-risk-score-list';
        ALERT_SELECTOR_PREFIX:string = '.alert-id-';
        INDICATOR_SELECTOR_PREFIX:string = '.indicator-id-';
        INDICATORS_LIST_SELECTOR:string = '.indicators-list-wrapper';
        INDICATORS_MASK_SELECTOR:string = '.indicators-list-mask';
        SCROLL_ANIMATION_TIME:number = 300;
        OPEN_INDICATOR_ANIMATION_TIME = 300;
        CLOSE_INDICATOR_ANIMATION_TIME = 300;

        _userRiskScoreElement:JQuery;
        _alertElement: JQuery;
        _indicatorsListElement: JQuery;
        _indicatorElement: JQuery;
        _indicatorsMaskElement: JQuery;

        alertSortBys = [
            {
                text: 'Severity',
                value: 'severity'
            },
            {
                text: 'Date',
                value: 'date'
            }
        ];

        selectedAlertSortBy:any;

        /**
         * Returns the risk score element
         * @returns {ng.IAugmentedJQuery}
         * @private
         */
        _getUserRiskScoreElement ():JQuery {
            if (!this._userRiskScoreElement) {
                this._userRiskScoreElement = this.$element.find(this.USER_RISK_SCORE_LIST_SELECTOR_NAME);
            }

            return this._userRiskScoreElement;
        }

        /**
         * Returns an alert element
         * @param alertId
         * @returns {any}
         * @private
         */
        _getAlertElement (alertId?: string):JQuery {

            // return a specific alert element (not the active one)
            if (alertId) {
                return this.$element.find(this.ALERT_SELECTOR_PREFIX + alertId);
            }

            if (!this.activeAlertId) {
                return null;
            }

            if (!this._alertElement) {
                this._alertElement = this.$element.find(this.ALERT_SELECTOR_PREFIX + this.activeAlertId);
            }

            return this._alertElement;
        }

        /**
         * Returns an indicators list element
         * @returns {JQuery}
         * @private
         */
        _getIndicatorsListElement ():JQuery {
            if (!this._getAlertElement()) {
                return null;
            }

            if (!this._indicatorsListElement) {
                this._indicatorsListElement = this._getAlertElement().find(this.INDICATORS_LIST_SELECTOR);
            }
            return this._indicatorsListElement;
        }

        _getIndicatorElement ():JQuery {

            if (!this._getIndicatorsListElement() || !this.activeIndicatorId) {
                return null;
            }

            if (!this._indicatorElement) {
                this._indicatorElement = this._getIndicatorsListElement().find(this.INDICATOR_SELECTOR_PREFIX + this.activeIndicatorId);
            }
            return this._indicatorElement;
        }

        /**
         * Returns an indicators list mask element
         * @param alertElement
         * @returns {any}
         * @private
         */
        _getIndicatorsMaskElement (alertElement? :JQuery):JQuery {

            // return a specific mask element and not the active alert element
            if (alertElement) {
                return alertElement.find(this.INDICATORS_MASK_SELECTOR);
            }

            if (!this._getAlertElement()) {
                return null;
            }

            if (!this._indicatorsMaskElement) {
                this._indicatorsMaskElement = this._getAlertElement().find(this.INDICATORS_MASK_SELECTOR);
            }

            return this._indicatorsMaskElement;
        }


        /**
         * Calculates an offset between two element
         * @param targetElement
         * @param containerElement
         * @returns {number}
         * @private
         */
        _calcTopOffsetInContainer (targetElement:JQuery, containerElement:JQuery):number {
            var childPos = targetElement.offset();
            var parentPos = containerElement.offset();
            return childPos.top - parentPos.top;
        }

        _isIndicatorElementOutOfBounds (): boolean {
            let userRiskElement = this._getUserRiskScoreElement();
            let indicatorElement = this._getIndicatorElement();
            let offset = this._calcTopOffsetInContainer(indicatorElement, userRiskElement);

            return (offset < 0 || offset + indicatorElement.outerHeight() > userRiskElement.outerHeight());
        }


        /**
         * Animates indicators open.
         * @private
         */
        _animateOpenIndicators ():ng.IPromise<any> {

            return this.$q((resolve, reject) => {
                // Get alert element and stop animation if alert does not exist
                let alertElement = this._getAlertElement();
                if (!alertElement) {
                    return;
                }

                // Get Elements
                let indicatorsListElement = this._getIndicatorsListElement();
                let indicatorsMaskElement = this._getIndicatorsMaskElement();

                // Do not open if mask element already open
                if (indicatorsMaskElement.outerHeight() > 0) {
                    return resolve();
                }

                //calc height
                let height = indicatorsListElement.outerHeight();
                // animate height
                indicatorsMaskElement.animate({height: height}, this.OPEN_INDICATOR_ANIMATION_TIME, () => {
                    resolve();
                });
            });
        }

        /**
         * Animates indicators list close
         * @param alertId
         * @private
         */
        _animateCloseIndicators (alertId) {
            let alertElement = this._getAlertElement(alertId);
            let indicatorsMaskElement = this._getIndicatorsMaskElement(alertElement);

            indicatorsMaskElement.animate({height: 0}, this.CLOSE_INDICATOR_ANIMATION_TIME);
        }

        /**
         * Animates a scroll to alert in the list
         * @returns {IPromise<void>}
         * @private
         */
        _animateActiveAlert (): ng.IPromise<any> {
            return this.$q((resolve, reject) => {
                let userRiskElement = this._getUserRiskScoreElement();
                let alertElement = this._getAlertElement();
                userRiskElement.animate(
                    {scrollTop: this._calcTopOffsetInContainer(alertElement, userRiskElement)},
                    this.SCROLL_ANIMATION_TIME, () => {
                        resolve();
                    });
            });

        }

        /**
         * Animates an active indicator
         * @param cb
         * @returns {IPromise<T>}
         * @private
         */
        _animateActiveIndicator ():ng.IPromise<any> {
            return this.$q((resolve, reject) => {
                let userRiskElement = this._getUserRiskScoreElement();
                let alertElement = this._getAlertElement();
                let indicatorsListElement = this._getIndicatorsListElement();
                let indicatorElement = this._getIndicatorElement();
                let offset;

                //find offset of indicator item in indicators list
                let indicatorInIndicatorsListOffset = this._calcTopOffsetInContainer(indicatorElement,
                    indicatorsListElement);

                // if the indicator top plus height plus alert element height fits in the user risk element
                // then scroll to alert
                let indicatorBottom = indicatorInIndicatorsListOffset + indicatorElement.outerHeight() +
                    alertElement.find('.alert-item-inner-wrapper').outerHeight();
                if (indicatorBottom <= userRiskElement.outerHeight()) {
                    offset = this._calcTopOffsetInContainer(alertElement, userRiskElement);
                } else {
                    // If the indicator bottom is greater then risk element height then scroll to indicator element
                    offset = this._calcTopOffsetInContainer(indicatorElement, userRiskElement);
                }

                userRiskElement.animate(
                    {scrollTop: offset + userRiskElement.scrollTop()},
                    this.SCROLL_ANIMATION_TIME, () => {
                        resolve();
                    });
            });
        }

        /**
         * Renders the sort dropdown control
         * @private
         */
        _renderSortControlDropdown () {
            let ctrl = this;

            function onChange (evt:any) {
                ctrl.selectedAlertSortBy = evt.sender.dataItem();
                ctrl.$scope.$applyAsync(() => {
                    ctrl._orderAlerts();
                });
            }

            //Create the drop down
            (<any>$(".sort-drop-down")).kendoDropDownList({
                dataTextField: "text",
                dataValueField: "value",
                dataSource: ctrl.alertSortBys,
                index: 0,
                change: onChange
            });

            //Select the first element
            ctrl.selectedAlertSortBy = this.alertSortBys[0];

            ctrl._orderAlerts();

        }

        /**
         * Initiates a user watch. On new user, clones the user and places on controller
         * @private
         */
        _initUserWatch () {
            let userWatchUnregister;
            this.$scope.$watch(
                () => this._user,
                (user) => {
                    if (user) {
                        this.user = _.cloneDeep(this._user);
                        this._setActiveIds();
                    }
                }
            );
        }

        /**
         * Initates an alerts watch. On new alerts, clones, orders, and animates to the relevant alert.
         * @private
         */
        _initAlertsWatch () {
            this.$scope.$watch(
                () => this._alerts,
                (alerts) => {
                    if (alerts) {
                        this.alerts = _.cloneDeep(this._alerts);
                        this._orderAlerts();
                        this._orderIndicators();
                        this._filterIndicators();
                        this._emitAlertsChange();
                        this._setActiveIds();

                        // Scroll to alert if on alert state
                        if (this.$stateParams.alertId /*&& !this.$stateParams.indicatorId*/) {
                            this.$scope.$applyAsync(() => {
                                this._animateActiveAlert()
                                    .then(() => {
                                        this._animateOpenIndicators();
                                    });
                            });
                        }

                        // Scroll to indicator if on indicator state
                        if (this.$stateParams.alertId && this.$stateParams.indicatorId) {
                            this.$scope.$applyAsync(() => {
                                this._animateOpenIndicators()
                                    .then(() => {
                                        this._animateActiveIndicator();
                                    });
                            });
                        }
                    }
                }
            );
        }

        /**
         * Listener for state changes start. On a change start, sets activeAlertId, and animates.
         * @private
         */
        _initStateChangeWatch () {
            let ctrl = this;

            this.$scope.$on('$stateChangeSuccess',
                (evt:any, toState:any, toParams:{alertId:string, userId:string}, fromState:any,
                    fromParams:{alertId:string, userId:string}) => {
                    this._setActiveIds();
                    this.$scope.$applyAsync(() => {

                        if (this.$stateParams.alertId /*&& this.$stateParams.indicatorId*/) {
                            this._animateOpenIndicators()
                                .then(() => {
                                    if (this.$stateParams.indicatorId && this._isIndicatorElementOutOfBounds()) {
                                        this._animateActiveIndicator();
                                    }
                                });
                        }


                        if (fromParams.alertId && toParams.alertId !== fromParams.alertId) {
                            this._animateCloseIndicators(fromParams.alertId);
                        }

                    });
                });
        }

        _emitAlertsChange () {
            this.$scope.$applyAsync(() => {
                this.$scope.$emit('userRiskScore:AlertsSorted', this.alerts);
            });
        }

        /**
         * Orders alerts and returns an ordered list
         * @returns {any}
         * @private
         */
        _orderAlerts ():void {
            if (!this.alerts || !this.selectedAlertSortBy) {
                return;
            }

            switch (this.selectedAlertSortBy.value) {
                case 'severity':
                    this.alerts = _.clone(
                        _.orderBy(this.alerts, ['userScoreContributionFlag', 'severityCode', 'startDate'],
                            ['desc', 'asc', 'desc']));
                    this._emitAlertsChange();
                    break;
                case 'date':
                    this.alerts = _.clone(
                        _.orderBy(this.alerts, ['userScoreContributionFlag', 'startDate', 'severityCode'],
                            ['desc', 'desc', 'asc']));
                    this._emitAlertsChange();
                    break;
                default:
                    return;
            }
        }

        _orderIndicators ():void {
            if (!this.alerts) {
                return;
            }

            _.each(this.alerts, (alert:any) => {
                alert.evidences = this.userIndicatorsUtils.orderIndicators(alert.evidences);
            });
        }

        _filterIndicators ():void {
            if (!this.alerts) {
                return;
            }

            _.each(this.alerts, (alert:any) => {
                // filter out 'tag' indicators and save the filtered indicators on tagEvidence attribute
                alert.tagEvidences = this.userIndicatorsUtils.getTagsIndicators(alert.evidences);
                alert.evidences =
                    this.userIndicatorsUtils.filterIndicators(alert.evidences);
            });
        }

        _setActiveIds () {
            this.activeAlertId = this.$stateParams.alertId || null;
            this.activeIndicatorId = this.$stateParams.indicatorId || null;

            // nullify elements
            this._userRiskScoreElement = null;
            this._alertElement = null;
            this._indicatorsListElement = null;
            this._indicatorElement = null;
            this._indicatorsMaskElement = null;

        }



        $onInit () {
            this._initUserWatch();
            this._initAlertsWatch();
            this._initStateChangeWatch();
            this._setActiveIds();

            this._renderSortControlDropdown();
        }

        static $inject = ['$scope', '$timeout', '$stateParams', '$element', '$q', 'userIndicatorsUtils'];

        constructor (public $scope:ng.IScope, public $timeout:ng.ITimeoutService,
            public $stateParams:any, public $element:ng.IAugmentedJQuery, public $q:ng.IQService, public userIndicatorsUtils: IUserIndicatorsUtilsService) {
        }
    }

    let riskScoreComponent:ng.IComponentOptions = {
        controller: RiskScoreController,
        templateUrl: 'app/layouts/user/components/user-risk-score/user-risk-score.component.html',
        bindings: {
            _alerts: '<alerts',
            _user: '<user',
        }
    };
    angular.module('Fortscale.layouts.user')
        .component('userRiskScore', riskScoreComponent);
}
