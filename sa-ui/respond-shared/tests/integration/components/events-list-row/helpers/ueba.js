import { findAll, find } from '@ember/test-helpers';
import { selectors, ueba } from './selectors';

const ENTITY_CLASS = 'entity';
const DATA_ENTITY_ID = 'data-entity-id';
const DATA_META_KEY = 'data-meta-key';

export const assertRowPresent = (assert) => {
  assert.equal(findAll(selectors.row).length, 1);
  assert.equal(findAll(selectors.uebaHeader).length, 1);
  assert.equal(findAll(selectors.uebaDetail).length, 0);
  assert.equal(findAll(selectors.caption).length, 0);
};

export const assertRowAlertDetails = (assert, { name, summary, score }) => {
  assert.equal(find(selectors.alertName).textContent.trim(), name);
  assert.equal(find(selectors.alertScore).textContent.trim(), score);
  assert.equal(find(selectors.eventSummary).textContent.trim(), summary);
};

export const assertRowHeaderContext = (assert, { username }) => {
  if (username) {
    assert.equal(find(ueba.eventUsernameValue).attributes[DATA_ENTITY_ID].nodeValue, username);
    assert.equal(find(ueba.eventUsernameValue).attributes[DATA_META_KEY].nodeValue, 'username');
    assert.equal(find(ueba.eventUsernameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(ueba.eventUsernameValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertRowHeader = (assert, { eventType, category, username, operationType, eventCode, result }) => {
  assert.equal(find(ueba.eventTimeLabel).textContent.trim(), 'EVENT TIME');
  assert.ok(find(ueba.eventTimeValue).textContent.trim() !== '');
  assert.equal(find(ueba.eventTypeLabel).textContent.trim(), 'EVENT TYPE');
  assert.equal(find(ueba.eventTypeValue).textContent.trim(), eventType);
  assert.equal(find(ueba.eventCategoryLabel).textContent.trim(), 'CATEGORY');
  assert.equal(find(ueba.eventCategoryValue).textContent.trim(), category);
  assert.equal(find(ueba.eventUsernameLabel).textContent.trim(), 'USERNAME');
  assert.equal(find(ueba.eventUsernameValue).textContent.trim(), username);
  assert.equal(find(ueba.eventOperationTypeLabel).textContent.trim(), 'OPERATION TYPE');
  assert.equal(find(ueba.eventOperationTypeValue).textContent.trim(), operationType);
  assert.equal(find(ueba.eventCodeLabel).textContent.trim(), 'EVENT CODE');
  assert.equal(find(ueba.eventCodeValue).textContent.trim(), eventCode);
  assert.equal(find(ueba.eventResultLabel).textContent.trim(), 'RESULT');
  assert.equal(find(ueba.eventResultValue).textContent.trim(), result);
};
