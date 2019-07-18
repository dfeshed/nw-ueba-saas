import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { headers } from 'recon/reducers/emails/selectors';

export default Component.extend({
  layout,
  isEmailExpanded: false,

  @computed('email')
  headerFields(email) {
    return _.pickBy(email, (emailValue, emailField) => !(_.isEmpty(emailValue)) & headers.includes(emailField));
  },

  @computed('email.headers')
  additionalHeaderFields(headers) {
    const createdHeaders = {};
    headers.forEach(function(header) {
      if (!_.isEmpty(header.value)) {
        createdHeaders[header.name] = header.value;
      }
    });
    return createdHeaders;
  },

  @computed('isEmailExpanded')
  collapseArrowDirection(isEmailExpanded) {
    return isEmailExpanded ? 'down' : 'right';
  },

  actions: {
    toggleEmailExpansion() {
      const isEmailExpanded = this.toggleProperty('isEmailExpanded');
      if (this.toggleEmailExpansion) {
        this.toggleEmailExpansion(isEmailExpanded);
      }

    }
  }
});
