import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';

moduleForComponent('contet-filter/filter-trigger', 'Integration | Component | filter trigger', {
  integration: true,
  resolver: engineResolverFor('investigate-files'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('it should render the button with passed label', function(assert) {

  this.set('filterLabel', 'size');

  this.render(hbs`{{content-filter/filter-trigger filterLabel=filterLabel}}`);

  return wait().then(() => {
    assert.equal(this.$('.filter-label').text().trim(), 'size');
  });
});

test('it should show remove button', function(assert) {

  this.setProperties({ filterLabel: 'size', showRemoveButton: true, removeAction: () => {} });

  this.render(hbs`{{content-filter/filter-trigger filterLabel=filterLabel showRemoveButton=showRemoveButton removeAction=removeAction}}`);

  return wait().then(() => {
    assert.equal(this.$('.filter-label').text().trim(), 'size');
    const $icon = this.$('.rsa-icon-remove-circle-2-filled');
    assert.ok($icon.length, 'Expected to find close icon in DOM.');
  });
});
