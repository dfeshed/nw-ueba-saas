import RsaApplicationRoute from 'component-lib/routes/application';

export default RsaApplicationRoute.extend({

  model(params) {
    // jscs:disable requireCamelCaseOrUpperCaseIdentifiers
    return this.store.peekRecord('spec', params.spec_id);
  }

});
