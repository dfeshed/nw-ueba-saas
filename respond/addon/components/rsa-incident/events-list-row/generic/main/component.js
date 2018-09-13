import Component from '@ember/component';

export default Component.extend({
  testId: 'genericEventMain',
  attributeBindings: ['testId:test-id'],
  classNames: ['events-list-info'],
  click() {
    this.expand(this.item.id);
  }
});
