import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import SELECTORS from '../../selectors';

let setState;

module('Integration | Component | list footer', function(hooks) {

  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const originalList = [ { id: '1', name: 'foo' }, { id: '2', name: 'bar' }];
  const stateLocation1 = 'listManager';
  const listName1 = 'Meta Groups';

  const {
    listFooter,
    helpIcon
  } = SELECTORS;

  test('renders footer for list with correct components', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .list(originalList)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);

    await render(hbs`{{list-manager/list-manager-container/list-footer
      stateLocation=stateLocation
      }}`);

    assert.ok(find(listFooter));
    assert.notOk(find(helpIcon), 'Help Icon not available');

    const buttons = findAll(`${listFooter} button`);
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);
  });

  test('renders help icon if provided', async function(assert) {
    assert.expect(3);
    const helpId1 = { topicId: 'foo', moduleId: 'bar' };
    new ReduxDataHelper(setState)
      .stateLocation(stateLocation1)
      .list(originalList)
      .helpId(helpId1)
      .listName(listName1)
      .build();
    this.set('stateLocation', stateLocation1);

    await render(hbs`{{list-manager/list-manager-container/list-footer
      stateLocation=stateLocation
      }}`);

    assert.ok(find(listFooter));
    assert.ok(find(`${helpIcon} button`), 'Help Icon available');

    const buttons = findAll(`${listFooter} button`);
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);
  });
});
