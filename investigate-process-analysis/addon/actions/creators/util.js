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
