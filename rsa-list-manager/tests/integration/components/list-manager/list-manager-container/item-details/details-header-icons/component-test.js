import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | item details - details header icons', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const detailsHeaderIcons = '.details-header-icons';
  const helpIcon = '.list-help-icon';
  const listLocation1 = 'listManager';
  const helpId1 = { moduleId: 'foo', topicId: '123' };
  const helpId2 = { moduleId: 'bar' };

  test('renders icons with correct class', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .listName('Foos')
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.ok(find(detailsHeaderIcons), 'component shall have correct class');
  });

  test('renders help icon if hasContextualHelp', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .listName('Foos')
      .helpId(helpId1)
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.equal(findAll(helpIcon).length, 1, 'shall render one help icon if hasContextualHelp is true');
  });

  test('shall not render help icon if hasContextualHelp is false', async function(assert) {
    new ReduxDataHelper(setState)
      .stateLocation(listLocation1)
      .listName('Foos')
      .helpId(helpId2)
      .build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-header-icons
      stateLocation=stateLocation
    }}`);

    assert.notOk(find(helpIcon), 'shall not render help icon if hasContextualHelp is false');
  });
});
