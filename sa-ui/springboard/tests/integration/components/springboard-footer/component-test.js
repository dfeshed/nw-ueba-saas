import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import DataHelper from '../../../helpers/data-helper';
import { SIMPLE_DATA_SET } from './test-data';

const selectors = {
  footer: '.springboard-footer',
  pager: '.springboard-pager',
  action: '.action.rsa-form-button-wrapper',
  page: '.page',
  activePage: '.active'
};

let setState;

module('Integration | Component | springboard-footer', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('springboard')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it renders the springboard footer', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    await render(hbs`<SpringboardFooter/>`);
    assert.dom(selectors.footer).exists('Footer section is present');
    assert.dom(selectors.pager).exists('Footer section is has pager');
  });
  test('it renders the springboard footer with pages', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    await render(hbs`<SpringboardFooter/>`);
    assert.dom(selectors.page).exists({ count: 6 }, '6 pages present in the pager');
    assert.dom(selectors.action).exists({ count: 2 }, 'Two arrow actions present in the pager');
    assert.equal(findAll(selectors.activePage).length > 1, true, 'Active pages present in the pager');
  });
  test('it renders the springboard pager active and default pages', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    await render(hbs`<SpringboardFooter/>`);
    assert.dom(selectors.page).exists({ count: 6 }, '6 pages present in the pager');
    assert.equal(findAll(selectors.activePage).length > 1, true, 'Active pages present in the pager');
  });

});
