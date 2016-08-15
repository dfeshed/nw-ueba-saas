import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import selectors from 'sa/tests/selectors';

moduleForComponent('rsa-respond/incident-detail/detail-overview', 'Integration | Component | rsa respond/incident detail/detail overview', {
  integration: true
});

test('it renders', function(assert) {
  let model = {
    id: 'INC-1',
    summary: 'Test Summary'
  };
  model.save = function() {};

  let saveAction = function(updatedField, updatedVal) {
    assert.equal(updatedField, 'summary', 'Testing detail overview has right updated field');
    assert.equal(updatedVal, 'Updated Summary', 'Testing detail overview has right  updated summary');
  };

  this.setProperties({
    model,
    saveAction
  });

  this.render(hbs`{{rsa-respond/incident-detail/detail-overview model=model saveAction=saveAction}}`);

  assert.equal(this.$(selectors.pages.respond.details.overview.accordion).length, 1, 'Testing detail overview accordion element exists');

  let element = this.$(selectors.pages.respond.details.overview.textarea);
  assert.equal(element[0].value, 'Test Summary', 'Testing detail overview has right summary');

  element.val('Updated Summary');
  element.blur();
});
