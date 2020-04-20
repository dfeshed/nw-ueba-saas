import Component from '@ember/component';
import layout from '../templates/components/rsa-nav-tab';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  classNames: ['rsa-nav-tab'],

  classNameBindings: ['isActive', 'tabsAlignment'],

  isActive: false,

  align: 'left',

  compact: false,

  @computed('align', 'compact')
  tabsAlignment(align, compact) {
    return `is-${align}-aligned-${compact ? 'secondary' : 'primary'}`;
  }
});
