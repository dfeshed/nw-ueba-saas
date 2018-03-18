(function () {
    'use strict';

    function FsIndexedDBService ($q) {
        var service = this;

        /**
         * Cross platform
         */
        service.indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;
        service.IDBTransaction = window.IDBTransaction || window.webkitIDBTransaction || window.msIDBTransaction;
        service.IDBKeyRange = window.IDBKeyRange || window.webkitIDBKeyRange || window.msIDBKeyRange;
        service.log = console.log.bind(console);
        service.error = console.error.bind(console);

        /**
         * Standard handler for a request. Adds success, complete, and error failures.
         *
         * @param {*} req
         * @param {function} resolve
         * @param {function} reject
         * @private
         */
        service._standardPromiseHandler = function (req, resolve, reject) {
            req.addEventListener('success', res => resolve(res));
            req.addEventListener('complete', res => resolve(res));
            req.addEventListener('error', err => {
                service.error(err);
                reject(err);
            });
        };

        /**
         * Returns a specific store (or null if no transaction)
         * @param {IDBDatabase} db
         * @param {string} objectStoreName
         * @param {function} resolve
         * @param {function} reject
         * @param {string} errMsg
         * @returns {IDBObjectStore|null}
         * @private
         */
        service._getStore = function (db, objectStoreName, resolve, reject, errMsg) {
            var transaction = service.getTransaction(db, objectStoreName);
            // Validate transaction was made successfully. If not, log and reject promise.
            if (!transaction) {
                service.error(errMsg);
                reject(new Error(errMsg));
                return null;
            }
            service._standardPromiseHandler(transaction, resolve, reject);
            return transaction.objectStore(objectStoreName);
        };

        /**
         * Opens an existing database, or creates a new one for a specific table (dbStore).
         *
         * @param {string} dbName
         * @param {string} dbStoreName
         * @param {string} dbStoreConfig
         * @param {Array<{indexName: string, keyPath: string, options: {}=}>} indices
         * @param {number} rev
         * @returns {Promise}
         */
        service.openDb = function (dbName, dbStoreName, dbStoreConfig, indices, rev) {

            // Set defaults
            dbStoreConfig = dbStoreConfig || {};
            indices = indices || [];
            rev = rev || 1;

            // Create a promise
            return $q((resolve, reject) => {
                var req = service.indexedDB.open(dbName, rev);
                service._standardPromiseHandler(req, resolve, reject);

                /**
                 * Handler for db upgrade. Will fire when db doesn't exists or revolution is higher than current.
                 * @param evt
                 */
                req.onupgradeneeded = function (evt) {

                    // get the db
                    var db = evt.currentTarget.result;

                    try {
                        // delete the old store
                        db.deleteObjectStore(dbStoreName);
                    } catch (err) {
                    }

                    try {
                        // Create a new store
                        var objectStore = db.createObjectStore(dbStoreName, dbStoreConfig);

                        // Add indices
                        _.each(indices, function (indexObj) {
                            objectStore.createIndex(indexObj.indexName, indexObj.keyPath, indexObj.options || {});
                        });
                    } catch (err) {
                        service.error(err);
                        throw err;
                    }

                };
            })
            // return the db from the event
                .then(evt => evt.target.result)
                .catch(err => {
                    service.error(err);
                    throw err;
                });
        };

        /**
         * Deletes a database
         *
         * @param {string} dbName
         * @returns {*}
         */
        service.deleteDB = function (dbName) {
            return $q(function (resolve, reject) {
                var req = service.indexedDB.deleteDatabase(dbName);
                service._standardPromiseHandler(req, resolve, reject);
            })
                .catch(err => {
                    service.error(err);
                    throw err;
                });

        };

        /**
         * Returns a transaction, or null if transaction was unsuccessful
         * @param {IDBDatabase} db
         * @param {string} objectStoreName
         * @returns {IDBTransaction|null}
         */
        service.getTransaction = function (db, objectStoreName) {
            try {
                return db.transaction([objectStoreName], 'readwrite');
            } catch (err) {
                service.error(err);
                return null;
            }
        };

        /**
         * Adds an object to a store
         *
         * @param {IDBDatabase} db
         * @param {string} objectStoreName
         * @param {{}} obj
         * @returns {Promise}
         */
        service.addObject = function (db, objectStoreName, obj) {
            return $q((resolve, reject) => {
                var errMsg = 'FsIndexedDBService: addObject: No transaction';
                // get object store
                var objectStore = service._getStore(db, objectStoreName, resolve, reject, errMsg);
                if (!objectStore) {
                    return reject(errMsg);
                }

                // add the object to store
                var request = objectStore.add(obj);
                // log on any error in request
                request.onerror = function (err) {
                    service.error(err);
                };

            })
                .catch(err => {
                    service.error(err);
                    throw err;
                });
        };

        /**
         * Counts the number of objects in a store by index
         * @param {IDBDatabase} db
         * @param {string} objectStoreName
         * @param {string=} indexName
         * @returns {Promise}
         */
        service.count = function (db, objectStoreName, indexName) {
            var countRequest;

            return $q((resolve, reject) => {
                var errMsg = 'FsIndexedDBService: count: No transaction';
                // get object store
                var objectStore = service._getStore(db, objectStoreName, resolve, reject, errMsg);
                if (!objectStore) {
                    return reject(errMsg);
                }

                // get index (indexName either received or the first index in store)
                var index = objectStore.index(indexName || objectStore.indexNames[0]);
                // count the item in the store
                countRequest = index.count();

            })
                .then(() => {
                    return countRequest.result;
                })
                .catch(err => {
                    service.error(err);
                    throw err;
                });
        };

        /**
         * Gets all items from a store.
         *
         * @param {IDBDatabase} db
         * @param {string} objectStoreName
         * @returns {Promise|*}
         */
        service.findAll = function (db, objectStoreName) {
            return $q((resolve, reject) => {
                var transaction = db.transaction(objectStoreName, service.IDBTransaction.READ_ONLY);
                var store = transaction.objectStore(objectStoreName);
                var items = [];
                service._standardPromiseHandler(transaction, () => resolve(items), reject);

                var cursorRequest = store.openCursor();

                cursorRequest.onerror = function (err) {
                    service.error(err);
                };

                cursorRequest.onsuccess = function (evt) {
                    var cursor = evt.target.result;
                    if (cursor) {
                        items.push(cursor.value);
                        cursor.continue();
                    }
                };
            })
                .catch(err => {
                    service.error(err);
                    throw err;
                });

        };

        service.deleteAll = function (db, objectStoreName) {
            var errMsg = 'FsIndexedDBService: deleteAll: ';

            return $q((resolve, reject) => {
                // get object store
                var objectStore = service._getStore(db, objectStoreName, resolve, reject, errMsg);
                if (!objectStore) {
                    return reject(errMsg);
                }

                // add the object to store
                var request = objectStore.clear();
                // log on any error in request
                request.onerror = function (err) {
                    throw err;
                };
            })
                .catch(err => {
                    service.error(err);
                    throw err;
                });

        };
    }

    FsIndexedDBService.$inject = ['$q'];
    angular.module('Fortscale.shared.services.fsIndexedDBService', [])
        .service('fsIndexedDBService', FsIndexedDBService);

}());
