import Component from 'ember-component';
import computed from 'ember-computed-decorators';

export default Component.extend({

  tagName: 'hbox',

  classNames: ['host-title-bar flexi-fit rsa-nav-tab-group heading-tabs'],

  @computed('tabs')
  visibleTabs(tabs) {
    return tabs.filter((tab) => !tab.hidden);
  },

  actions: {
    activate(tabName) {
      this.sendAction('defaultAction', tabName);
    }
  }
});
