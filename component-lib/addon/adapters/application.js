/**
* @file Application Adapter
* @description extends the DataAdapterMixin and sets the authorizer to
* our custom sa-authorizer
* @public
*/

import { getOwner } from '@ember/application';

import { computed } from '@ember/object';
import RESTAdapter from 'ember-data/adapters/rest';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';

export default RESTAdapter.extend(DataAdapterMixin, {

  config: computed(function() {
    return getOwner(this).resolveRegistration('config:environment');
  }),

  authorizer: computed(function() {
    return this.get('config')['ember-simple-auth'].authorizer;
  })

});
