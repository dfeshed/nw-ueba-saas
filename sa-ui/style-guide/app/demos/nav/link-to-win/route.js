import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Link To Window',
      'subtitle': 'A hyperlink that uses window.open rather than Ember transitions',
      'description': '',
      'testFilter': 'rsa-link-to-win',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-link-to-win.js',
      'styleRepo': '',
      'templateRepo': ''
    };
  }

});
