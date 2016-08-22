import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('rsa-investigate/meta', 'Integration | Component | rsa investigate/meta', {
  integration: true
});

test('it renders with appropriate class name', function(assert) {
  this.render(hbs`{{rsa-investigate/meta}}`);
  assert.equal(this.$('.rsa-investigate-meta').length, 1, 'Expected to find DOM element');
});

test('it invokes callbacks when clicking its UI elements for setting size', function(assert) {
  assert.expect(9);

  const minSizeAction = () => {
    this.set('size', 'min');
    assert.ok(true, 'Expected minSizeAction to be invoked.');
  };
  const maxSizeAction = () => {
    this.set('size', 'max');
    assert.ok(true, 'Expected maxSizeAction to be invoked.');
  };
  const defaultSizeAction = () => {
    this.set('size', 'default');
    assert.ok(true, 'Expected maxSizeAction to be invoked.');
  };

  this.setProperties({
    size: 'default',
    defaultSizeAction,
    minSizeAction,
    maxSizeAction
  });

  this.render(hbs`{{rsa-investigate/meta
    size=size
    defaultSizeAction=defaultSizeAction
    minSizeAction=minSizeAction
    maxSizeAction=maxSizeAction}}`);

  assert.equal(this.$('.size.min').length, 1, 'Expected to find min size trigger');
  assert.equal(this.$('.size.max').length, 1, 'Expected to find max size trigger');

  this.$('.size.min').click();
  assert.equal(this.get('size'), 'min', 'Expected size to change.');

  assert.equal(this.$('.size.default').length, 1, 'Expected to find default size trigger');
  this.$('.size.default').click();
  assert.equal(this.get('size'), 'default', 'Expected size to change.');

  this.$('.size.max').click();
  assert.equal(this.get('size'), 'max', 'Expected size to change.');
});
