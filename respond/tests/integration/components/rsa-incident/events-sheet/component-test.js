import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;
const trim = (text) => text.replace(/\s\s+/g, ' ').trim();

module('Integration | Component | Incident Events Sheet', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('should display selectedIndicatorName when incident selection is truthy and of type storyPoint', async function(assert) {
    setState({
      respond: {
        incident: {
          selection: {
            type: 'storyPoint',
            ids: [ 'alert1' ]
          }
        },
        storyline: {
          storyline: [
            {
              id: 'alert1',
              alert: { numEvents: 10, name: 'foo' },
              items: [
                {
                  id: 'abc123'
                }
              ]
            }
          ]
        }
      }
    });

    await render(hbs`{{rsa-incident/events-sheet}}`);

    const selector = '[test-id=indicatorLabel]';
    assert.equal(findAll(selector).length, 1, 'Expected to find indicator label element in DOM.');
    assert.equal(trim(find(selector).textContent), 'in foo', 'Expected selected indicator alert name.');
  });

  test('should display selectedIndicatorName and indicator count when incident selection is truthy but available ids > 1', async function(assert) {
    setState({
      respond: {
        incident: {
          selection: {
            type: 'storyPoint',
            ids: [ 'alert1', 'alert2' ]
          }
        },
        storyline: {
          storyline: [
            {
              id: 'alert1',
              alert: { numEvents: 10, name: 'foo' },
              items: [
                {
                  id: 'abc123'
                }
              ]
            }
          ]
        }
      }
    });

    await render(hbs`{{rsa-incident/events-sheet}}`);

    const selector = '[test-id=indicatorLabel]';
    assert.equal(findAll(selector).length, 1, 'Expected to find indicator label element in DOM.');
    assert.equal(trim(find(selector).textContent), 'in 2 indicators', 'Expected indicator count.');
  });

  test('should not display selectedIndicatorName when incident selection is truthy but storyline is empty array', async function(assert) {
    setState({
      respond: {
        incident: {
          selection: {
            type: 'storyPoint',
            ids: [ 'alert1' ]
          }
        },
        storyline: {
          storyline: []
        }
      }
    });

    await render(hbs`{{rsa-incident/events-sheet}}`);

    const selector = '[test-id=indicatorLabel]';
    assert.equal(findAll(selector).length, 0, 'Expected not to find indicator label element in DOM.');
  });

  test('should not display selectedIndicatorName when incident selection is truthy but storyline is undefined', async function(assert) {
    setState({
      respond: {
        incident: {
          selection: {
            type: 'storyPoint',
            ids: [ 'alert1' ]
          }
        },
        storyline: {
          storyline: undefined
        }
      }
    });

    await render(hbs`{{rsa-incident/events-sheet}}`);

    const selector = '[test-id=indicatorLabel]';
    assert.equal(findAll(selector).length, 0, 'Expected not to find indicator label element in DOM.');
  });
});
