import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render, settled } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { set } from '@ember/object';
import { run } from '@ember/runloop';
import Immutable from 'seamless-immutable';
import { incidentDetails } from '../../../../data/data';
import { getAllEnabledUsers } from 'respond-shared/actions/creators/create-incident-creators';

let setState;
const trim = (text) => text && text.replace(/\s\s+/g, ' ').trim() || undefined;

module('Integration | Component | Incident Overview', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    this.set('incidentDetails', incidentDetails);
    await render(hbs`{{rsa-incident/overview info=incidentDetails infoStatus='completed'}}`);
    return settled().then(() => {
      const elements = document.querySelectorAll('.rsa-incident-overview');
      assert.equal(elements.length, 1, 'Expected to find overview root element in DOM.');

      [ '.created', '.by', '.sources', '.catalyst-count' ].forEach((selector) => {
        const field = elements[0].querySelector(`${selector} span`);
        assert.ok(field.textContent.trim(), `Expected to find non-empty field element in DOM for: ${selector}`);
      });

      ['.assignee', '.priority', '.status'].forEach((selector) => {
        const field = elements[0].querySelector(`${selector} div.edit-button .rsa-form-button`);
        assert.ok(field.textContent.trim(), `Expected to find non-empty button text in DOM for : ${selector}`);
      });
    });
  });

  test('Assignee dropdown has current logged in user at the top', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    await redux.dispatch(getAllEnabledUsers());
    const accessControl = this.owner.lookup('service:accessControl');
    const component = this.owner.factoryFor('component:rsa-incident/overview').create();
    accessControl.set('username', '2');
    assert.ok(component.get('assigneeOptions')[0].name.includes('Myself'));
  });

  test('Selecting the (unassigned) option from the assignee dropdown properly calls updateItem with a null value', async function(assert) {
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
    await selectChoose('.assignee', '(Unassigned)');
  });

  test('The unassigned option from the assignee dropdown properly updates when locale is changed', async function(assert) {
    assert.expect(2);

    await render(hbs`{{rsa-incident/overview info=info infoStatus='completed'}}`);

    const unassigned = '(未割り当て)';
    const i18n = this.owner.lookup('service:i18n');
    run(i18n, 'addTranslations', 'ja-jp', { 'respond.assignee.none': unassigned });

    const selector = '.assignee .edit-button';
    assert.equal(trim(find(selector).textContent), '(Unassigned)');

    set(i18n, 'locale', 'ja-jp');

    return settled().then(async() => {
      assert.equal(trim(find(selector).textContent), unassigned);
    });
  });

  test('Assignee dropdown is disabled for non RIAC-Admins in RIAC mode', async function(assert) {
    const ac = this.owner.lookup('service:accessControl');
    const redux = this.owner.lookup('service:redux');
    const exampleUser = { id: 'admin' };
    setState({
      respond: {
        users: {
          enabledUsers: [exampleUser]
        }
      }
    });
    ac.set('authorities', ['Analysts']);
    await redux.dispatch({
      type: 'RESPOND::GET_RIAC_SETTINGS',
      promise: Promise.resolve({
        data: {
          enabled: true,
          adminRoles: ['Administrators']
        }
      })
    });
    this.set('info', {
      assignee: exampleUser
    });
    await render(hbs`{{rsa-incident/overview info=info infoStatus='completed' updateItem=updateItem}}`);
    assert.equal(findAll('.rsa-form-button-wrapper.is-disabled').length, 1,
      'When itemsSelected has at least one item in RIAC mode, change assignee disabled');
    assert.equal(findAll('.rsa-form-button-wrapper:not(.is-disabled)').length, 2,
      'When itemsSelected has at least one item in RIAC mode, only 2 options enabled');
  });
});
