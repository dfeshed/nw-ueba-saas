export const getQueryNode = function(input, selectedNode) {
  const { et, st, pn, sid, aid: agentId } = input;

  let processName = pn;

  if (selectedNode) {
    processName = selectedNode;
  }

  const queryNode = {
    endTime: et,
    startTime: st,
    queryTimeFormat: 'DB',
    serviceId: sid,
    metaFilter: _getMetaFilter(agentId, processName)
  };
  return queryNode;
};
/**
 * To construct a process tree need to get all the createProcess event for given host and a process and it should include
 * only endpoint related events. Always query will contain fixed set of meta, agent.id, action, type and file name
 *
 * agent.id -> for a given host
 * action -> to get only createProcess events
 * device.type -> to get only endpoint related events
 * fileName.src -> selected process
 *
 * @param agentId
 * @param processName
 * @returns {{conditions: *[]}}
 * @private
 */
const _getMetaFilter = (agentId, processName) => {
  return {
    conditions: [
      {
        meta: 'agent.id',
        operator: '=',
        value: `'${agentId}'`
      },
      {
        meta: 'action',
        operator: '=',
        value: '\'createProcess\''
      },
      {
        meta: 'device.type',
        operator: '=',
        value: '\'nwendpoint\''
      },
      {
        meta: 'filename.src',
        operator: '=',
        value: `'${processName}'`
      }
    ]
  };
};
