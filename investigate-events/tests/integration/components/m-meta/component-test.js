import { moduleForComponent, test, skip } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('meta', 'Integration | Component | meta-view', {
  integration: true,
  resolver: engineResolverFor('investigate-events'),
  beforeEach() {
    this.inject.service('redux');
  }
});

test('it renders with appropriate class name', function(assert) {
  this.render(hbs`{{meta-view}}`);
  assert.equal(this.$('.rsa-investigate-meta').length, 1, 'Expected to find DOM element');
});

test('it invokes callbacks when clicking its UI elements for setting size', function(assert) {
  assert.expect(9);

  const dh = new DataHelper(this.get('redux')).initializeData();

  const minSizeAction = () => {
    dh.setMetaPanelSize('min');
    assert.ok(true, 'Expected minSizeAction to be invoked.');
  };
  const maxSizeAction = () => {
    dh.setMetaPanelSize('max');
    assert.ok(true, 'Expected maxSizeAction to be invoked.');
  };
  const defaultSizeAction = () => {
    dh.setMetaPanelSize('default');
    assert.ok(true, 'Expected maxSizeAction to be invoked.');
  };

  this.setProperties({
    defaultSizeAction,
    minSizeAction,
    maxSizeAction
  });

  this.render(hbs`{{meta-view
    defaultSizeAction=defaultSizeAction
    minSizeAction=minSizeAction
    maxSizeAction=maxSizeAction}}`);

  assert.equal(this.$('.size.min').length, 1, 'Expected to find min size trigger');
  assert.equal(this.$('.size.max').length, 1, 'Expected to find max size trigger');

  this.$('.size.min').click();
  assert.ok(this.$('.meta-size-min'), 'Expected size to be "min".');

  assert.equal(this.$('.size.default').length, 1, 'Expected to find default size trigger');

  this.$('.size.default').click();
  assert.ok(this.$('.meta-size-default'), 'Expected size to be "default".');

  this.$('.size.max').click();
  assert.ok(this.$('.meta-size-max'), 'Expected size to be "max".');
});

test('it renders the meta values panel by default', function(assert) {
  this.render(hbs`{{meta-view size="default"}}`);
  assert.equal(this.$('.rsa-investigate-meta-values-panel').length, 1, 'Expected to find meta values panel embedded in DOM');
});

skip('it renders the total count of meta keys in the group, but only if not zero', function(assert) {
  const emptyGroup = {};
  const nonEmptyGroup = {
    keys: [
      { name: 'foo' },
      { name: 'bar' }
    ]
  };
  this.set('group', null);
  this.render(hbs`{{meta-view group=group}}`);
  assert.equal(this.$('.js-group-keys-count').length, 0, 'Expected keys count to be omitted from DOM for a null group');

  this.set('group', emptyGroup);
  assert.equal(this.$('.js-group-keys-count').length, 0, 'Expected keys count to be omitted from DOM for an empty group');

  // TODO - this test breaks when you set `groups` with the error
  // (0 , _observer.observer) is not a function
  this.set('group', nonEmptyGroup);
  assert.equal(this.$('.js-group-keys-count').text(), `(${nonEmptyGroup.keys.length})`, 'Expected to find count of keys in DOM');
});
