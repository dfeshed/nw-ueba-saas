import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('contet-filter/filter-trigger', 'Integration | Component | filter trigger', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it should render the button with passed label', async function(assert) {

    this.set('filterLabel', 'size');

    await render(hbs`{{content-filter/filter-trigger filterLabel=filterLabel}}`);
    assert.equal(find('.filter-label').textContent.trim(), 'size');
  });

  test('it should show remove button', async function(assert) {

    this.setProperties({ filterLabel: 'size', showRemoveButton: true, removeAction: () => {} });

    await render(hbs`{{content-filter/filter-trigger filterLabel=filterLabel showRemoveButton=showRemoveButton removeAction=removeAction}}`);
    assert.equal(find('.filter-label').textContent.trim(), 'size');
    assert.ok(findAll('.rsa-icon-remove-circle-2-filled').length, 'Expected to find close icon in DOM.');
  });
});