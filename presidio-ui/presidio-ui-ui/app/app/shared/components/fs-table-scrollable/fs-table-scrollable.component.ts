module Fortscale.shared.components.fsTableScrollable {


    const FETCH_AHEAD_PAGES:number = 5;
    const FS_TABLE_CALLER_ID:string='fs-tabLe-caller-id';
    import IStateManagementService = Fortscale.shared.services.stateManagementService.IStateManagementService;

    class TableScrollableController {

        _tableElement: any;
        pagesCache: any[] = [];
        useCache: boolean;
        tableSettings: any;
        readDataDelegate: any;
        totalPage: number;
        stateId: string;

        /**
         * Takes a tableElement angular element, compiles it, and returns it.
         *
         * @param {angular.element} tableElement
         * @returns {angular.element}
         * @private
         */
        _compileKendoGridElement(tableElement) : any {
            return this.$compile(tableElement)(this.$scope);
        }

        /**
         * Creates an angular element from a kendo-grid tag and returns it.
         *
         * @returns {angular.element}
         * @private
         */
        _createKendoGridElement ():  any {
            return angular.element('<kendo-grid class="fs-table-scrollable" options=' +
                '"$ctrl.tableSettings" > ' +
                '</kendo-grid>');
        }

        /**
         * Register _reloadTable to be executed when state changed
         * @private
         */
        _initStateChangeWatch(): void{

            this.stateManagementService.registerToStateChanges(this.stateId,FS_TABLE_CALLER_ID,this._reloadTable.bind(this));
        }

        /**
         * Reload the table again.
         * Should be called when filter or sort change
         * @private
         */
        _reloadTable(): void{
            this._removeTableElement();
            this._renderTable();
        }

        /**
         * Remove the table from the DOM
         * @private
         */
        _removeTableElement(): void{
            var prevKendoElement = this.$element.find('[kendo-grid]');
            prevKendoElement.remove();


        }

        /**
         * Render the table into the DOM
         * @private
         */
        _renderTable(): void{
            let tableElement = this._createKendoGridElement();
            // Compile and link table element
            tableElement = this._compileKendoGridElement(tableElement);

            this.$element.append(tableElement);
            this._tableElement = tableElement;
        }

        $onInit () : void {
            this._initStateChangeWatch();
            this._initReadDataDelegate();
            this._renderTable();

        }

        /**
         * This method intiate the read function on the transport.
         * If we are not using cache, the transport will be the delegate as retrieved from outside.
         * If we are using cache, the delegate will be wrapped by  _readDataWrapper, and read function of the transport
         * will be the _readDataWrapper which contain the delegate
         *
         * if no dataSource.transport configured the user will get an error.
         * @private
         */
        _initReadDataDelegate():void{
            this.useCache = !!this.useCache;

            if (this.tableSettings.dataSource && this.tableSettings.dataSource.transport){
                this.tableSettings.dataSource.transport.read = this._readDataWrapper.bind(this);
            } else {
                throw new Error("Table settings must have 'dataSource.trasnport' property");
            }

        }

        /**
         * This method called from the grid when have new page should be loaded (only if useCache = true)
         * The method check if the page already in cache. If it does it return the page immedietly,
         * if not it called to "readWithCache" to load several pages and store the in the cache for future use
         * @param options -
         * @private
         */
        _readDataWrapper(options){
            //page number start from 1. Cache start from index 0.
            let pageNumber : number = options.data.page;
            let pageSize : number = options.data.pageSize;

            let pagePromise: any;
            if (!this.useCache){
                pagePromise=this.readDataDelegate({"pageNumber" : pageNumber, "pageSize" : pageSize});
            } else {
                pagePromise = this.readWithCache(pageNumber,pageSize);
            }

            pagePromise.then(function (page:any) {
                options.success(page);
            });

        }

        /**
         * That method load FETCH_AHEAD_PAGES number of pages, and add them to the cache.
         *
         * @param page - start from 1
         * @param pageSize
         * @returns {function(any=): JQueryPromise<T>|function(string=, Object=): JQueryPromise<any>|IPromise<T>}
         */
        readWithCache(pageNumber: number, pageSize: number):ng.IPromise<any> {
            let ctrl:any = this;


            //If page already loaded
            if (typeof ctrl.pagesCache[pageNumber-1] === "object"){

                let data:any = {data: ctrl.pagesCache[pageNumber-1], total: ctrl.totalPages};
                //let deferred:any = this.$q.defer();
                //deferred.resolve(data);
                //return deferred.promise;
                return this.$q.when(data);
            } else {
                //Page should be loaded with cache. Each call retrieve "pagesGroup " according to amount in FETCH_AHEAD_PAGES
                let groupRequestSize: number =  FETCH_AHEAD_PAGES * pageSize; //Each call fetch 3 pages.
                let groupRequestPageNumber: number = Math.ceil(pageNumber / FETCH_AHEAD_PAGES); //The group number for the server request

                return this.readDataDelegate({"pageNumber" : groupRequestPageNumber, "pageSize" : groupRequestSize})
                    .then((data:any) => {
                        // Validate data

                        if (data) {

                            let firstResultPageNumber : number= (groupRequestPageNumber-1) * FETCH_AHEAD_PAGES+1;
                            ctrl._savePagesToCache(data.data, firstResultPageNumber, pageSize);

                            ctrl.totalPages = data.total;
                        }
                        return {data: ctrl.pagesCache[pageNumber-1], total: ctrl.totalPages};


                    });
            }



        }

        /**
         * Split the data retrieved from the server (the response should contain FETCH_AHEAD_PAGES* page_size rows)
         * into FETCH_AHEAD_PAGES and add each one to the cache in right index.
         * @param data
         * @param firstResultPageNumber
         * @param pageSize
         * @private
         */
        _savePagesToCache(data:any[], firstResultPageNumber:number, pageSize:number) {


            for (var i=0; i<FETCH_AHEAD_PAGES; i++) {
                let pageData:any = data.splice(0,pageSize);
                this.pagesCache[firstResultPageNumber+i-1] = pageData;
            }

        }


        static $inject = ['$scope','$element', '$compile',"$q",'stateManagementService'];

        constructor (public $scope:ng.IScope,public $element:ng.IAugmentedJQuery, public $compile:ng.ICompileService
        ,public $q:ng.IQService, public stateManagementService:IStateManagementService) {

        }
    }

    let fsTableScrollable:ng.IComponentOptions = {
        controller: TableScrollableController,
        templateUrl: 'app/shared/components/fs-table-scrollable/fs-table-scrollable.view.html',
        bindings: {
            stateId: '<',
            tableSettings: '<',
            readDataDelegate: '&',
            useCache: '<'
        }
    };
    angular.module('Fortscale.shared.components')
        .component('fsTableScrollable', fsTableScrollable);
}
