import Component from '@ember/component';
import { computed } from '@ember/object';
import { htmlSafe } from '@ember/string';

export default Component.extend({

  tagName: 'header',

  classNames: 'spec-masthead',

  model: null,

  description: computed('model.description', function() {
    return htmlSafe(this.get('model.description'));
  })

});
