import Ember from 'ember';

const {
  Route,
  inject: {
    service
  }
} = Ember;

export default Route.extend({

  fatalErrors: service(),

  model() {
    return {
      'title': 'Fatal Errors',
      'subtitle': 'Error handling for cases where the UI is unusable as a result of errors.',
      'description': 'Passing an error message to the fatalError service\'s logError method will forward that message to the fatalError modal, which in turns exposes it in the UI.'
    };
  },

  afterModel() {
    this.get('fatalErrors').logError('This is an example error message');
  }
});
