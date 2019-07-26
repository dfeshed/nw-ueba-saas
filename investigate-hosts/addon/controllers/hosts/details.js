import Controller from '@ember/controller';

export default Controller.extend({
  // Query Params
  queryParams: ['sid'],

  sid: null,

  actions: {
    controllerNavigateToTab(category) {
      this.send('navigateToTab', category);
    }
  }


});
