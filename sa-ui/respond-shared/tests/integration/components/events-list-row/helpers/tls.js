import { findAll, find } from '@ember/test-helpers';
import { selectors, tls } from './selectors';

const ENTITY_CLASS = 'entity';
const DATA_ENTITY_ID = 'data-entity-id';
const DATA_META_KEY = 'data-meta-key';

export const assertRowPresent = (assert) => {
  assert.equal(findAll(selectors.row).length, 1);
  assert.equal(findAll(selectors.tlsHeader).length, 1);
  assert.equal(findAll(selectors.tlsDetail).length, 0);
  assert.equal(findAll(selectors.caption).length, 1);
};

export const assertRowAlertDetails = (assert, { name, summary, score }) => {
  assert.equal(find(selectors.alertName).textContent.trim(), name);
  assert.equal(find(selectors.alertScore).textContent.trim(), score);
  assert.equal(find(selectors.eventSummary).textContent.trim(), summary);
};

export const assertRowHeaderContext = (assert, { username }) => {
  if (username) {
    assert.equal(find(tls.eventUsernameValue).attributes[DATA_ENTITY_ID].nodeValue, username);
    assert.equal(find(tls.eventUsernameValue).attributes[DATA_META_KEY].nodeValue, 'username');
    assert.equal(find(tls.eventUsernameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(tls.eventUsernameValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertRowHeader = (assert, { eventType, category, sslSubject, sslCa }) => {
  assert.equal(find(tls.eventTimeLabel).textContent.trim(), 'EVENT TIME');
  assert.ok(find(tls.eventTimeValue).textContent.trim() !== '');
  assert.equal(find(tls.eventTypeLabel).textContent.trim(), 'EVENT TYPE');
  assert.equal(find(tls.eventTypeValue).textContent.trim(), eventType);
  assert.equal(find(tls.eventCategoryLabel).textContent.trim(), 'CATEGORY');
  assert.equal(find(tls.eventCategoryValue).textContent.trim(), category);
  assert.equal(find(tls.eventSslSubjectLabel).textContent.trim(), 'SSL SUBJECT');
  assert.equal(find(tls.eventSslSubjectValue).textContent.trim(), sslSubject);
  assert.equal(find(tls.eventSslCasLabel).textContent.trim(), 'SSL CAs');
  assert.equal(find(tls.eventSslCasValue).textContent.trim(), sslCa);
};

export const assertTableColumns = (assert) => {
  assert.equal(find(tls.eventTableIpLabel).textContent.trim(), 'IP');
  assert.equal(find(tls.eventTablePortLabel).textContent.trim(), 'PORT');
  assert.equal(find(tls.eventTableCountryLabel).textContent.trim(), 'COUNTRY');
  assert.equal(find(tls.eventTableJa3Label).textContent.trim(), 'JA3/JA3S');
};

export const assertTableSource = (assert, { ip, port, country, ja3 }) => {
  assert.equal(find(tls.eventSourceLabel).textContent.trim(), 'SOURCE');
  assert.equal(find(tls.eventSourceIpValue).textContent.trim(), ip);
  assert.equal(find(tls.eventSourcePortValue).textContent.trim(), port);
  assert.equal(find(tls.eventSourceCountryValue).textContent.trim(), country);
  assert.equal(find(tls.eventSourceJa3Value).textContent.trim(), ja3);
};

export const assertTableTarget = (assert, { ip, port, country, ja3 }) => {
  assert.equal(find(tls.eventTargetLabel).textContent.trim(), 'TARGET');
  assert.equal(find(tls.eventTargetIpValue).textContent.trim(), ip);
  assert.equal(find(tls.eventTargetPortValue).textContent.trim(), port);
  assert.equal(find(tls.eventTargetCountryValue).textContent.trim(), country);
  assert.equal(find(tls.eventTargetJa3Value).textContent.trim(), ja3);
};