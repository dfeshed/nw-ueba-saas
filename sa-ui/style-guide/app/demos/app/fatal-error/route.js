import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({

  fatalErrors: service(),

  model() {
    return {
      'title': 'Fatal Errors',
      'subtitle': 'Error handling for cases where the UI is unusable as a result of errors.',
      'description': 'Passing an error message to the fatalError service\'s logError method will forward that message to the fatalError modal, which in turns exposes it in the UI.',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/services/fatal-errors.js'
    };
  },

  afterModel() {
    this.get('fatalErrors').logError('This is an example error message');
  }
});
