import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['query'],

  query: null,
  actions: {
    controllerNavigateToCertificateView(thumbprint) {
      this.send('navigateToCertificateView', thumbprint);
    }
  }

});
