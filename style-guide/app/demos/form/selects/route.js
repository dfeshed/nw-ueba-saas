import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Form Select',
      'subtitle': 'Multi-option enabled select dropdown',
      'description': 'For consistent form elements throughout the application, use these components with their out-of-the-box styles rather than applying ad-hoc styling.',
      'testFilter': 'ember-power-select-trigger',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-select.scss'
    };
  }

});
