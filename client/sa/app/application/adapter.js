/**
* @file Application Adapter
* @description extends the DataAdapterMixin and sets the authorizer to
* our custom sa-authorizer
* @author Srividhya Mahalingam
*/

import DS from "ember-data";
import config from "../config/environment";
import DataAdapterMixin from "ember-simple-auth/mixins/data-adapter-mixin";

export default DS.RESTAdapter.extend(DataAdapterMixin, {
    authorizer: config["ember-simple-auth"].authorizer
});
