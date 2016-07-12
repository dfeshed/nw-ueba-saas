/**
* @file Application Adapter
* @description extends the DataAdapterMixin and sets the authorizer to
* our custom sa-authorizer
* @public
*/

import Ember from 'ember';
import DS from 'ember-data';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';
const { getOwner } = Ember;

export default DS.RESTAdapter.extend(DataAdapterMixin, {

  config: Ember.computed(function() {
    return getOwner(this).resolveRegistration('config:environment');
  }),

  authorizer: Ember.computed(function() {
    return this.get('config')['ember-simple-auth'].authorizer;
  })

});
