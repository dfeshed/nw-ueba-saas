module Fortscale.shared.components.fsUserTooltip {

    declare var Opentip:any;

    const TEMPLATE_URL = 'app/shared/components/fs-user-devices-tooltip/fs-user-devices-tooltip-internal.template.html';


    function fsUserDevicesTooltipDirective ($compile:ng.ICompileService,
        $templateCache:ng.ITemplateCacheService):ng.IDirective {

        class UserDevicesTooltipController {

            static $inject = ['$scope', '$element'];

            constructor (public $scope:ng.IScope, public $element:JQuery) {

            }

            /**
             * The alerts list to be displayed in the tooltip
             */
            devices:any[];

            /**
             * A string that holds a selector for the tooltip target
             */
            tooltipTargetSelector:string;

            /**
             * Received tooltip settings
             */
            _externalTooltipSettings:any;

            /**
             * Generated tooltip object
             */
            _tooltip:any;

            /**
             * Tooltip local settings object
             *
             * @type {{className: string, stem: boolean, stemLength: number, stemBase: number, hideDelay: number, tipJoint: string, fixed: boolean, removeElementsOnHide: boolean, group: string, background: string, borderRadius: number, borderColor: string, shadow: boolean, shadowBlur: number, shadowOffset: number[], shadowColor: string, containInViewport: boolean, offset: number[]}}
             * @private
             */
            _tooltipSettings:{} = {
                className: 'user-tooltip',
                stem: true,
                stemLength: 8,
                stemBase: 12,
                hideDelay: 0.3,
                tipJoint: 'top',
                fixed: true,
                removeElementsOnHide: false,
                group: 'users',
                background: '#f0f7f8',
                borderRadius: 3,
                borderColor: '#f0f7f8',
                shadow: true,
                shadowBlur: 15,
                shadowOffset: [0, 0],
                shadowColor: 'rgba(0, 0, 0, 0.5)',
                containInViewport: true,
                offset: [0, 0]
            };

            /**
             * If tooltipTargetSelector was provided, this method returns the element the selector refers to.
             *
             * @returns {HTMLElement|null}
             * @private
             */
            _getTargetElement ():HTMLElement {
                if (this.tooltipTargetSelector) {
                    let closest = this.$element.closest(this.tooltipTargetSelector);
                    let inner = this.$element.find(this.tooltipTargetSelector);
                    return closest.length ? closest[0] :
                        inner.length ? inner[0] : null;
                }
                return null;
            }

            /**
             * Initiates the tooltip settings (creates an instance settings)
             *
             * @private
             */
            _initSettings ():void {
                this._tooltipSettings = _.merge(
                    {},
                    this._tooltipSettings,
                    {
                        target: this._getTargetElement(),
                    },
                    this._externalTooltipSettings ? this._externalTooltipSettings : {});
            }

            /**
             * Renders the tooltip
             *
             * @private
             */
            _initTooltip ():void {
                // Set this as ctrl for the callbacks
                let ctrl = this;

                let template = $templateCache.get(TEMPLATE_URL);
                let tooltipContent = $compile(angular.element(template))(ctrl.$scope);
                ctrl._tooltip = new Opentip(ctrl.$element, ctrl._tooltipSettings);
                ctrl._tooltip.setContent(tooltipContent);

                // Prevent close on mouseover tooltip
                ctrl._tooltip.content.on({
                    mouseenter: function () {
                        ctrl._tooltip._abortHiding();
                    },
                    mouseleave: function () {
                        ctrl._tooltip.prepareToHide();
                    }
                });
            }

            /**
             * Cleansup the tooltip when the scope is destroyed
             *
             * @private
             */
            _initCleanup ():void {
                this.$scope.$on('$destroy', () => {
                    if (this._tooltip != null){
                        this._tooltip.adapter.remove(this._tooltip.container);
                        this._tooltip.container = null;
                        this._tooltip.tooltipElement = null;
                        this._tooltip = null;
                    }
                });
            }

            showTooltip () : void{
                this._initTooltip();
                this._initCleanup();
            }


            $onInit ():void {
                this.tooltipTargetSelector="user-devices-for-tooltip";
                this._initSettings();

            }

        }


        return {
            restrict: 'A',
            controller: UserDevicesTooltipController,
            controllerAs: '$ctrl',
            scope: {},
            transclude: true,
            template: '<div class="user-devices-for-tooltip" ng-mouseover="$ctrl.showTooltip()"><ng-transclude></ng-transclude></a>',
            bindToController: {
                devices: '<',
                _externalTooltipSettings: '<tooltipSettings'
            }
        }
    }

    fsUserDevicesTooltipDirective.$inject = ['$compile', '$templateCache'];

    angular.module('Fortscale.shared.components')
        .directive('fsUserDevicesTooltip', fsUserDevicesTooltipDirective);
}
