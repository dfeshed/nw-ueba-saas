import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled } from '@ember/test-helpers';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { incidentDetails } from '../../../../data/data';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';

let setState;

module('Integration | Component | Incident Overview', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it renders', async function(assert) {
    this.set('incidentDetails', incidentDetails);
    await render(hbs`{{rsa-incident/overview info=incidentDetails infoStatus='completed'}}`);
    return settled().then(() => {
      const $el = this.$('.rsa-incident-overview');
      assert.equal($el.length, 1, 'Expected to find overview root element in DOM.');

      [ '.created', '.by', '.sources', '.catalyst-count' ].forEach((selector) => {
        const $field = $el.find(`${selector} span`);
        assert.ok($field.text().trim(), `Expected to find non-empty field element in DOM for: ${selector}`);
      });

      ['.assignee', '.priority', '.status'].forEach((selector) => {
        const $field = $el.find(`${selector} div.edit-button .rsa-form-button`);
        assert.ok($field.text().trim(), `Expected to find non-empty button text in DOM for : ${selector}`);
      });
    });
  });

  test('Selecting the (unassigned) option from the assigne dropdown properly calls updateItem with a null value', async function(assert) {
    assert.expect(1);
    const exampleUser = { id: 'admin' };
    setState({
      respond: {
        users: {
          enabledUsers: [exampleUser]
        }
      }
    });
    this.set('info', {
      assignee: exampleUser
    });
    this.set('updateItem', (entityId, field, updatedValue) => {
      assert.equal(updatedValue, null, 'When unassigning an incident, the updatedValue is null');
    });
    await render(hbs`{{rsa-incident/overview info=info infoStatus='completed' updateItem=updateItem}}`);
    clickTrigger('.assignee');
    selectChoose('.assignee', '(Unassigned)');
  });
});
