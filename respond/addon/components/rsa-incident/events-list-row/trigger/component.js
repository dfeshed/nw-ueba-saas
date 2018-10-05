import Component from '@ember/component';
import computed from 'ember-computed-decorators';

const ENTER_KEY = 13;

export default Component.extend({
  testId: 'eventRowTrigger',
  classNames: ['events-list-table-cell'],
  attributeBindings: [
    'role',
    'tabIndex:tabindex',
    'testId:test-id',
    'ariaControls:aria-controls',
    'ariaExpanded:aria-expanded',
    'ariaPressed:aria-pressed'
  ],
  @computed('expanded')
  ariaExpanded(expanded) {
    return expanded ? 'true' : 'false';
  },
  @computed('expanded')
  ariaPressed(expanded) {
    return expanded ? 'true' : 'false';
  },
  click() {
    this.showDetail();
  },
  keyPress(e) {
    if (e && e.keyCode === ENTER_KEY) {
      this.showDetail();
    }
  }
});
