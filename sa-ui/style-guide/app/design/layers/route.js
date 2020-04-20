import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      'title': 'Layers',
      'subtitle': 'Standards for z-index.',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/component-lib/app/styles/component-lib/base/design/_layers.scss'
    };
  }

});
