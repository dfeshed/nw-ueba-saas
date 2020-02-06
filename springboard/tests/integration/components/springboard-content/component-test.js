import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import Immutable from 'seamless-immutable';
import { hbs } from 'ember-cli-htmlbars';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import DataHelper from '../../../helpers/data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import { SIMPLE_DATA_SET } from './test-data';

const selectors = {
  content: '.springboard-content',
  widget: '.springboard-content__widget'
};
let setState;

module('Integration | Component | springboard-content', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('springboard')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it renders the springboard layout component', async function(assert) {
    await render(hbs`<SpringboardContent/>`);
    assert.dom(selectors.content).exists('Main section is present');
  });

  test('it should render widget layout based on config', async function(assert) {
    new DataHelper(setState).activeSpringboardId('2').springboards(SIMPLE_DATA_SET).build();
    await render(hbs`<SpringboardContent/>`);
    assert.dom(selectors.widget).exists({ count: 4 }, 'Widgets sections are rendered');
  });
});
