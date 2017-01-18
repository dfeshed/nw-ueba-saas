/**
 * @file Configuration file for the list-view component.
 * @public
 */
import Ember from 'ember';

const {
  Object: EmberObject,
  computed,
  run,
  addObserver,
  removeObserver,
  isPresent,
  isEmpty
} = Ember;

// full list of columns to be used in the list-view
export const availableColumnsConfig = [
  EmberObject.create({
    title: '',
    class: 'rsa-form-row-checkbox',
    width: '30',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  }),
  EmberObject.create({
    field: 'id',
    title: 'incident.list.id',
    class: 'rsa-respond-list-incident-id',
    width: '70',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'name',
    title: 'incident.list.name',
    width: '400',
    class: 'rsa-respond-list-name',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'riskScore',
    title: 'incident.list.riskScore',
    dataType: 'custom',
    width: '100',
    class: 'rsa-respond-list-riskscore',
    componentClass: 'rsa-content-badge-score',
    isDescending: true,
    visible: true
  }),
  EmberObject.create({
    field: 'prioritySort',
    title: 'incident.list.priority',
    width: '80',
    class: 'rsa-respond-list-priority',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'statusSort',
    title: 'incident.list.status',
    width: '100',
    class: 'rsa-respond-list-status',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'created',
    title: 'incident.list.createdDate',
    width: '90',
    class: 'rsa-respond-list-created',
    dataType: 'date-time',
    componentClass: 'rsa-content-datetime',
    visible: true
  }),
  EmberObject.create({
    field: 'assigneeName',
    title: 'incident.list.assignee',
    width: '80',
    class: 'rsa-respond-list-assignee',
    dataType: 'text',
    visible: true
  }),
  EmberObject.create({
    field: 'alertCount',
    title: 'incident.list.alertCount',
    width: '50',
    class: 'rsa-respond-list-alertCount',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'sources',
    title: 'incident.list.sources',
    width: '100',
    class: 'rsa-respond-list-sources',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'eventCount',
    title: 'incident.fields.events',
    width: '50',
    class: 'rsa-respond-list-events',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'groupBySourceIp',
    title: 'incident.fields.groupBySourceIp',
    width: '100',
    class: 'rsa-respond-list-sourceIps',
    dataType: 'text',
    visible: false
  }),
  EmberObject.create({
    field: 'groupByDestinationIp',
    title: 'incident.fields.groupByDestinationIp',
    width: '100',
    class: 'rsa-respond-list-destinationIps',
    dataType: 'text',
    visible: false
  })
];

/**
Builds an array of Ember Objects for the replay manager @see sa/utils/replay-manager
Component builds this array on init.
Component context i.e. 'this' is assumed and expected when the function is executed.
Each array element provides the 'path' on the restoredState from which persisted value will be read and a computed property ('value') on which restored value will be set.
All values are computed properties.
These computed properties perform validation, some pre-processing logic if required, before setting restored values on component attributes.
value computed property is set by Replay Manager when replay method is invoked @see sa/utils/replay-manager replay
@returns {Array} array of Ember Objects
@public
*/

export function replayConfig() {

  return [
    EmberObject.extend({
      path: 'flags.currentSort',
      value: computed({
        set: (key, value) => {
          this.set('currentSort', value);
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'columns',
      value: computed({
        set: (key, values) => {
          if (values) {
            this.get('availableColumnsConfig').forEach((column) => {
              const field = column.get('field');
              if (isPresent(values[field].visible)) {
                column.set('visible', values[field].visible);
              }
              if (isPresent(values[field].isDescending)) {
                column.set('isDescending', values[field].isDescending);
              }
              if (isPresent(values[field].width)) {
                column.set('width', values[field].width);
              }
            });
          }
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'filters.priorities',
      value: computed({
        set: (key, values) => {
          if (!isEmpty(values)) {
            this.get('priorityList').forEach((filter) => {
              if (values.includes(filter.get('id'))) {
                filter.set('value', true);
              }
            });
          }
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'filters.statuses',
      value: computed({
        set: (key, values) => {
          if (!isEmpty(values)) {
            this.get('statusList').forEach((filter) => {
              if (values.includes(filter.get('id'))) {
                filter.set('value', true);
              }
            });
          }
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'filters.assignees',
      value: computed({
        set: (key, assigneeIds) => {
          if (!isEmpty(assigneeIds)) {
            const allUsers = this.get('usersList');
            if (!isEmpty(allUsers)) {
              const assignees = allUsers.filter((user) => assigneeIds.any((assigneeId) => user.id === assigneeId));
              this.set('selectedAssignees', assignees);
            }
          }
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'filters.categories',
      value: computed({
        set: (key, value) => {
          if (!isEmpty(value)) {
            this.set('selectedCategories', value);
          }
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'filters.sources',
      value: computed({
        set: (key, value) => {
          if (!isEmpty(value)) {
            this.set('selectedSources', value);
          }
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'filters.riskScores',
      value: computed({
        set: (key, value) => {
          if (!isEmpty(value)) {
            this.set('riskScoreStart', value);
          }
        }
      })
    }).create(),
    EmberObject.extend({
      path: 'filters.time',
      value: computed({
        set: (key, value) => {
          if (!isEmpty(value)) {
            this.set('filteredTime', value);
          }
        }
      })
    }).create()
  ];
}

/**
Builds an array of POJO for the Persistence Manager @ see sa/utils/persistence-manager
Component context i.e. 'this' is assumed and expected when the function is executed.
Each element of the array can implement the following methods
1) persistInitialState: if initial state of an attribute has to be persisted (for e.g. columns visible, width and sort order)
2) createObserver: If implemented, it is expected this method will invoke Ember addObserver providing appropriate key, target and method i.e 'execute'
3) removeObserver: If implemented, it is expected this method will invoke Ember removeObserver providing appropriate key, target and method i.e 'execute'
4) execute: this method should provide the core logic of persistence.
The above methods are executed by Persistence Manager as and when requested by the component.
for e.g. persistInitialState, createObservers and removeObservers in didReceiveAttrs component hook.
removeObservers in willDestroyElement component hook
@returns {Array} array of POJO
@public
*/
export function persistenceConfig() {

  const component = this;
  const configArray = [];

  configArray.pushObject({
    persistInitialState: () => {
      if (isPresent(this.get('currentSort'))) {
        this.get('persistence.state').persist('flags.currentSort', this.get('currentSort'));
      }
    }
  });

  this.get('availableColumnsConfig').forEach((column) => {

    configArray.pushObject({
      persistInitialState: () => {
        this.get('persistence.state').persist(`columns.${column.get('field')}.visible`, column.get('visible'));
      },
      execute: () => {
        run.once(() => {
          this.get('persistence.state').persist(`columns.${column.get('field')}.visible`, column.get('visible'));
        });
      },
      createObserver() {
        addObserver(column, 'visible', this.execute);
      },
      destroyObserver() {
        removeObserver(column, 'visible', this.execute);
      }
    });

    configArray.pushObject({
      persistInitialState: () => {
        this.get('persistence.state').persist(`columns.${column.get('field')}.width`, column.get('width'));
      },
      execute: () => {
        run.once(() => {
          this.get('persistence.state').persist(`columns.${column.get('field')}.width`, column.get('width'));
        });
      },
      createObserver() {
        addObserver(column, 'width', this.execute);
      },
      destroyObserver() {
        removeObserver(column, 'width', this.execute);
      }
    });

    configArray.pushObject({
      persistInitialState: () => {
        if (isPresent(column.get('isDescending'))) {
          this.get('persistence.state').persist(`columns.${column.get('field')}.isDescending`, column.get('isDescending'));
        }
      },
      execute: () => {
        run.once(() => {
          if (this.get('persistence.state.value.flags.currentSort') !== this.get('currentSort')) {
            const isRemoved = this.get('persistence.state').remove(`columns.${this.get('persistence.state.value.flags.currentSort')}.isDescending`, true);
            if (isRemoved) {
              this.get('persistence.state').persist('flags.currentSort', this.get('currentSort'));
            }
          }
          this.get('persistence.state').persist(`columns.${column.get('field')}.isDescending`, column.get('isDescending'));
        });
      },
      createObserver() {
        addObserver(column, 'isDescending', this.execute);
      },
      destroyObserver() {
        removeObserver(column, 'isDescending', this.execute);
      }
    });

  });

  configArray.pushObject({
    execute: () => {
      run.once(() => {
        this.get('persistence.state').persist('filters.statuses', this.get('filteredStatuses'));
      });
    },
    createObserver() {
      addObserver(component, 'statusList.@each.value', this.execute);
    },
    destroyObserver() {
      removeObserver(component, 'statusList.@each.value', this.execute);
    }
  });

  configArray.pushObject({
    execute: () => {
      run.once(() => {
        this.get('persistence.state').persist('filters.priorities', this.get('filteredPriorities'));
      });
    },
    createObserver() {
      addObserver(component, 'priorityList.@each.value', this.execute);
    },
    destroyObserver() {
      removeObserver(component, 'priorityList.@each.value', this.execute);
    }
  });

  configArray.pushObject({
    execute: () => {
      run.once(() => {
        const assignees = this.get('selectedAssignees');
        const assigneeIds = assignees.map((assignee) => assignee.id);
        this.get('persistence.state').persist('filters.assignees', assigneeIds);
      });
    },
    createObserver() {
      addObserver(component, 'selectedAssignees', this.execute);
    },
    destroyObserver() {
      removeObserver(component, 'selectedAssignees', this.execute);
    }
  });

  configArray.pushObject({
    execute: () => {
      run.once(() => {
        this.get('persistence.state').persist('filters.categories', this.get('filteredCategories'));
      });
    },
    createObserver() {
      addObserver(component, 'selectedCategories', this.execute);
    },
    destroyObserver() {
      removeObserver(component, 'selectedCategories', this.execute);
    }
  });

  configArray.pushObject({
    execute: () => {
      run.once(() => {
        this.get('persistence.state').persist('filters.sources', this.get('selectedSources'));
      });
    },
    createObserver() {
      addObserver(component, 'selectedSources', this.execute);
    },
    destroyObserver() {
      removeObserver(component, 'selectedSources', this.execute);
    }
  });
  configArray.pushObject({
    execute: () => {
      run.once(() => {
        this.get('persistence.state').persist('filters.riskScores', this.get('filteredRiskScores'));
      });
    },
    createObserver() {
      addObserver(component, 'filteredRiskScores', this.execute);
    },
    destroyObserver() {
      removeObserver(component, 'filteredRiskScores', this.execute);
    }
  });

  configArray.pushObject({
    execute: () => {
      run.once(() => {
        this.get('persistence.state').persist('filters.time', this.get('filteredTime'));
      });
    },
    createObserver() {
      addObserver(component, 'filteredTime', this.execute);
    },
    destroyObserver() {
      removeObserver(component, 'filteredTime', this.execute);
    }
  });

  return configArray;
}

/*
Storage key used for respond mode list view
*/
export const storageKey = 'rsa::securityAnalytics::respondModeList';
