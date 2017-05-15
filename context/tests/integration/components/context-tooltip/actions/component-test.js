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
      assert.equal(this.$('.action').length, 2, 'Expected to find 2 action menu options');
      // Click the second option, that's the Add To List option
      this.$('.action').eq(1).trigger('click');
      return wait();
    });
});

test('it only shows the Pivot to Endpoint link for IPs, HOSTs & MAC addresses', function(assert) {
  this.setProperties({
    entityType: 'IP',
    entityId: '10.20.30.40'
  });
  this.render(hbs`{{context-tooltip/actions 
    entityType=entityType 
    entityId=entityId}}`);
  return wait()
    .then(() => {
      assert.ok(this.$('.js-test-endpoint-link').length, 'Expected to find endpoint link for IP');
      this.setProperties({
        entityType: 'HOST',
        entityId: 'MACHINE1'
      });
      return wait();
    })
    .then(() => {
      assert.ok(this.$('.js-test-endpoint-link').length, 'Expected to find endpoint link for HOST');
      this.setProperties({
        entityType: 'MAC_ADDRESS',
        entityId: 'aa:bb:cc:dd'
      });
      return wait();
    })
    .then(() => {
      assert.ok(this.$('.js-test-endpoint-link').length, 'Expected to find endpoint link for MAC address');
      this.setProperties({
        entityType: 'USER',
        entityId: 'username1'
      });
      return wait();
    })
    .then(() => {
      assert.notOk(this.$('.js-test-endpoint-link').length, 'Expected to NOT find endpoint link for user');
      this.setProperties({
        entityType: 'IP',
        entityId: null
      });
      return wait();
    })
    .then(() => {
      assert.notOk(this.$('.js-test-endpoint-link').length, 'Expected to NOT find endpoint link for empty IP');
    });
});
