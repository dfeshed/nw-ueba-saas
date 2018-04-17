import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { selectAll } from 'd3-selection';
import engineResolverFor from '../../../helpers/engine-resolver';

module('Integration | Component | process-tree', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });


  test('it renders the process tree', async function(assert) {
    const treeData = {
      'processName': 'Evil.exe',
      'id': 1,
      'riskScore': 25,
      'children': [
        {
          'processName': 'cmd.exe',
          'id': 2,
          'riskScore': 25,
          'children': [
            {
              'processName': 'notepad.exe',
              'id': 3,
              'riskScore': 25,
              'children': []
            },
            {
              'processName': 'winword.exe',
              'id': 4,
              'riskScore': 87
            }
          ]
        },
        {
          'processName': 'cmd.exe',
          'riskScore': 15,
          'id': 6,
          'children': []
        },
        {
          'processName': 'evil-new.exe',
          'riskScore': 100,
          'id': 9
        },
        {
          'processName': 'cmd.exe',
          'riskScore': 25,
          'id': 8
        }
      ]
    };

    this.set('treeData', treeData);
    await render(hbs`{{process-tree treeData=treeData}}`);
    assert.equal(findAll('.process').length, 5, 'Expected to render 5 nodes');
  });

  test('it should expand the node on click', async function(assert) {
    const newData = {
      'processName': 'Evil.exe',
      'id': 1,
      'riskScore': 25,
      'children': [
        {
          'processName': 'cmd.exe',
          'id': 2,
          'riskScore': 25,
          'children': [
            {
              'processName': 'notepad.exe',
              'id': 3,
              'riskScore': 25,
              'children': []
            },
            {
              'processName': 'winword.exe',
              'id': 4,
              'riskScore': 87
            }
          ]
        }
      ]
    };
    this.set('treeData', newData);
    await render(hbs`{{process-tree treeData=treeData}}`);
    await selectAll('.process:nth-of-type(2)').dispatch('click');
    assert.equal(findAll('.process').length, 4, 'Expected to render 7 nodes');
  });
});
