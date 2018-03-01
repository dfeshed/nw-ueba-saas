import layout from './template';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';

export default Component.extend({
  layout,
  classNameBindings: ['derivedClassName'],

  @computed('tab.toolTipText', 'tab.loadingIcon')
  derivedClassName(toolTipText, loadingIcon) {
    if (toolTipText && !loadingIcon) {
      return 'rsa-context-panel__tabs__disabled';
    }
    return loadingIcon ? '' : 'rsa-context-panel__tabs__enabled';
  }

});
