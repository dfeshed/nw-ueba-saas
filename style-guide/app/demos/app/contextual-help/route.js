import Route from '@ember/routing/route';

export default Route.extend({
  model() {
    return {
      'title': 'Contextual Help',
      'description': 'Contextual help can be accessed either at the module level via the link in the header, or at the topic level via a help link in any component.'
    };
  }
});
