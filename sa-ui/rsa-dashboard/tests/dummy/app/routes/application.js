import Route from '@ember/routing/route';

export default Route.extend({

  model() {
    return {
      dashboardConfig: {
        columnOne: {
          topRiskMachine: {
            id: 'topRiskMachine',
            title: 'Top Risky Machine',
            displayMapping: {
              name: {
                dataIndex: 'machine.machineName'
              },
              description: {
                dataIndex: 'machine.machineOsType'
              },
              additionalInfo: {
                label: 'Risk Score Updated:',
                dataIndex: 'machine.machineOsType'
              }
            },
            data: [

              {
                score: 100,
                machine: {
                  machineName: 'Test',
                  machineOsType: 'Windows'
                }
              },
              {
                score: 100,
                machine: {
                  machineName: 'Test Machine',
                  machineOsType: 'Windows'
                }
              },
              {
                score: 100,
                machine: {
                  machineName: 'Test',
                  machineOsType: 'Windows'
                }
              },
              {
                score: 100,
                machine: {
                  machineName: 'Test',
                  machineOsType: 'Windows'
                }
              },
              {
                score: 100,
                machine: {
                  machineName: 'Test',
                  machineOsType: 'Windows'
                }
              }
            ]
          }

        },
        columnTwo: {

          entityStats: {
            id: 'entityStats',
            title: 'Stats',
            items: [
              {
                dataIndex: 'machineOfflineCount'
              },
              {
                dataIndex: 'criticalHosts'
              },
              {
                dataIndex: 'machineOfflineCount'
              },
              {
                dataIndex: 'machineOfflineCount'
              }
            ],
            data: {
              machineOfflineCount: 100,
              criticalHosts: 2,
              mediumHosts: 10,
              onlineMachine: 10
            }
          },
          topAlerts: {
            id: 'topAlerts',
            title: 'Top Alerts',
            data: [
              {
                alert: {
                  name: 'Test',
                  host_summary: 'Test Host',
                  severity: 97
                }
              },
              {
                alert: {
                  name: 'Test',
                  host_summary: 'Test Host',
                  severity: 97
                }
              },
              {
                alert: {
                  name: 'Test',
                  host_summary: 'Test Host',
                  severity: 97
                }
              },
              {
                alert: {
                  name: 'Test Machine in bangalore office',
                  host_summary: 'Test Host',
                  severity: 97
                }
              },
              {
                alert: {
                  name: 'Test',
                  host_summary: 'Test Host',
                  severity: 97
                }
              },
              {
                alert: {
                  name: 'Test',
                  host_summary: 'Test Host',
                  severity: 97
                }
              },
              {
                alert: {
                  name: 'Test',
                  host_summary: 'Test Host',
                  severity: 97
                }
              }
            ],
            displayMapping: {
              alertName: {
                dataIndex: 'alert.name'
              },
              alertSummary: {
                dataIndex: 'alert.host_summary'
              },
              severity: {
                label: 'Severity',
                dataIndex: 'alert.severity'
              }
            }
          }
        }
      }
    };
  }
});
