module Fortscale.shared.components.fsUserTooltip {

    declare var Opentip:any;
    // import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;
    // import UserFilter = Fortscale.layouts.users.UserFilter;

    const TEMPLATE_URL = 'app/shared/components/fs-text-tooltip/fs-text-tooltip.template.html';
    // const STATE_ID:string = "users-page";
    // const USERS_PAGE_PREFIX:string = "/users"

    function fsTextTooltipDirective ($compile:ng.ICompileService,
        $templateCache:ng.ITemplateCacheService):ng.IDirective {

        class TextTooltipController {

            static $inject = ['$scope', '$element','stateManagementService','$location','$filter'];

            constructor (public $scope:ng.IScope, public $element:JQuery,
                         public stateManagementService:IStateManagementService,public $location:ng.ILocationService,
                            public $filter:any) {

            }

            /**
             * The user object to be displayed in the tooltip
             */
            text:string;

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
                    this._tooltip.adapter.remove(this._tooltip.container);
                    this._tooltip.container = null;
                    this._tooltip.tooltipElement = null;
                    this._tooltip = null;
                });
            }

            buildTranslateObject(key:string, value:string){
                let translateObject:any = {};
                translateObject[key]=value;
                return translateObject;
            }

            $onInit ():void {
                this._initSettings();
                this._initTooltip();
                this._initCleanup();
            }

        }


        return {
            restrict: 'A',
            controller: TextTooltipController,
            controllerAs: '$ctrl',
            scope: {},
            bindToController: {
                text: '<fsTextTooltip',

            }
        }
    }

    fsTextTooltipDirective.$inject = ['$compile', '$templateCache'];

    angular.module('Fortscale.shared.components')
        .directive('fsTextTooltip', fsTextTooltipDirective);
}
