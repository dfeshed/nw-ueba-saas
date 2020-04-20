import Component from '@ember/component';
import computed from 'ember-computed-decorators';

import layout from './template';
import { prepareTextForDisplay } from '../util';

export default Component.extend({
  layout,
  tagName: '',

  metaToHighlight: null,
  text: null,

  @computed('text', 'metaToHighlight.value')
  renderedText(text, metaToHighlight) {
    return prepareTextForDisplay(text, metaToHighlight);
  }
});
