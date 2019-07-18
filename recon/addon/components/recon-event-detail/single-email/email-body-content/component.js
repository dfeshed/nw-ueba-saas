import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { htmlSafe } from '@ember/template';

export default Component.extend({
  layout,

  @computed('email.bodyContent')
  renderEmailBodyContent(bodyContent) {
    return htmlSafe(_.unescape(bodyContent));
  }
});
