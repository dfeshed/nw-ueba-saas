import { lookup } from 'ember-dependency-lookup';

/**
 * Making api call to fetch all the springboard
 * @returns {RSVP.Promise}
 */
export const getAllSpringboards = () => {
  return new Promise((resolve) => setTimeout(() => resolve(getDefaultSpringboard()), 1000));
};

export const widgetQuery = (widget) => {
  const request = lookup('service:request');
  const modelName = 'springboard';
  const method = 'query';
  return request.promiseRequest({
    method,
    modelName,
    query: {
      data: {
        leadType: widget.leadType,
        size: widget.leadCount,
        sort: { keys: ['score'], descending: true }
      }
    }
  });
};

// This will be replaced by backend code, will move this to backend once we commit the config property
const getDefaultSpringboard = () => {
  return {
    data: {
      items: [
        {
          id: 'spring_board_1',
          name: 'Analyst Springboard',
          widgets: [
            {
              columnIndex: 1,
              widget: {
                name: 'Top Risky Hosts',
                leadType: 'HOST',
                leadCount: 25,
                deepLink: {
                  location: 'HOST_LIST'
                },
                content: [
                  {
                    type: 'chart',
                    chartType: 'donut-chart',
                    aggregate: {
                      columns: ['machineIdentity.machineOsType'],
                      type: 'COUNT'
                    },
                    extraCss: 'flexi-fit'
                  },
                  {
                    type: 'table',
                    deepLink: {
                      location: 'HOST_DETAILS',
                      params: ['machineIdentity.id', 'serviceId']
                    },
                    columns: ['machineIdentity.machineName', 'score', 'machineIdentity.machineOsType'],
                    sort: {
                      keys: ['score'],
                      descending: true
                    }
                  }
                ]
              }
            },
            {
              columnIndex: 1,
              widget: {
                name: 'Top Risky Files',
                leadType: 'FILE',
                leadCount: 25,
                content: [
                  {
                    type: 'chart',
                    chartType: 'donut-chart',
                    aggregate: {
                      columns: ['reputationStatus'],
                      type: 'COUNT'
                    },
                    extraCss: 'flexi-fit'
                  },
                  {
                    type: 'table',
                    columns: ['firstFileName', 'score', 'hostCount'],
                    sort: {
                      keys: ['score'],
                      descending: true
                    }
                  }
                ]
              }
            }
          ]
        }
      ]
    }
  };
};