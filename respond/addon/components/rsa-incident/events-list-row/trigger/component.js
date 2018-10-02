import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  tagName: 'button',
  testId: 'eventRowTrigger',
  classNames: ['events-list-table-cell'],
  attributeBindings: [
    'testId:test-id',
    'ariaControls:aria-controls',
    'ariaExpanded:aria-expanded'
  ],
  @computed('expanded')
  ariaExpanded(expanded) {
    return expanded ? 'true' : 'false';
  },
  click() {
    this.showDetail();
    this.element.focus();
  }
});
