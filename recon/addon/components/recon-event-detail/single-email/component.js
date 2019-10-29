import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import fetch from 'component-lib/utils/fetch';
import { inject as service } from '@ember/service';

export default Component.extend({
  layout,
  isEmailExpanded: false,
  i18n: service(),

  init() {
    this._super(...arguments);

    if (this.get('emailCount') === 1) {
      this.toggleProperty('isEmailExpanded');
    }
  },

  didReceiveAttrs() {
    this._super(...arguments);

    // the bodyContent of HTML emails is not embedded in the email object, but served separately on a
    // REST URL (after scrubbing off scripts, anchor tags, images, etc). Hence when the email is being rendered,
    // "fetch" the body from the REST url specified in the "bodyUrl" property of the email.
    if (!this.get('isBodyLoaded')) {
      fetch(this.get('email.bodyUrl')).then((fetched) => fetched.text()).then((response) => {
        this.set('lazyLoadedBody', response);
      }).catch(() => {
        this.set('lazyLoadError', true);
      });
    }
  },

  @computed('isEmailExpanded')
  collapseArrowDirection(isEmailHeadersExpanded) {
    return isEmailHeadersExpanded ? 'subtract' : 'add';
  },

  @computed('email.bodyContent', 'lazyLoadedBody')
  isBodyLoaded(bodyContent, lazyLoadedBody) {
    return bodyContent || lazyLoadedBody;
  },

  actions: {
    toggleEmailExpansion() {
      this.toggleProperty('isEmailExpanded');
    }
  }
});
