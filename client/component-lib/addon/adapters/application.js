/**
* @file Application Adapter
* @description extends the DataAdapterMixin and sets the authorizer to
* our custom sa-authorizer
* @public
*/

import DS from 'ember-data';
import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';
import getOwner from 'ember-getowner-polyfill';

export default DS.RESTAdapter.extend(DataAdapterMixin, {

  config: (function() {
    return getOwner(this).resolveRegistration('config:environment');
  }).property(),

  authorizer: (function() {
    return this.get('config')['ember-simple-auth'].authorizer;
  }).property()

});
