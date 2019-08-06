import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  @computed('email.bodyContent')
  renderEmailBodyContent(bodyContent) {
    return bodyContent;
  }
});
