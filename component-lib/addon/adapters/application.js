/**
* @file Application Adapter
* @description extends the DataAdapterMixin and sets the authorizer to
* our custom sa-authorizer
* @public
*/

import Ember from 'ember';
import RESTAdapter from 'ember-data/adapters/rest';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';

const {
  getOwner,
  computed
} = Ember;

export default RESTAdapter.extend(DataAdapterMixin, {

  config: computed(function() {
    return getOwner(this).resolveRegistration('config:environment');
  }),

  authorizer: computed(function() {
    return this.get('config')['ember-simple-auth'].authorizer;
  })

});
