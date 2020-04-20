import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll } from '@ember/test-helpers';
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
  activePage: '.active',
  leftAction: '.left button',
  rightAction: '.right button',
  leftActionDisabled: '.left.is-disabled',
  rightActionDisabled: '.right.is-disabled',
  addAction: '.add'
};

let setState;

module('Integration | Component | springboard-pager', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('springboard')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it renders the springboard pager', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    this.set('pagerAction', () => {
      assert.ok(true);
    });
    await render(hbs`<SpringboardPager @pagerAction={{this.pagerAction}}/>`);
    assert.dom(selectors.pager).exists('Springboard pager exists');
  });
  test('it renders the springboard pager active pages', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    await render(hbs`<SpringboardPager/>`);
    assert.dom(selectors.page).exists({ count: 6 }, '6 pages present in the pager');
    assert.equal(findAll(selectors.activePage).length > 1, true, 'Active pages dynamic count present in the pager');
  });
  test('Springboard pager initially left action disable and right action enable ', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1')
      .springboards(SIMPLE_DATA_SET)
      .pagerPosition(0)
      .build();
    this.set('pagerAction', () => {
      assert.ok(true);
    });
    await render(hbs`<SpringboardPager @pagerAction={{this.pagerAction}}/>`);
    assert.dom(selectors.leftActionDisabled).exists({ count: 1 }, 'Left action disabled');
    assert.dom(selectors.rightActionDisabled).exists({ count: 0 }, 'right action not disabled');
  });
  test('Springboard pager on click right action right and left actions are enabled ', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    this.set('pagerAction', () => {
      assert.ok(true);
    });
    await render(hbs`<SpringboardPager @pagerAction={{this.pagerAction}}/>`);
    await click(selectors.rightAction);
    assert.dom(selectors.leftActionDisabled).exists({ count: 0 }, 'Left action not disabled');
    assert.dom(selectors.rightActionDisabled).exists({ count: 0 }, 'right action not disabled');
  });
  test('Springboard pager right action disabled when moved to extreme right ', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    const [data] = SIMPLE_DATA_SET;
    this.set('data', data.widgets);
    this.set('pagerAction', () => {
      assert.ok(true);
    });
    await render(hbs`<SpringboardPager @pagerAction={{this.pagerAction}}/>`);
    await click(selectors.rightAction);
    await click(selectors.rightAction);
    await click(selectors.rightAction);
    await click(selectors.rightAction);
    await click(selectors.rightAction);
    assert.dom(selectors.leftActionDisabled).exists({ count: 0 }, 'Left action disabled');
    assert.dom(selectors.rightActionDisabled).exists({ count: 1 }, 'right action disabled');
  });
  test('Springboard pager add lead page button present if flag is true ', async function(assert) {
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(SIMPLE_DATA_SET).build();
    const [data] = SIMPLE_DATA_SET;
    this.set('data', data.widgets);
    await render(hbs`
    <div class="springboard-content"></div>
    <SpringboardPager @componentSelectorClass='springboard-content' @showAddLeadAction="true"/>`);
    assert.dom(selectors.addAction).exists({ count: 1 }, 'Add lead action presents');
  });
  test('Springboard pager on click right action right and left actions are disabled when widget count is less than 4 ', async function(assert) {
    const data = [
      {
        id: 'spring_board_1',
        name: 'Analyst Springboard',
        widgets: [
          {
            columnIndex: 1,
            widget: {
              name: 'Top Risky Hosts 1',
              leadType: 'hosts',
              leadCount: 10,
              content: [
                {
                  type: 'chart',
                  chartType: 'donut-chart',
                  aggregate: {
                    column: ['hostOsType'],
                    type: 'COUNT'
                  },
                  extraCss: 'flexi-fit'
                },
                {
                  type: 'table',
                  columns: ['hostName', 'score', 'hostOsType'],
                  sort: {
                    keys: ['score'],
                    descending: true
                  }
                }
              ]
            }
          },
          {
            columnIndex: 2,
            widget: {
              name: 'Top Risky Hosts 2',
              leadType: 'hosts',
              leadCount: 10,
              content: [
                {
                  type: 'chart',
                  chartType: 'donut-chart',
                  aggregate: {
                    column: ['hostOsType'],
                    type: 'COUNT'
                  },
                  extraCss: 'flexi-fit'
                },
                {
                  type: 'table',
                  columns: ['hostName', 'score', 'hostOsType'],
                  sort: {
                    keys: ['score'],
                    descending: true
                  }
                }
              ]
            }
          }]
      }];
    new DataHelper(setState).activeSpringboardId('spring_board_1').springboards(data).build();
    await render(hbs`
    <div class="springboard-content"></div>
    <SpringboardPager @componentSelectorClass='springboard-content'/>`);
    await click(selectors.rightAction);
    assert.dom(selectors.leftActionDisabled).exists({ count: 1 }, 'Left action disabled');
    assert.dom(selectors.rightActionDisabled).exists({ count: 1 }, 'right action disabled');
  });

});