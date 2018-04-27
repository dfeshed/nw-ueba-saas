import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import customFilterData from '../../../state/custom-filter-data';
import { render, find, findAll, triggerEvent } from '@ember/test-helpers';

module('custom-filters/custom-filter-list', 'Integration | Component | custom filters/custom filter list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });

  test('Title attribute & text of the custom filter passed should be rendered', async function(assert) {
    assert.expect(3);
    this.set('filter', customFilterData.fileFilters.data[0]);
    this.set('applyCustomFilter', () => {});
    await render(hbs`{{custom-filters/custom-filter-list filter=filter applyCustomFilter=applyCustomFilter}}`);

    assert.equal(findAll('.filter-list__item-label').length, 1, 'Length of filter list item rendered');
    assert.equal(find('.filter-list__item-label').textContent.trim(), 'entropy_less_than_3', 'Text of the filter list item');
    assert.equal(find('.filter-list__item-label').title, 'entropy_less_than_3', 'Title attribute of filter list item');
  });

  test('Computed property upon setting custom-filter list-item', async function(assert) {
    this.set('filter', customFilterData.fileFilters.data[0]);
    this.set('activeFilter', '5a6830ec3f11d6700d9ca761');
    this.set('reset', false);
    this.set('isSystemFilter', false);
    this.set('applyCustomFilter', 'applyCustomFilter');
    await render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);

    assert.equal(document.querySelector('.filter-list__item').classList.contains('is-active'), true, 'Computed property calculated correctly upon setting custom-filter list-item');
  });

  test('Computed property upon resetting system-filter list-item', async function(assert) {
    this.set('filter', customFilterData.fileFilters.data[0]);
    this.set('activeFilter', '5a6830ec3f11d6700d9ca761');
    this.set('reset', true);
    this.set('isSystemFilter', false);
    this.set('applyCustomFilter', 'applyCustomFilter');
    await render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);

    assert.equal(document.querySelector('.filter-list__item').classList.contains('is-active'), false, 'Computed property calculated correctly upon resetting custom-filter list-item');
  });

  test('Computed property when custom-filter id & active filter id is different', async function(assert) {
    this.set('filter', customFilterData.fileFilters.data[0]);
    this.set('activeFilter', '5a6830ec3f11d6700d9ca762');
    this.set('reset', false);
    this.set('isSystemFilter', false);
    this.set('applyCustomFilter', 'applyCustomFilter');
    await render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);
    assert.equal(document.querySelector('.filter-list__item').classList.contains('is-active'), false, 'Computed property calculated correctly when active filter and passed filter id are different');
  });

  test('Computed property when system filter boolean is set for custom filter', async function(assert) {
    this.set('filter', customFilterData.fileFilters.data[0]);
    this.set('activeFilter', '5a6830ec3f11d6700d9ca761');
    this.set('reset', false);
    this.set('isSystemFilter', true);
    this.set('applyCustomFilter', 'applyCustomFilter');
    await render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);

    assert.equal(document.querySelector('.filter-list__item').classList.contains('is-active'), false, 'Computed property calculated correctly when system filter boolean is set for custom filter');
  });

  test('Mouse hover on custom filter', async function(assert) {
    assert.expect(2);
    this.set('filter', customFilterData.fileFilters.data[0]);
    this.set('applyCustomFilter', 'applyCustomFilter');
    await render(hbs`{{custom-filters/custom-filter-list filter=filter applyCustomFilter=applyCustomFilter}}`);

    await triggerEvent('.filter-list__item-label', 'mouseover');
    assert.equal(document.querySelector('.filter-list__item').classList.contains('is-hovering'), true, 'Mouse entered/hovered on custom filter');

    await triggerEvent('.filter-list__item-label', 'mouseout');
    assert.equal(document.querySelector('.filter-list__item').classList.contains('is-hovering'), false, 'Mouse unhovered on custom filter');
  });
});