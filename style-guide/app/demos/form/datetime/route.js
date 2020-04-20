import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Date/Time',
      'subtitle': '',
      'description': 'rsa-form-datetime gives you a datepicker input component that can be used in NW application. Pikaday and Moment.js are used in the background.',
      'testFilter': 'rsa-form-datetime',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-form-datetime.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_form-datetime-picker.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/templates/components/rsa-form-datetime.hbs'
    };
  }
});
