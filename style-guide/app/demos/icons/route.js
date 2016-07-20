import Ember from 'ember';
const { Route } = Ember;

export default Route.extend({

  model() {
    return {
      'title': 'Icons',
      'subtitle': 'A small subset of out vast icon library.',
      'description': 'For icon options, refer to the Styles linked below. To preview all icons, refer to <a target=\'_blank\' href=\'http://www.streamlineicons.com/\'>http://www.streamlineicons.com/</a>, or ask your designer to help you with the specific icon you\'re looking for.',
      'testFilter': 'rsa-icon',
      'jsRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/addon/components/rsa-icon.js',
      'styleRepo': 'https://github.rsa.lab.emc.com/asoc/sa-ui/blob/master/client/component-lib/app/styles/component-lib/base/_icons.scss'
    };
  }

});
