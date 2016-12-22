import Ember from 'ember';

const {
  Route,
  inject: {
    service
  }
} = Ember;

export default Route.extend({

  flashMessages: service(),

  model() {
    return {
      'title': 'Flash Messages',
      'subtitle': 'Flash messages for success, info, and errors.',
      'description': 'ember-cli-flash is used to provide flash messages. There are a number of standards listed below that should satisfy most use cases. If additional functionality is required or for general setup instructions, be sure to consult the docs on github.',
      'jsRepo': 'https://github.com/poteto/ember-cli-flash'
    };
  }

});
