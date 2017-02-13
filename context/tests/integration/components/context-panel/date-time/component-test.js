import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('context-panel/date-time', 'Integration | Component | context panel/date time', {
  integration: true
});

test('Test context panel should display time window', function(assert) {
  this.set('timeStamp', 1486022329719);
  this.set('asTimeAgo', true);

  this.render(hbs`{{context-panel/date-time timestamp=timeStamp asTimeAgo=asTimeAgo}}`);
  assert.equal(this.$('.rsa-context-panel__context-data-table__heading-text').prevObject[0].textContent.trim(),
      '11 days ago');

});
