import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | rsa-incident/inspector', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    localStorage.clear();
    initialize(this.owner);
    setState = (initialState) => {
      patchReducer(this, initialState);
    };
  });

  test('component renders', async function(assert) {
    setState({
      respond: {
        riac: {
          isRiacEnabled: false,
          adminRoles: ['Administrators']
        },
        incident: {
          info: {
            status: 'ASSIGNED',
            name: 'foobar',
            id: 'INC-123'
          }
        }
      }
    });
    await render(hbs`{{rsa-incident/inspector}}`);
    assert.ok(find('.rsa-incident-inspector__toolbar'), 'Toolbar renders');
    assert.ok(find('.rsa-incident-inspector__body'), 'body renders');
    assert.equal(findAll('.rsa-incident-inspector__toolbar .rsa-tab').length, 3, '3 tabs are visible');

    assert.equal(find('.incident-inspector-header .id').textContent.trim(), 'INC-123', 'correct incident ID');
    assert.equal(find('.incident-inspector-header .name .editable-field__value').textContent.trim(), 'foobar', 'correct incident name');
  });

  test('find-related tab does not appear if RIAC permissions dont allow it', async function(assert) {
    setState({
      respond: {
        riac: {
          isRiacEnabled: true,
          adminRoles: ['foo']
        }
      }
    });
    await render(hbs`{{rsa-incident/inspector}}`);
    assert.equal(findAll('.rsa-incident-inspector__toolbar .rsa-tab').length, 2, 'find-related tab is hidden');
  });
});
