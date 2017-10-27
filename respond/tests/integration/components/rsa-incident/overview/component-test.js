import { moduleForComponent, test } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import wait from 'ember-test-helpers/wait';
import { incidentDetails } from '../../../../data/data';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';

let setState;

moduleForComponent('rsa-incident-overview', 'Integration | Component | Incident Overview', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(Immutable.from(state));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('it renders', function(assert) {
  this.set('incidentDetails', incidentDetails);
  this.render(hbs`{{rsa-incident/overview info=incidentDetails infoStatus='completed'}}`);
  return wait().then(() => {
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

test('Selecting the (unassigned) option from the assigne dropdown properly calls updateItem with a null value', function(assert) {
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
  this.render(hbs`{{rsa-incident/overview info=info infoStatus='completed' updateItem=updateItem}}`);
  clickTrigger('.assignee');
  selectChoose('.assignee', '(Unassigned)');
});