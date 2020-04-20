import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Date/Time Range',
      'subtitle': '',
      'description': 'rsa-date-time-range provides an editable set of inputs representing a range between two dates/times',
      'testFilter': 'rsa-date-time-range',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-date-time-range/component.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/form/_rsa-date-time-range.scss',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-date-time-range/template.hbs'
    };
  }
});
