import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';

const metaItems = [
  [ 'size', 62750 ],
  [ 'payload', 56460 ],
  [ 'medium', 1 ],
  [ 'eth.src', '70:56:81:9A:94:DD' ],
  [ 'eth.dst', '10:0D:7F:75:C4:C8' ]
];

let setState;
module('Integration | Component | recon-meta-content', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => patchReducer(this, state);
    initialize(this.owner);
  });


  test('several meta items render correctly', async function(assert) {
    new ReduxDataHelper(setState).meta(metaItems).build();

    await render(hbs`{{recon-meta-content}}`);

    assert.equal(findAll('.recon-meta-content-item').length, 5);
  });

  test('renders error correctly', async function(assert) {
    new ReduxDataHelper(setState).isMetaError().build();
    await render(hbs`{{recon-meta-content}}`);
    assert.equal(findAll('.rsa-panel-message .message')[0].textContent.trim(), 'An unexpected error has occurred attempting to retrieve this data. If further details are available, they can be found in the console. code: 1 - UNHANDLED_ERROR');
  });

  test('zero meta items render correctly', async function(assert) {
    new ReduxDataHelper(setState).meta([]).build();

    await render(hbs`{{recon-meta-content}}`);

    assert.equal(findAll('.recon-meta-content-item').length, 0);

  });

  test('meta grouping drop down with default/none grouping type should rendered', async function(assert) {
    new ReduxDataHelper(setState).meta(metaItems).build();

    await render(hbs`{{recon-meta-content}}`);

    assert.equal(find('.meta-grouping .meta-grouping-label').textContent, 'Organize by', 'grouping drop down label name');

    assert.equal(find('.meta-grouping .meta-grouping-drop-down').textContent.trim(), 'Default', 'default grouping type');

    assert.notOk(find('.recon-meta-content-group .meta-content-section'), 'Grouping of metas is not done on default grouping type');

    assert.equal(findAll('.recon-meta-content-group .recon-meta-content-item').length, 5, '5 metas are displayed');
  });

  test('meta grouping by alphabet is rendered when A-Z is selected from drop down', async function(assert) {
    new ReduxDataHelper(setState).meta(metaItems).build();

    await render(hbs`{{recon-meta-content}}`);

    await clickTrigger('.meta-grouping .meta-grouping-drop-down');

    assert.equal(find('.meta-grouping .meta-grouping-drop-down').textContent.trim(), 'Default', 'default grouping type');

    assert.equal(findAll('.ember-power-select-options li.ember-power-select-option').length, 2, 'There are 2 options available');

    await selectChoose('.meta-grouping .meta-grouping-drop-down', '.ember-power-select-option', 1);

    assert.equal(find('.meta-grouping .meta-grouping-drop-down').textContent.trim(), 'Alphabet (A-Z)', 'default grouping type');

    assert.equal(findAll('.recon-meta-content-group .meta-content-section').length, 4, '4 grouping sections are displayed');

    assert.equal(findAll('.recon-meta-content-item').length, 5, '5 metas are displayed');
  });
});
