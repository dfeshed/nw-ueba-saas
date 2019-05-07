import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import complianceConfig from './compliance-config';
import { or } from 'ember-computed-decorators';
import ContextualHelp from 'component-lib/mixins/contextual-help';
import { scheduleOnce } from '@ember/runloop';

export default Component.extend(ContextualHelp, {
  layout,
  tagName: 'div',
  classNames: ['license-banner'],
  classNameBindings: ['isHidden:hidden:shown', 'level'],

  license: service(),
  contextualHelp: service(),

  compliant: true,

  @or('compliant', 'dismissed')
  isHidden: true,

  async _getLicenseCompliance() {
    const { compliant, compliances: [ compliance ] } = await this.get('license').getCompliance();

    if (this.get('isDestroyed') || this.get('isDestroying')) {
      return;
    }
    this.set('compliant', compliant);
    const { messageKey, level, url, urlTextKey } = complianceConfig[compliance.status];
    this.setProperties({ messageKey, level, url, urlTextKey });
    if (level === 'error') {
      this.get('license').resetBannerDismissed();
    } else {
      this.set('dismissed', this.get('license').isBannerDismissed());
    }
  },

  init() {
    this._super(...arguments);
    scheduleOnce('afterRender', this, '_getLicenseCompliance');
  },

  actions: {
    dismiss() {
      this.set('dismissed', true);
      this.get('license').setBannerDismissed();
    }
  }

});