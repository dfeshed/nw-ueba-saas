import { findAll, find } from '@ember/test-helpers';
import { selectors, process } from './selectors';

const ENTITY_CLASS = 'entity';
const DATA_ENTITY_ID = 'data-entity-id';
const DATA_META_KEY = 'data-meta-key';

export const assertRowPresent = (assert) => {
  assert.equal(findAll(selectors.row).length, 1);
  assert.equal(findAll(selectors.processHeader).length, 1);
  assert.equal(findAll(selectors.processDetail).length, 0);
  assert.equal(findAll(selectors.caption).length, 1);
};

export const assertRowAlertDetails = (assert, { name, summary, score }) => {
  assert.equal(find(selectors.alertName).textContent.trim(), name);
  assert.equal(find(selectors.alertScore).textContent.trim(), score);
  assert.equal(find(selectors.eventSummary).textContent.trim(), summary);
};

export const assertRowHeaderContext = (assert, { username }) => {
  if (username) {
    assert.equal(find(process.eventUsernameValue).attributes[DATA_ENTITY_ID].nodeValue, username);
    assert.equal(find(process.eventUsernameValue).attributes[DATA_META_KEY].nodeValue, 'username');
    assert.equal(find(process.eventUsernameValue).classList.contains(ENTITY_CLASS), true);
  } else {
    assert.equal(find(process.eventUsernameValue).classList.contains(ENTITY_CLASS), false);
  }
};

export const assertRowHeader = (assert, { eventType, category, username, operationType, dataSource }) => {
  assert.equal(find(process.eventTimeLabel).textContent.trim(), 'EVENT TIME');
  assert.ok(find(process.eventTimeValue).textContent.trim() !== '');
  assert.equal(find(process.eventTypeLabel).textContent.trim(), 'EVENT TYPE');
  assert.equal(find(process.eventTypeValue).textContent.trim(), eventType);
  assert.equal(find(process.eventCategoryLabel).textContent.trim(), 'CATEGORY');
  assert.equal(find(process.eventCategoryValue).textContent.trim(), category);
  assert.equal(find(process.eventUsernameLabel).textContent.trim(), 'USERNAME');
  assert.equal(find(process.eventUsernameValue).textContent.trim(), username);
  assert.equal(find(process.eventOperationTypeLabel).textContent.trim(), 'OPERATION TYPE');
  assert.equal(find(process.eventOperationTypeValue).textContent.trim(), operationType);
  assert.equal(find(process.eventDataSourceLabel).textContent.trim(), 'DATA SOURCE');
  assert.equal(find(process.eventDataSourceValue).textContent.trim(), dataSource);
};
