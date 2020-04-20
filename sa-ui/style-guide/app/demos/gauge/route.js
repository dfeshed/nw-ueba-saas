import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Gauge',
      'subtitle': 'Dashboard-like gauge with label and animation',
      'description': 'The gauge will expand to fill available space. Random values shown here for demonstration. "value" can accept a number between 0 and 1 inclusive, or a percentage as a string.',
      'testFilter': 'rsa-gauge',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/addon/components/rsa-gauge/component.js'
    };
  }

});
