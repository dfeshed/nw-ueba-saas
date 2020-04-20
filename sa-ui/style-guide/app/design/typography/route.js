import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Typography',
      'subtitle': 'Typefaces, type weights and type sizes.',
      'description': 'This Style Guide prescribes text styles for consistency across the UI. You will find these styles used in components across the application, such as Button and Text.',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/design/_fonts.scss'
    };
  }

});
