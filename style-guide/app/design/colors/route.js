import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Colors',
      'subtitle': 'A palette of named colors for styling components.',
      'description': 'RSA uses a specific palette of colors across its themes. These colors are implemented as SASS variables with friendly names so that they can be referenced throughout our styles. To ensure consistency across the UI, avoid using ad-hoc color values; use these named color variables instead.',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_colors.scss'
    };
  }
});
