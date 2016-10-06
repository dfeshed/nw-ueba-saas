import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({
  model() {
    return {
      'title': 'Memory/File Size',
      'subtitle': 'Simple component for displaying memory/file sizes.',
      'description': 'This component supports translating byte file sizes to KB (rounded to 1 decimal) at sizes > 1024b, including rendering the proper label. The original full value is present in a tooltip.',
      'testFilter': 'rsa-content-memsize',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-content-memsize.js',
      'templateRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/templates/components/rsa-content-memsize.hbs'
    };
  }

});
