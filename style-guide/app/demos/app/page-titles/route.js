import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Page Titles',
      'subtitle': 'Patterns around updating page titles',
      'description': 'Pattern descriptions are listed below, and addon docs can be found at the link below.',
      'jsRepo': 'https://github.com/kimroen/ember-cli-document-title'
    };
  }

});
