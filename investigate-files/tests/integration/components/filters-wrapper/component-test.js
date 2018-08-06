import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { render, findAll, click, fillIn, blur } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchSocket } from '../../../helpers/patch-socket';

module('filters-wrapper', 'Integration | Component | Filter Wrapper', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('It renders the rsa-data-filters', async function(assert) {
    await render(hbs`{{filters-wrapper}}`);
    assert.equal(findAll('.rsa-data-filters').length, 1, 'Filters Rendered');
  });

  test('It shows save filter modal on clicking the save button', async function(assert) {
    this.set('showSaveFilterButton', true);
    await render(hbs`{{filters-wrapper showSaveFilterButton=showSaveFilterButton}}`);
    await click(document.querySelector('.save-filter-button button'));
    assert.equal(document.querySelectorAll('#modalDestination .save-search').length, 1, 'Save Filter modal rendered');
  });

  test('apply filter getting called', async function(assert) {
    assert.expect(1);
    this.set('showSaveFilterButton', true);
    patchSocket((method, modelName, query) => {
      assert.equal(query.data.criteria.expressionList['0'].propertyName, 'firstFileName');
    });
    await render(hbs`{{filters-wrapper showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await blur('.file-name-input  input');
  });

  test('apply filter getting called', async function(assert) {
    assert.expect(2);
    this.set('showSaveFilterButton', true);
    await render(hbs`{{filters-wrapper showSaveFilterButton=showSaveFilterButton}}`);
    await fillIn('.file-name-input  input', 'malware.exe');
    await blur('.file-name-input  input');
    patchSocket((method, modelName, query) => {
      assert.equal(query.data.criteria.expressionList['0'].propertyName, 'firstFileName');
      assert.equal(method, 'saveFilter');
    });
    await click(document.querySelector('.save-filter-button button'));
    await fillIn('.custom-filter-name  input', 'test');
    await blur('.custom-filter-name  input');

    await click(document.querySelector('.save-filter button'));

  });


});
