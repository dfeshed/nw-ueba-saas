import { computed } from '@ember/object';
import layout from './template';
import Component from '@ember/component';

const ENTER_KEY = 13;

export default Component.extend({
  layout,
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

  ariaExpanded: computed('expanded', function() {
    return this.expanded ? 'true' : 'false';
  }),

  ariaPressed: computed('expanded', function() {
    return this.expanded ? 'true' : 'false';
  }),

  click() {
    this.showDetail();
  },
  keyDown(e) {
    if (e && e.keyCode === ENTER_KEY) {
      this.showDetail();
    }
  }
});
