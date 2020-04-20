/**
* @file Application Adapter
* @description extends the DataAdapterMixin and sets the authorizer to
* our custom sa-authorizer
* @public
*/

import { inject as service } from '@ember/service';

import { computed } from '@ember/object';
import RESTAdapter from 'ember-data/adapters/rest';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';

export default RESTAdapter.extend(DataAdapterMixin, {

  session: service(),

  headers: computed(function() {
    return {
      'Authorization': `Bearer ${this.get('session.persistedAccessToken')}`
    };
  }).volatile()

});
