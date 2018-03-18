module Fortscale.shared.components.fsNanobar {

    import INanobarAutomationService = Fortscale.shared.services.fsNanobarAutomation.INanobarAutomationService;
    export interface INanobarOptions {
        classname?:string,
        id?:string,
        target?:HTMLElement,
        targetGlobal?:boolean,
        color?:string
    }

    declare var Nanobar:any;

    class NanobarController {

        ERR_MSG:string = 'fsNanobarDirective: fsNanobarController: ';
        nanobarProgress:number;
        nanobarOptions:INanobarOptions;
        nanobar:any;
        nanobarId:string;

        private _validateId () {
            this.assert.isString(this.nanobarId, `${this.ERR_MSG}Directive must receive a nanobar Id.`);
        }

        private _validateOptions () {
            if (this.nanobarOptions) {
                this.assert.isString(this.nanobarOptions.classname, 'nanobarOptions.classname', this.ERR_MSG, true);
                this.assert.isString(this.nanobarOptions.id, 'nanobarOptions.id', this.ERR_MSG, true);
                this.assert.isString(this.nanobarOptions.color, 'nanobarOptions.color', this.ERR_MSG, true);
            }
        }

        /**
         * Validates 'progress' argument.
         * @private
         */
        private _validateProgress ():void {
            this.assert(_.isNumber(this.nanobarProgress), `${this.ERR_MSG}progress argument must be a number`,
                TypeError);
            this.assert(this.nanobarProgress >= 0,
                `${this.ERR_MSG}progress argument must be => 0. its ${this.nanobarProgress}`,
                RangeError);
            this.assert(this.nanobarProgress <= 100,
                `${this.ERR_MSG}progress argument must be <= 100. its ${this.nanobarProgress}`,
                RangeError);
        }

        /**
         * Updates progress to nanobar
         * @private
         */
        private _updateProgress () {
            if (this.nanobar) {
                $(this.nanobar.el).find('.bar').css({backgroundColor: this.nanobarOptions.color || '#000000'});
                this.nanobar.go(this.nanobarProgress);
            }
        }


        /**
         * Handler for 'progress' change
         * @private
         */
        private _progressHandler () {
            if (this.nanobarProgress === null || this.nanobarProgress === undefined) {
                return;
            }

            // validate progress
            this._validateProgress();
            this._updateProgress();

            if (this.nanobarProgress === 100) {
                this.$timeout(() => {
                    if (this.nanobarProgress === 100) {
                        this.fsNanobarAutomationService.reset(this.nanobarId);
                    }
                }, 1000);
            }
        }

        /**
         * Creates a nanobar options. target is taken from the provided options or from the element
         * @returns {INanobarOptions}
         * @private
         */
        private _createNanobarOptions ():INanobarOptions {
            if (this.nanobarOptions) {

                // Copy options
                let options = _.merge<INanobarOptions>({}, this.nanobarOptions);

                // decide on target element. If not targetGlobal then target is either explicit or parent of element
                if (!this.nanobarOptions.targetGlobal) {
                    options.target = (this.nanobarOptions || {}).target || this.$element.parent()[0];
                }

                return options;
            }
        }

        /**
         * Set the bar color if received in options
         * @private
         */
        private _setBarColor () {
            // Set color
            if (this.nanobarOptions && this.nanobarOptions.color) {
                $(this.nanobar.el).find('.bar').css('background-color', this.nanobarOptions.color);
            }
        }

        /**
         * Creates a nanobar instance
         */
        renderNanobar () {
            let nanobarOptions = this._createNanobarOptions();
            this.nanobar = new Nanobar(nanobarOptions);
            this._setBarColor();
        }

        updateProgress (progress:number):void {
            this.nanobarProgress = progress;
            this._progressHandler();
        }

        $onInit ():void {
            // Validations
            this._validateId();
            this._validateOptions();

            this.fsNanobarAutomationService.addNanobar(this.nanobarId, this);

            // init cleanup
            this.$scope.$on('$destroy', () => {
                $(this.nanobar.el).remove();
                this.nanobar = null;
            })

        };

        static $inject = ['$scope', '$element', 'assert', 'fsNanobarAutomationService', '$timeout'];

        constructor (public $scope:ng.IScope, public $element:ng.IAugmentedJQuery, public assert:any,
            public fsNanobarAutomationService:INanobarAutomationService, public $timeout:ng.ITimeoutService) {
        }
    }


    let linkFn:ng.IDirectiveLinkFn = function (scope:ng.IScope, instanceElement:ng.IAugmentedJQuery,
        instanceAttributes:ng.IAttributes, controller:NanobarController, transclude:ng.ITranscludeFunction):void {
        controller.renderNanobar();
    };

    let fsNanobarFn = ():ng.IDirective => {
        let nanobarDirective:ng.IDirective = {
            controller: NanobarController,
            controllerAs: 'nanobarCtrl',
            bindToController: {
                nanobarId: '@',
                // nanobarProgress: '<',
                nanobarOptions: '<'
            },
            link: linkFn
        };

        return nanobarDirective;
    };

    angular.module('Fortscale.shared.components.fsNanobar', [])
        .directive('fsNanobar', fsNanobarFn)
}
