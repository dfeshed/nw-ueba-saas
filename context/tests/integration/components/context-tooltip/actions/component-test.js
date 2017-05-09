import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';

moduleForComponent('context-tooltip', 'Integration | Component | context tooltip actions', {
  integration: true
});

const entityType = 'IP';
const entityId = '10.20.30.40';

test('it renders an actionsList based on the entityType', function(assert) {
  assert.expect(6);

  const hideAction = () => {
    assert.ok(true, 'hideAction was invoked');
  };
  const addToListAction = (entity = {}) => {
    assert.ok(true, 'addToListAction was invoked');
    assert.equal(entity.id, entityId, 'Expected addToListAction to receive entity object.');
    assert.equal(entity.type, entityType, 'Expected addToListAction to receive entity object.');
  };

  this.setProperties({
    entityType,
    entityId,
    hideAction,
    addToListAction
  });

  this.render(hbs`{{context-tooltip/actions 
    entityType=entityType 
    entityId=entityId 
    hideAction=hideAction 
    addToListAction=addToListAction}}`);

  return wait()
    .then(() => {
      assert.equal(this.$('.rsa-context-tooltip-actions').length, 1, 'Expected to find root DOM node');
      assert.equal(this.$('.action').length, 1, 'Expected to find one action menu option');
      this.$('.action').trigger('click');
      return wait();
    });
});
