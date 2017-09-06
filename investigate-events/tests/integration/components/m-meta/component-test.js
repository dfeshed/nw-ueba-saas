import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';

moduleForComponent('meta', 'Integration | Component | meta-view', {
  integration: true,
  resolver: engineResolverFor('investigate-events')
});

test('it renders with appropriate class name', function(assert) {
  this.render(hbs`{{meta-view}}`);
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

  this.render(hbs`{{meta-view
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

test('it renders the meta values panel by default', function(assert) {
  this.render(hbs`{{meta-view size="default"}}`);
  assert.equal(this.$('.rsa-investigate-meta-values-panel').length, 1, 'Expected to find meta values panel embedded in DOM');
});

test('it renders the total count of meta keys in the group, but only if not zero', function(assert) {
  const emptyGroup = {};
  const nonEmptyGroup = {
    keys: [{
      name: 'foo'
    }, {
      name: 'bar'
    }]
  };
  this.set('group', null);
  this.render(hbs`{{meta-view size="default" group=group}}`);
  assert.equal(this.$('.js-group-keys-count').length, 0, 'Expected keys count to be omitted from DOM for a null group');

  this.set('group', emptyGroup);
  assert.equal(this.$('.js-group-keys-count').length, 0, 'Expected keys count to be omitted from DOM for an empty group');

  this.set('group', nonEmptyGroup);
  assert.equal(this.$('.js-group-keys-count').text(), `(${nonEmptyGroup.keys.length})`, 'Expected to find count of keys in DOM');
});
