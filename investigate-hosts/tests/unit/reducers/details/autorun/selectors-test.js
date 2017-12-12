import { module, test } from 'qunit';
import { autorunsData } from '../../../state/state';
import { fileContextAutorunsSchema, fileContextServicesSchema, fileContextTasksSchema } from 'investigate-hosts/reducers/details/autorun/schemas';
import Immutable from 'seamless-immutable';
import { normalize } from 'normalizr';

module('Unit | Selectors | autorun');

import {
  tasks,
  autoruns,
  services
} from 'investigate-hosts/reducers/details/autorun/selectors';

test('autoruns', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextAutorunsSchema);
  const { autorun } = normalizedData.entities;
  const result = autoruns(Immutable.from({
    endpoint: {
      autoruns: {
        autorun
      },
      explore: {
      }
    }
  }));
  assert.equal(result.length, 1);
});

test('services', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextServicesSchema);
  const { service } = normalizedData.entities;
  const result = services(Immutable.from({
    endpoint: {
      autoruns: {
        service
      },
      explore: {
      }
    }

  }));
  assert.equal(result.length, 1);
});

test('tasks', function(assert) {
  const normalizedData = normalize(autorunsData, fileContextTasksSchema);
  const { task } = normalizedData.entities;
  const result = tasks(Immutable.from({
    endpoint: {
      autoruns: {
        task
      },
      explore: {
      }
    }
  }));
  assert.equal(result.length, 2);
});
