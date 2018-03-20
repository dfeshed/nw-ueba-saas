import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, click, triggerEvent } from '@ember/test-helpers';

import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { patchSocket } from '../../../../helpers/patch-socket';

let i18n;

module('Integration | Component | content filter/list filter', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });
  hooks.beforeEach(function() {
    i18n = this.owner.lookup('service:i18n');
  });

  const defaultConfig = {
    selected: [],
    propertyName: 'signature',
    panelId: 'list-filter-test',
    label: 'investigateFiles.fields.signature.features',
    options: ['signed', 'unsigned', 'microsoft'],
    expression: {
    }
  };

  test('it renders list filter', async function(assert) {
    this.setProperties({
      i18n,
      config: defaultConfig
    });
    await render(hbs`{{content-filter/list-filter config=config i18n=i18n}}`);
    assert.equal(findAll('.filter-trigger-button').length, 1, 'List filter button exists');
    assert.equal(find('.filter-trigger-button span').textContent.trim(), 'Signature: All', 'Label is displayed');
  });

  test('show filter options on trigger', async function(assert) {
    this.setProperties({
      i18n,
      config: defaultConfig
    });
    await render(hbs`{{content-filter/list-filter config=config i18n=i18n}}`);
    await click('.filter-trigger-button');
    assert.equal(findAll('.list-filter__content').length, 1, 'List filter content displayed');
    assert.equal(findAll('.list-filter__content li').length, 3, 'List filter options displayed');
  });

  test('it parse the given expression correctly for display', async function(assert) {
    const expression = {
      propertyName: 'signature',
      propertyValues: [{ value: 'signed' }],
      restrictionType: 'IN'
    };
    this.setProperties({
      i18n,
      config: { ...defaultConfig, expression }
    });
    await render(hbs`{{content-filter/list-filter config=config i18n=i18n}}`);
    await click('.filter-trigger-button');
    assert.equal(findAll('.list-filter__content').length, 1, 'List filter content displayed');
    assert.equal(findAll('.list-filter__content .rsa-form-checkbox:checked').length, 1, '1 option checked in the list option');
  });

  test('it should send correct expression for filtering on update', async function(assert) {
    assert.expect(2);
    const expression = {
      propertyName: 'signature',
      propertyValues: [{ value: 'signed' }],
      restrictionType: 'IN'
    };
    this.setProperties({
      i18n,
      config: { ...defaultConfig, expression }
    });

    patchSocket((method, model, query) => {
      assert.equal(method, 'search');
      assert.deepEqual(query.data.criteria, undefined);
    });

    await render(hbs`{{content-filter/list-filter config=config i18n=i18n}}`);
    await click('.filter-trigger-button');
    const ele = find('.list-filter__content input:nth-of-type(1)');
    await triggerEvent(ele, 'change');
  });
});
