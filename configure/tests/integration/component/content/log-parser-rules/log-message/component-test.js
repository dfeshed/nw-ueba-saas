import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, fillIn, find, findAll, render, triggerKeyEvent } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import { patchSocket, throwSocket } from '../../../../../helpers/patch-socket';

let setState;

module('Integration | Component | Configure - Content - Sample Log Message', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  const sampleLogs = 'May 5 2010 15:55:49 switch : %ACE-4-400000: IDS:1000 IP Option Bad Option List by user admin@test.com ' +
    'from 10.100.229.59 to 224.0.0.22 on port 12345. \n\nApr 29 2010 03:15:34 pvg1-ace02: %ACE-3-251008: Health probe failed ' +
    'for server 218.83.175.75:81, connectivity error: server open timeout (no SYN ACK) domain google.com  with mac 06-00-00-00-00-00.';

  const sampleLogsWithHighlighting = 'May 5 2010 15:55:49 switch : %ACE-4-400000: IDS:1000 IP Option Bad Option List by ' +
    'user admin@test.com<span class=\'highlight_capture_SourceIPorIP:Port\'><span class=\'highlight_literal_SourceIPorIP:Port\'> from' +
    ' </span>10.100.229.59</span>to 224.0.0.22 on<span class=\'highlight_capture_AnyPort\'><span class=\'highlight_literal_AnyPort\'> port' +
    ' </span>12345</span>';

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state = {}) => {
      const fullState = {
        configure: {
          content: {
            logParserRules: {
              parserRules: [{ name: 'Any Port' }],
              selectedParserRuleIndex: 0,
              sampleLogsStatus: 'completed',
              sampleLogs,
              ...state
            }
          }
        }
      };
      patchReducer(this, Immutable.from(fullState));
    };
  });

  const selectors = {
    componentClass: '.sample-log-message',
    sampleLog: '.sample-log-text',
    textArea: 'pre'
  };

  test('it displays the log message text', async function(assert) {
    setState();
    await render(hbs`{{content/log-parser-rules/log-message}}`);
    assert.equal(findAll(selectors.componentClass).length, 1, 'The sample log component appears in the DOM');
    assert.equal(find(selectors.sampleLog).textContent, sampleLogs, 'The content is as expected');
  });

  test('highlighting for a rule that is currently selected gets the "is-selected" class, others get highlight-capture ' +
    'and highlight-literal classes', async function(assert) {
    setState({ sampleLogs: sampleLogsWithHighlighting });
    await render(hbs`{{content/log-parser-rules/log-message}}`);
    assert.equal(findAll('.highlight-capture').length, 2);
    assert.equal(findAll('.highlight-literal').length, 2);
    assert.equal(findAll('.is-selected').length, 2, 'The Any Port spans are given the classes is-selected');
  });

  test('highlighting for a rule that is currently selected gets the "is-selected" class, others get highlight-capture ' +
    'and highlight-literal classes', async function(assert) {
    setState({ sampleLogs: sampleLogsWithHighlighting, sampleLogsStatus: 'wait' });
    await render(hbs`{{content/log-parser-rules/log-message}}`);
    assert.equal(findAll('aside .rsa-loader').length, 1, 'The loading spinner appears when the sampleLogsStatus is wait');
  });

  test('keyup events trigger a highlighting call', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    setState();
    await render(hbs`{{content/log-parser-rules/log-message keyUpDelay=100 }}`);
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'highlight');
      assert.equal(modelName, 'log-parser-rules');
      assert.deepEqual(query, {
        logs: {
          logs: ['test']
        },
        parserRules: [{
          name: 'Any Port'
        }]
      });
      done();
    });
    await click('pre');
    await fillIn('pre', 'test');
    triggerKeyEvent('pre', 'keyup', 65);
  });

  test('An error is shown if the highlight call fails', async function(assert) {
    setState();
    await render(hbs`{{content/log-parser-rules/log-message keyUpDelay=100 }}`);
    await click('pre');
    await fillIn('pre', 'test');
    throwSocket();
    await triggerKeyEvent('pre', 'keyup', 65);
    assert.equal(findAll('aside .error').length, 1, 'The error message is found');
  });
});
