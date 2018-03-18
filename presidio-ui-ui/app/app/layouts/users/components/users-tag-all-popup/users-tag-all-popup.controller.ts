module Fortscale.shared.components.fsUserTooltip {
    import IToastrService = Fortscale.shared.services.toastrService.IToastrService;
    declare var Opentip:any;

    const TEMPLATE_URL = 'app/layouts/users/components/users-tag-all-popup/users-tag-all-popup-internal.template.html';

    class UsersTagAllPopupComponentController {


        /**
         * The alerts list to be displayed in the tooltip
         */
        tags : {id:any,value:any, count?:number, isAssignable:boolean}[]=[];
        checked: {[key:string]:boolean} = {};
        newTagName:string;
        /**
         * Delegates
         */
        addTagsDelegate: (tagIds:{tagIds:string[]}) => ng.IPromise<any>;
        removeTagsDelegate: (tagIds:{tagIds:string[]}) => ng.IPromise<any>;


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
            className: 'users-tag-all-tooltip',
            showOn: 'click',
            hideOn: 'tip',
            stem: true,
            stemLength: 8,
            stemBase: 12,
            hideDelay: 0.3,
            tipJoint: 'top',
            fixed: true,
            removeElementsOnHide: false,
            group: 'users-tag-all-popup',
            background: '#f0f7f8',
            borderRadius: 3,
            borderColor: '#f0f7f8',
            shadow: true,
            shadowBlur: 15,
            shadowOffset: [0, 0],
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            containInViewport: true,
            offset: [0, 0],

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
                });
        }

        /**
         * Renders the tooltip
         *
         * @private
         */
        _initTooltip ():void {
            // Set this as ctrl for the callbacks
            let ctrl = this;

            let template = this.$templateCache.get(TEMPLATE_URL);
            let tooltipContent = this.$compile(angular.element(template))(ctrl.$scope);
            ctrl._tooltip = new Opentip(ctrl.$element, ctrl._tooltipSettings);
            ctrl._tooltip.setContent(tooltipContent);

            // Prevent close on mouseover tooltip
            ctrl._tooltip.content.on({
                mouseenter: function () {
                    ctrl._tooltip._abortHiding();
                },
                mouseleave: function () {
                    ctrl._tooltip.prepareToHide();
                    //ctrl._tooltip._abortHiding(); //If you want to show on leave
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


        addTags():void{
            let tagIds:string[] = this._getCheckedTagsIgs();
            if (this.newTagName && this.newTagName.length>0){
                let isNewTag = true;
                _.each(this.tags, (tag:{id:any,value:any, count?:number})=> {
                    // Tag with the same name already exists
                    if(tag.value.toLowerCase()===this.newTagName.toLowerCase()){
                        this.toastrService.error(`A tag with the name ${tag.value} already exists; please select a different name.`);
                        isNewTag = false;
                    }
                });
                if(!isNewTag){
                    this.newTagName = "";
                    this.closeTagPopup();
                    return;
                }
                tagIds.push(this.newTagName);
            }
            this.addTagsDelegate({"tagIds":tagIds}).then(()=>{
                if (this.newTagName){
                    this.newTagName="";
                }
                this.closeTagPopup();
            }).catch(()=>{

                this.closeTagPopup();

            });
        }

        removeTags():void{
            let tagIds:string[] = this._getCheckedTagsIgs();
            this.removeTagsDelegate({"tagIds":tagIds}).then(()=>{

                this.closeTagPopup();
            }).catch(()=>{

                this.closeTagPopup();

            });
        }

        _getCheckedTagsIgs():string[]{
            let checkedTags:any = _.pickBy(this.checked,(value) => {
                return value; //Only if the value of the key is true
            });
            return _.keys(checkedTags);
        }

        closeTagPopup():void{
                this.checked = {};
                this._tooltip.hide();
        }

        _initCloseOnWindowSizeChange(){
            let ctrl:any=this;
            angular.element(this.$window).bind('resize', function () {
                ctrl.closeTagPopup();

            });
        }

        $onInit ():void {
            this.tooltipTargetSelector="users-tags-all-popup";
            this._initSettings();
            this._initTooltip();
            this._initCleanup();
            this._initCloseOnWindowSizeChange();

        }


        static $inject = ['$scope', '$element','$templateCache','$compile','$window', 'toastrService'];

        constructor (public $scope:ng.IScope, public $element:JQuery,
                     public $templateCache:ng.ITemplateCacheService,
                     public $compile:ng.ICompileService,
                     public $window:ng.IWindowService,
                     public toastrService:IToastrService) {

        }

    }

    let UsersTagAllPopupComponent:ng.IComponentOptions = {

        controller: UsersTagAllPopupComponentController,
        controllerAs: '$ctrl',
        templateUrl: 'app/layouts/users/components/users-tag-all-popup/users-tag-all-popup.template.html',
        bindings: {
            tags: '<',
            removeTagsDelegate:'&',
            addTagsDelegate:'&'
        }

    }



    angular.module('Fortscale.shared.components')
        .component('usersTagsAllPopup', UsersTagAllPopupComponent);
}
