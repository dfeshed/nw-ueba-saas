import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { waitFor } from 'sa/tests/integration/components/rsa-respond/landing-page/respond-index/list-view/helpers';
import selectors from 'sa/tests/selectors';

moduleForComponent('rsa-incidents-tab', 'Integration | Component | rsa-application-incident-queue-panel', {
  integration: true
});

test('it renders', function(assert) {

  this.render(hbs`{{rsa-application-incident-queue-panel}}`);

  let myIncidentsTab = this.$(selectors.pages.incident.myIncidentsBtn);
  let allIncidentsTab = this.$(selectors.pages.incident.allIncidentsBtn);
  let isActive = selectors.pages.respond.rsaActiveElmCheck;

  assert.equal(myIncidentsTab.length, 1, 'My Incident Tab is available for selection');
  assert.equal(allIncidentsTab.length, 1, 'All Incident Tab is available for selection');

  assert.ok(myIncidentsTab.hasClass(isActive), 'My Incident Tab is selected by default');
  assert.notOk(allIncidentsTab.hasClass(isActive), 'All Incident Tab is not selected by default');

  // Check incident count (default = 0)
  let incidents = this.$(selectors.pages.respond.rsaTableCell).find(selectors.pages.respond.card.rsaContentCard);
  assert.ok(incidents.length === 0, 'Incident does not exists');


});

test('Change tabs', function(assert) {
  const done = assert.async(1);

  this.render(hbs`{{rsa-application-incident-queue-panel}}`);

  // Note: This will trigger data retrieval via call to backend that must complete
  this.$(selectors.pages.incident.allIncidentsBtn).click();

  // Delay check to allow websocket call to return to prevent "calling set on destroyed object" exception
  waitFor(
    () => this.$(selectors.pages.respond.rsaLoader).length === 0
  ).then(() => {
    let myIncidentsTab = this.$(selectors.pages.incident.myIncidentsBtn);
    let allIncidentsTab = this.$(selectors.pages.incident.allIncidentsBtn);
    let isActive = selectors.pages.respond.rsaActiveElmCheck;
    assert.notOk(myIncidentsTab.hasClass(isActive), 'My Incident Tab is not selected');
    assert.ok(allIncidentsTab.hasClass(isActive), 'All Incident Tab is selected');
    done();
  });

});

test('Render with data', function(assert) {

  let incident = {
    'id': 'INC-2',
    'name': 'Suspected command and control communication with 4554mb.ru',
    'summary': 'Incident Summary',
    'priority': 'CRITICAL',
    'prioritySort': 3,
    'riskScore': 80,
    'status': 'NEW',
    'created': 1474091565378,
    'lastUpdated': 1474585318241,
    'assignee': null
  };

  let arr = [incident];
  this.set('incidents', arr);
  this.render(hbs`{{rsa-application-incident-queue-panel incidents=incidents}}`);

  let incidents = this.$(selectors.pages.respond.rsaTableCell).find(selectors.pages.respond.card.rsaContentCard);

  assert.ok(incidents.length === 1, 'Incident exists.');
});
