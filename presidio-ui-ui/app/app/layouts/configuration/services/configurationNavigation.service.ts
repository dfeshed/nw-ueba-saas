module Fortscale.layouts.configuration.configurationNavigation {

    import ObjectIterator = _.ObjectIterator;
    export interface IConfigurationPageData {
        id: string;
        displayName?: string;
        description?: string;
        component?: string;
        customPage?: boolean;
        configurable?: boolean;
        doNotShowHeader?: boolean;
        formClassNames?: string;
    }

    export interface IConfigurationNavigationService {
        addConfigurationPage (configurationPageData:IConfigurationPageData): ConfigurationNavigationService;
        renderNavigation (navContainer:JQuery, $scope:ng.IScope):void;
        getConfigurationPage (configurationPageId:string): IConfigurationPageData;
    }

    export class ConfigurationNavigationService implements IConfigurationNavigationService {

        private _errMsg = 'ConfigurationNavigationService: ';

        private _configurationPages:Map<string, IConfigurationPageData>;

        static $inject = ['assert', 'appConfig', '$compile', '$state'];

        constructor (public assert, public appConfig, public $compile, public $state) {
            this._configurationPages = new Map<string, IConfigurationPageData>()
        }


        private _mergeConfigPagesIntoNodeTree (configNodeTree:any):any {
            let newConfigNodeTree = _.merge({}, configNodeTree);

            // Iterate through new configuration pages, and create a node for each one (it intentionally overwrites
            // old nodes).
            let configurationPages = Array.from(this._configurationPages.values());
            _.each(configurationPages, (configurationPage:IConfigurationPageData) => {

                // Breakdown namespace (id) into nodes
                let nodes:string[] = configurationPage.id.split('.');

                // Create paths for each node. For each path sets the configurationPage object and adds nodes object.
                // for example: a.b.c.d will create a, a.nodes.b, a.nodes.b.nodes.c.nodes, a.nodes.b.nodes.c.nodes.d
                for (let i = 0; i < nodes.length; i += 1) {
                    let nameSpace = nodes.slice(0, i + 1).join(".nodes.");
                    let localNameSpace = `nodes.${nameSpace}`;

                    // Find if object exists
                    let configurationPageObject = _.get(newConfigNodeTree, localNameSpace);
                    // If object does not exist, set it in
                    if (!configurationPageObject) {
                        let displayName = configurationPage.displayName || nodes[i];
                        let placeholder:any = i === nodes.length - 1 ? configurationPage : {
                            id: nodes.slice(0, i + 1).join('.'), configurable: true
                        };
                        placeholder.displayName = displayName;
                        _.set(newConfigNodeTree, localNameSpace, placeholder);
                        _.set(newConfigNodeTree, localNameSpace + '.nodes', {});
                    }
                }

            });

            return newConfigNodeTree;
        }

        /**
         * Tentativelly builds all the missing configuration pages for the received path
         *
         * @param {IConfigurationPageData} configurationPageData
         * @private
         */
        private _buildConfigurationPagePath (configurationPageData:IConfigurationPageData):void {
            let nodes:string[] = configurationPageData.id.split('.');
            for (let i = 0; i < nodes.length; i += 1) {
                let nameSpace = nodes.slice(0, i + 1).join(".");

                if (!this._configurationPages.get(nameSpace)) {
                    // If its not the last index then take the node[i] as display name, otherwise take
                    // configurationPageData.displayName as display name or nodes[i] as default.
                    if (i + 1 < nodes.length) {
                        this._configurationPages.set(nameSpace, {
                            id: nameSpace,
                            displayName: nodes[i],
                            description: null,
                            component: null,
                            customPage: false,
                            configurable: true,
                            doNotShowHeader: false,
                            formClassNames: ''
                        });
                    } else {
                        this._configurationPages.set(nameSpace, {
                            id: nameSpace,
                            displayName: configurationPageData.displayName || nodes[i],
                            description: configurationPageData.description || null,
                            component: configurationPageData.component || null,
                            customPage: true,
                            configurable: true,
                            doNotShowHeader: !!configurationPageData.doNotShowHeader,
                            formClassNames: configurationPageData.formClassNames || ''
                        });
                    }
                }
            }
        }

        /**
         * Adds a custom configuration page
         *
         * @param configurationPageData
         * @returns {ConfigurationNavigationService}
         */
        addConfigurationPage (configurationPageData:IConfigurationPageData):ConfigurationNavigationService {
            // Validations
            let errMsg = `${this._errMsg}addConfigurationPage: `;
            this.assert.isString(configurationPageData.id, 'configurationPageData.id', errMsg);
            this.assert.isString(configurationPageData.displayName, 'configurationPageData.displayName', errMsg, true);
            this.assert.isString(configurationPageData.description, 'configurationPageData.description', errMsg, true);
            this.assert.isString(configurationPageData.component, 'configurationPageData.component', errMsg, true);
            this.assert(!this._configurationPages.get(configurationPageData.id),
                `${errMsg}ConfigurationPage id must be unique.`, RangeError);

            this._buildConfigurationPagePath(configurationPageData);

            return this;
        }

        /**
         * Returns a tree object of configuration pages (containers or customs)
         * @returns {any}
         */
        private _getConfigNodesTree ():any {
            let configNodeTree:any = this.appConfig.getConfigNodesTree();
            return this._mergeConfigPagesIntoNodeTree(configNodeTree);
        }

        /**
         * Creates an unordered list
         *
         * @returns {JQuery}
         * @private
         */
        private _createNavUl ():JQuery {
            return angular.element('<ul class="menu-pane--sub-menu"></ul>');
        }

        /**
         *
         * @param {string} currentConfigId
         * @param {string} targetConfigId
         * @private
         */
        private _isParentOfConfigContainer (currentConfigId:string, targetConfigId:string) {
            // Check against appConfig if is parent of config container. If not proceed to local check
            if (this.appConfig.isParentOfConfigContainer(currentConfigId, targetConfigId)) {
                return true;
            }

            // Figure if there are custom config pages declared, and if so check parenthood
            let currentConfigPage = this._configurationPages.get(currentConfigId);
            let targetConfigPage = this._configurationPages.get(targetConfigId);
            return currentConfigPage && targetConfigPage && targetConfigId.indexOf(currentConfigId) === 0;

        }

        /**
         * Creates a navigation item (bullet)
         *
         * @param {*} node
         * @param {number} indentLevel
         * @param {IScope} $scope
         * @returns {JQuery}
         * @private
         */
        private _createNavLi (node:any, indentLevel:number, $scope:ng.IScope) {

            let navLi = angular.element(`
                <li>
                    <a>${node.displayName}</a>
                    <span class="open-close-display">
                        <i class="fa fa-chevron-left closed"></i>
                        <i class="fa fa-chevron-down opened"></i>
                    </span>
                </li>
            `);
            navLi.find('a').attr({
                'ui-sref': CONFIG_FORM_STATE_NAME + '({stateName: "' + node.id + '"})'
            });
            navLi.attr({
                'class': 'menu-pane--menu-item', 'ui-sref-active': 'active'
            });
            
            navLi.css({
                'padding-left': (indentLevel * 10) + 'px'
            });

            function openCloseClickHandler () {
                if (navLi.hasClass(OPENED_CLASS_NAME)) {
                    navLi.removeClass(OPENED_CLASS_NAME);
                    navLi.addClass(CLOSED_CLASS_NAME);
                } else if (navLi.hasClass(CLOSED_CLASS_NAME)) {
                    navLi.removeClass(CLOSED_CLASS_NAME);
                    navLi.addClass(OPENED_CLASS_NAME);
                }
            }

            // if node has nodes
            if (node.nodes && Object.keys(node.nodes).length) {

                // add proper class. closed as default or opened if current state is a child of node.id
                if (this.$state.params.stateName &&
                    this._isParentOfConfigContainer(node.id, this.$state.params.stateName)) {
                    navLi.addClass(OPENED_CLASS_NAME);
                } else if (indentLevel === 1) {
                    navLi.addClass(OPENED_CLASS_NAME);
                } else {
                    navLi.addClass(CLOSED_CLASS_NAME);
                }

                // Add click handler
                navLi.on('click', openCloseClickHandler);

                // Cleanup
                $scope.$on('$destroy', function () {
                    navLi.off('click', openCloseClickHandler);
                });
            }

            return navLi;
        }


        /**
         * Renders a single configuration node (bullet). It runs recursively.
         * @param {{}} node
         * @param {IScope} $scope
         * @param {JQuery=} element
         * @param {number=} indentLevel
         * @returns {JQuery}
         * @private
         */
        private _renderNavNode (node:any, $scope:ng.IScope, element:JQuery = angular.element('<div></div>'),
            indentLevel:number = 0):JQuery {

            // Create li container if node has displayName and id
            if (node.id && node.displayName && node.configurable) {
                element.append(this._createNavLi(node, indentLevel, $scope));
            }

            // if nodes keys has length iterate through nodes and run recursively
            let nodesKeys = node.nodes ? Object.keys(node.nodes) : null;
            if (node.nodes && nodesKeys.length) {

                // Create ul
                let navUl = this._createNavUl();
                element.append(navUl);

                // For each node run _renderNavNode
                _.each(node.nodes, <ObjectIterator<{}, any>>_.bind(function (node) {
                    if (node.configurable) {
                        this._renderNavNode(node, $scope, navUl, indentLevel + 1);
                    }
                }, this));
            }

            return element;
        }

        /**
         * Renders navigation bar
         * @param {jQuery} navContainer
         * @param {IScope} $scope
         */
        renderNavigation (navContainer:JQuery, $scope:ng.IScope):void {
            let configNodeTree = this._getConfigNodesTree();
            let navElement = this._renderNavNode(configNodeTree, $scope);
            navElement = this.$compile(navElement)($scope);
            navContainer.append(navElement);
        }

        /**
         * Returns a configuration page
         *
         * @param {string} configurationPageId
         */
        getConfigurationPage (configurationPageId:string):IConfigurationPageData {
            return this._configurationPages.get(configurationPageId) || null;
        }

    }


    angular.module('Fortscale.layouts.configuration')
        .service('Fortscale.layouts.configuration.configurationNavigationService', ConfigurationNavigationService);
}
