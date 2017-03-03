import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import DataHelper from '../../../helpers/data-helper';

moduleForComponent('rsa-incident-journal', 'Integration | Component | Incident Journal', {
  integration: true,
  resolver: engineResolverFor('respond'),
  setup() {
    this.inject.service('redux');
  }
});

test('it renders', function(assert) {
  new DataHelper(this.get('redux')).fetchIncidentDetails();
  this.render(hbs`{{rsa-incident-journal}}`);
  return wait().then(() => {
    const $el = this.$('.rsa-incident-journal');
    assert.equal($el.length, 1, 'Expected to find journal root element in DOM.');

    const $entries = $el.find('.rsa-incident-journal-entry');
    assert.ok($entries.length, 'Expected to find at least one data table body row element in DOM.');

    const $editor = $el.find('.editor');
    assert.ok($editor.length, 'Expected to find editor for adding new entry in DOM.');
  });
});