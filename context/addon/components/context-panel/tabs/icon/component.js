import { computed } from '@ember/object';
import layout from './template';
import Component from '@ember/component';

export default Component.extend({
  layout,
  classNameBindings: ['derivedClassName'],

  derivedClassName: computed('tab.toolTipText', 'tab.loadingIcon', function() {
    if (this.tab?.toolTipText && !this.tab?.loadingIcon) {
      return 'rsa-context-panel__tabs__disabled';
    }
    return this.tab?.loadingIcon ? '' : 'rsa-context-panel__tabs__enabled';
  })
});
