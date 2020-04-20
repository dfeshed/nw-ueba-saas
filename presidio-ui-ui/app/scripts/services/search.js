(function () {
    "use strict";

    /**
     * Service for searching data, for example searching available values in an entity
     * @param reports
     * @param DataEntityField
     * @param Report
     * @returns {{searchDataEntityField: searchDataEntityField}}
     */
    function search (reports, DataEntityField, Report) {

        function getDataEntityFieldSearchSettings (dataEntityField, labelField) {
            if (!(dataEntityField instanceof DataEntityField)) {
                throw new TypeError("Invalid data entity field, expected an instance of dataEntityField.");
            }

            if (labelField && !(labelField instanceof DataEntityField)) {
                throw new TypeError("Invalid labelField, expected an instance of dataEntityField.");
            }

            /* jshint validthis: true */
            return {
                search: searchDataEntityField.bind(this, dataEntityField.entity.id, dataEntityField.id,
                    labelField && labelField.id, null),
                placeholder: "Search " + dataEntityField.name,
                resultField: dataEntityField.id,
                showValueOnSelect: true
            };
        }

        function searchDataEntityField (dataEntityId, dataEntityFieldId, labelFieldId, extraTerms, term) {
            var report = new Report({
                endpoint: {
                    api: "dataQuery",
                    dataQuery: getSearchFieldDataQuery(dataEntityId, dataEntityFieldId, labelFieldId, extraTerms)
                }, requiredParams: ["term"], params: [{
                    dashboardParam: "term", field: "term"
                }], mock_data: "user_search"
            });

            return reports.runReport(report, {term: term}).then(function (results) {

                /**
                 *
                 * @type {Array<{normalized_username: string=, label: string=, value: string=}>}
                 */
                var tempArray = [];
                //since the search was changed to "Start with" and not "like" - no need for this option any more
                //keeping this code for the case we we change it back to like
                /*
                 //add the searched term to the first position at the result array
                 // - only if it's not exactly the return result
                 var addSearchLabel = true;
                 if(results.data.length == 1){
                 report.endpoint.dataQuery.fields.forEach(function (field) {
                 if(field.id && results.data[0][field.id] && results.data[0][field.id].toLowerCase() ===
                  term.toLowerCase()){
                 addSearchLabel = false;
                 }
                 });
                 }*/
                var addSearchLabel = false;
                if (addSearchLabel) {
                    tempArray[0] = {label: "Search: " + term, value: term};
                }
                results.data.forEach(function (result) {
                    tempArray.push(result);
                });

                //sanity check to verify display_name and normalized_username are present
                if (tempArray.length > 0 && tempArray[0].display_name && tempArray[0].normalized_username) {
                    //check for duplicate display names, if found concatenate the normalized_username
                    for (var i = 0; i < tempArray.length; i++) {
                        for (var j = i + 1; j < tempArray.length; j++) {
                            //if a pair was found, update both and go on to check the next name
                            if (tempArray[i].display_name === tempArray[j].display_name) {
                                tempArray[i].display_name += " (" + tempArray[i].normalized_username + ")";
                                tempArray[j].display_name += " (" + tempArray[j].normalized_username + ")";
                                break;
                            }
                        }
                    }
                }

                return tempArray;
            }).catch(function (error) {
                console.error("Can't execute field search: ", error);
            });
        }

        /*
         *   The search if preform using the start with operator define as part of the dataQuery language.
         *   dataEntityId - The entity on which the search is been perform
         *   dataEntityFieldId - field used as the return result of the search for the dashboard/widget using the search
         *   labelFieldId - field used for the search itself
         *   extraTerms - in the case you need to add additional condition to the search data query
         */
        function getSearchFieldDataQuery (dataEntityId, dataEntityFieldId, labelFieldId, extraTerms) {
            var fields = dataEntityFieldId;
            //if fields is not an array, make it an array (this test is for legacy purposes)
            if (!angular.isArray(fields)) {
                fields = [dataEntityFieldId];
            }
            if (labelFieldId) {
                fields.push(labelFieldId);
            }
            var terms = [{
                type: "field", id: labelFieldId || dataEntityFieldId, operator: "startsWith", valueParam: "term"
            }];
            if (extraTerms) {
                terms = terms.concat(extraTerms);
            }
            return {
                entity: dataEntityId, fields: fields, conditions: {
                    operator: "AND", terms: terms
                }, groupBy: fields, sort: labelFieldId || dataEntityFieldId, limit: 10
            };
        }

        return {
            getDataEntityFieldSearchSettings: getDataEntityFieldSearchSettings,
            searchDataEntityField: searchDataEntityField
        };

    }

    search.$inject = ["reports", "DataEntityField", "Report"];

    angular.module("Search", ["Utils", "Reports", "DataEntities"]).factory("search", search);

})();
