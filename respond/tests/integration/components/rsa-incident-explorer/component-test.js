import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('rsa-incident-explorer', 'Integration | Component | Incident Explorer', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

test('it renders with the correct CSS classes', function(assert) {
  const dataHelper = new DataHelper(this.get('redux'));
  dataHelper.initializeIncident();

  this.render(hbs`{{rsa-incident-explorer}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-explorer');
    assert.equal($el.length, 1, 'Expected to find root element in DOM.');

    // all 3 panels are closed by default
    assert.ok($el.hasClass('open-panels-count-0'), 'Expected CSS class for zero open panels.');

    // open each panel and see if CSS classes are updated as expected.
    dataHelper.toggleIncidentEntitiesPanel();
    return wait().then(() => {
      assert.ok($el.hasClass('open-panels-count-1'), 'Expected CSS class for 1 open panel.');
      assert.ok($el.hasClass('is-entities-panel-open'), 'Expected CSS class for open entities panel.');
      assert.notOk($el.hasClass('is-events-panel-open'), 'Expected CSS class for events panel to be missing.');
      assert.notOk($el.hasClass('is-journal-panel-open'), 'Expected CSS class for journal panel to be missing.');

      dataHelper.toggleIncidentEventsPanel();
      return wait().then(() => {
        assert.ok($el.hasClass('open-panels-count-2'), 'Expected CSS class for 2 open panels.');
        assert.ok($el.hasClass('is-entities-panel-open'), 'Expected CSS class for open entities panel.');
        assert.ok($el.hasClass('is-events-panel-open'), 'Expected CSS class for open events panel.');
        assert.notOk($el.hasClass('is-journal-panel-open'), 'Expected CSS class for journal panel to be missing.');

        dataHelper.toggleIncidentJournalPanel();
        return wait().then(() => {
          assert.ok($el.hasClass('open-panels-count-3'), 'Expected CSS class for 3 open panels.');
          assert.ok($el.hasClass('is-entities-panel-open'), 'Expected CSS class for open entities panel.');
          assert.ok($el.hasClass('is-events-panel-open'), 'Expected CSS class for open events panel.');
          assert.ok($el.hasClass('is-journal-panel-open'), 'Expected CSS class for open journal panel.');
        });
      });
    });
  });
});